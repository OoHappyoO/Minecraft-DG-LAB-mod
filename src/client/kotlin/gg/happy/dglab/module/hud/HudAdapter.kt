package gg.happy.dglab.module.hud

import gg.happy.dglab.DGLABClient
import gg.happy.dglab.module.api.Registrable
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier

abstract class HudAdapter(id: String) : Registrable
{
    val conf = DGLABClient.conf
    val mc = DGLABClient.mc

    private val layer: Identifier = Identifier.of("dg-lab", "$id-layer")

    var enabled = false

    override fun register()
    {
        HudLayerRegistrationCallback.EVENT.register {
            it.attachLayerBefore(
                IdentifiedLayer.CHAT,
                layer,
                ::render
            )
        }
    }

    abstract fun render(context: DrawContext, tickCounter: RenderTickCounter)
}