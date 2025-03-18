package gg.happy.dglab.module.hud

import gg.happy.dglab.DGLABClient
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier

abstract class HudAdapter(id: String)
{
    val conf = DGLABClient.conf
    val mc = DGLABClient.mc

    private val layer: Identifier = Identifier.of("dg-lab", "$id-layer")

    var enabled = false

    open fun register()
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