package gg.happy.dglab.module.outputer

import gg.happy.dglab.DGLABClient
import gg.happy.dglab.module.Conf
import gg.happy.dglab.module.Server
import gg.happy.dglab.module.api.ChannelType
import gg.happy.dglab.util.PulseUtil
import kotlinx.coroutines.*
import kotlin.math.pow

class Outputter(
    private val type: ChannelType,
    private val conf: Conf.Pulse,
    private val messageConf: Conf.WebSocket.Message
)
{
    private val data = Data(conf)

    private var isChanged = false

    private var updateJob: Job? = null

    private var outputJob: Job? = null

    fun initJob()
    {
        data.reset()
        isChanged = false
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
        outputJob?.cancelAndJoin()
        isChanged = false
        outputJob = DGLABClient.scope.launch {
            val data = Data.copy(data)
            var next = System.currentTimeMillis() - messageConf.lagCompensation
            Server.cleanPulse(type)
            while (!data.isIgnorable)
            {
                val strengths = mutableListOf<Int>().apply {
                    repeat(messageConf.length * 4) {
                        if (data.isIgnorable)
                            return@apply
                        add(data.getIntPercent())
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
        data.buffer += (damage / 20).pow(conf.compressor) * conf.multiple
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

    val percent: Int
        get() = data.getIntPercent()

    class Data(
        private val conf: Conf.Pulse,
        percent: Double = 0.0, //TODO RENAME
        buffer: Double = 0.0
    )
    {
        companion object
        {
            fun copy(data: Data): Data = Data(data.conf, data.percent, data.buffer)
        }

        var percent = percent
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
            percent = percent * (1.0 - conf.decreaseRate) + buffer * conf.increaseRate
            buffer *= (1.0 - conf.increaseRate)
        }

        fun getIntPercent() =
            (percent * 100).toInt().coerceIn(0, 100)

        fun reset()
        {
            percent = 0.0
            buffer = 0.0
        }

        val isIgnorable get() = percent < 0.01 && buffer < 0.01
    }
}