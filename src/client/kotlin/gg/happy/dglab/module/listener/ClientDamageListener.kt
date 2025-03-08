package gg.happy.dglab.module.listener

import gg.happy.dglab.module.outputer.OutputterManager
import kotlin.math.max

object ClientDamageListener
{
    var lastHealth = -1.0F
    var lastAbsorption = -1.0F
    var lowestHealth = Float.MAX_VALUE

    @JvmStatic
    fun onTick(health: Float, absorption: Float)
    {
        if (health < lowestHealth)
            lowestHealth = health

        OutputterManager.onClientDamage(
            max(lastHealth - health, 0.0F) + max(lastAbsorption - absorption, 0.0F)
        )

        lastHealth = health
        lastAbsorption = absorption
        lowestHealth = Float.MAX_VALUE
    }

    @JvmStatic
    fun onHealthUpdate(health: Float)
    {
        if (health < lowestHealth)
            lowestHealth = health
    }
}