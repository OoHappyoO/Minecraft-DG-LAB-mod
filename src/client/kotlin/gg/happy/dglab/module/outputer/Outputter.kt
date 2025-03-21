package gg.happy.dglab.module.outputer

import gg.happy.dglab.DGLABClient
import gg.happy.dglab.module.Conf
import gg.happy.dglab.module.Server
import gg.happy.dglab.module.api.ChannelType
import gg.happy.dglab.module.outputer.Outputter.Data.Companion.copy
import gg.happy.dglab.util.PulseUtil
import kotlinx.coroutines.*
import kotlin.math.pow

class Outputter(
    private val type: ChannelType,
    private val confGetter: () -> Conf.Pulse,
    private val messageConf: Conf.WebSocket.Message,
)
{
    private val conf get() = confGetter()

    private val data = Data(confGetter)

    private var isChanged = false

    private var updateJob: Job? = null

    private var outputJob: Job? = null

    fun initJob()
    {
        data.reset()
        isChanged = false
        updateJob?.cancel()
        updateJob = DGLABClient.scope.launch {
            var next = System.currentTimeMillis()
            while (isActive)
            {
                data.iterate()
                if (isChanged)
                    resetOutputJob()
                next += 25
                delay(next - System.currentTimeMillis())
            }
        }
    }

    private suspend fun resetOutputJob() = coroutineScope {
        isChanged = false
        outputJob?.cancelAndJoin()
        outputJob = DGLABClient.scope.launch {
            val data = data.copy()
            var next = System.currentTimeMillis() - messageConf.lagCompensation
            Server.cleanPulse(type)
            while (!data.isIgnorable)
            {
                val strengths = mutableListOf<Int>().apply {
                    repeat(messageConf.length * 4) {
                        if (data.isIgnorable)
                            return@apply
                        add(data.getIntStrength())
                        data.iterate()
                    }
                }
                Server.addPulse(type, PulseUtil.pulse(conf.frequency, strengths))
                next += messageConf.length * 100
                delay(next - System.currentTimeMillis())
            }
        }
    }

    fun stopJob()
    {
        updateJob?.cancel()
        outputJob?.cancel()
    }

    fun onDamage(damage: Double)
    {
        data.buffer += (damage / 20).pow(conf.compressor) * conf.multiplier
        isChanged = true
    }

    fun onDeath()
    {
        data.buffer += conf.onEvent.onDeath
        isChanged = true
    }

    fun onTotemPop()
    {
        data.buffer += conf.onEvent.onTotemPop
        isChanged = true
    }

    val strength: Int
        get() = data.getIntStrength()

    class Data(
        private val confGetter: () -> Conf.Pulse,
        strength: Double = 0.0,
        buffer: Double = 0.0
    )
    {
        companion object
        {
            fun Data.copy(): Data = Data(confGetter, strength, buffer)
        }

        private val conf get() = confGetter()

        var strength = strength
            set(value)
            {
                field = value.coerceIn(0.0, conf.maximum)
            }

        var buffer = buffer
            set(value)
            {
                field = value.coerceIn(0.0, conf.maximum)
            }

        fun iterate()
        {
            strength = strength * (1.0 - conf.decreaseRate) + buffer * conf.increaseRate
            buffer *= (1.0 - conf.increaseRate)
        }

        fun getIntStrength() =
            (strength * 100).toInt().coerceIn(0, 100)

        fun reset()
        {
            strength = 0.0
            buffer = 0.0
        }

        val isIgnorable get() = strength < 0.01 && buffer < 0.01
    }
}