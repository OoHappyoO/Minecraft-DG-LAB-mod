package gg.happy.dglab.module.outputer

import gg.happy.dglab.module.Conf
import gg.happy.dglab.module.api.ChannelType
import me.shedaniel.autoconfig.AutoConfig

object OutputterManager
{
    private val conf: Conf = AutoConfig.getConfigHolder(Conf::class.java).config

    val outputterA = Outputter(ChannelType.A, { conf.pulse.a }, conf.webSocket.message)
    val outputterB = Outputter(ChannelType.B, { conf.pulse.b }, conf.webSocket.message)

    private val outputters = setOf(outputterA, outputterB)

    var useServerDetection = false

    @JvmStatic
    fun onServerDamage(damage: Float, raw: Float)
    {
        outputters.forEach {
            it.onDamage((if (conf.pulse.others.rawDamageInput) raw else damage).toDouble())
        }
        useServerDetection = true
    }

    fun onClientDamage(damage: Float)
    {
        if (useServerDetection)
            return
        if (damage != 0.0F)
            outputters.forEach {
                it.onDamage(damage.toDouble())
            }
    }

    fun initJob() =
        outputters.forEach { it.initJob() }

    fun stopJob() =
        outputters.forEach { it.stopJob() }

    @JvmStatic
    fun onDeath() =
        outputters.forEach { it.onDeath() }

    @JvmStatic
    fun onTotemPop() =
        outputters.forEach { it.onTotemPop() }

}