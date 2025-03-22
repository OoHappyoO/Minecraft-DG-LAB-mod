package gg.happy.dglab.module.outputer

import gg.happy.dglab.DGLABClient
import gg.happy.dglab.module.Conf
import gg.happy.dglab.module.Server
import gg.happy.dglab.module.api.ChannelType
import gg.happy.dglab.module.outputer.Outputter.Data.Companion.copy
import gg.happy.dglab.util.PulseUtil
import kotlinx.coroutines.*
import kotlin.math.pow
import kotlin.math.round

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
                val frequencies = mutableListOf<Int>()
                val strengths = mutableListOf<Int>()
                for (i in 0 until messageConf.length * 4)
                {
                    if (data.isIgnorable)
                        break
                    frequencies.add(data.frequency)
                    strengths.add(data.intStrength)
                    data.iterate()
                }
                Server.addPulse(type, PulseUtil.pulse(frequencies, strengths))
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
        get() = data.intStrength

    class Data(
        private val confGetter: () -> Conf.Pulse,
        strength: Double = 0.0,
        buffer: Double = 0.0,
        private var crest: Double = 0.0
    )
    {
        companion object
        {
            fun Data.copy(): Data = Data(confGetter, strength, buffer, crest)
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
            val delta = buffer * conf.increaseRate - strength * conf.decreaseRate
            strength += delta
            if (delta > 0)
                crest = strength.coerceIn(0.0, 1.0)
            buffer *= (1.0 - conf.increaseRate)
        }

        val intStrength
            get() = (strength * 100).toInt().coerceIn(0, 100)

        val frequency
            get() = round(
                (strength / crest).coerceIn(0.0, 1.0)
                    .let { conf.frequency.from * it + conf.frequency.to * (1.0 - it) }
            ).toInt()

        fun reset()
        {
            strength = 0.0
            buffer = 0.0
            crest = 0.0
        }

        val isIgnorable get() = strength < 0.01 && buffer < 0.01
    }
}