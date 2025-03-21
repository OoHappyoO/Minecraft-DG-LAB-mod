package gg.happy.dglab.module.listener

import gg.happy.dglab.module.api.Registrable
import gg.happy.dglab.module.outputer.OutputterManager
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents

object JoinListener : Registrable
{
    override fun register()
    {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            OutputterManager.useServerDetection = false
            ClientDamageListener.run {
                lastHealth = -1.0F
                lastAbsorption = -1.0F
                lowestHealth = Float.MAX_VALUE
            }
        }
    }
}