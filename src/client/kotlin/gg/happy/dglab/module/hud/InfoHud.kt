package gg.happy.dglab.module.hud

import gg.happy.dglab.DGLABClient
import gg.happy.dglab.module.Conf
import gg.happy.dglab.module.Strength
import gg.happy.dglab.module.outputer.OutputterManager
import me.shedaniel.autoconfig.AutoConfig
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier

object InfoHud
{
    private val conf = AutoConfig.getConfigHolder(Conf::class.java).config
    private val mc = DGLABClient.mc

    private val LAYER: Identifier = Identifier.of("dg-lab", "info-hud-layer")

    private val A = OutputterManager.outputterA
    private val B = OutputterManager.outputterB

    fun register()
    {
        HudLayerRegistrationCallback.EVENT.register {
            it.attachLayerBefore(
                IdentifiedLayer.CHAT,
                LAYER,
                ::render
            )
        }
    }

    fun render(context: DrawContext, tickCounter: RenderTickCounter)
    {
        if (!conf.hud.enabled)
            return
        context.drawTextWithShadow(
            mc.textRenderer,
            "A: ${A.percent}% * ${Strength.aCurrentStrength}",
            conf.hud.x,
            conf.hud.y,
            0xFFFFFF
        )
        context.drawTextWithShadow(
            mc.textRenderer,
            "B: ${B.percent}% * ${Strength.bCurrentStrength}",
            conf.hud.x,
            conf.hud.y + 10,
            0xFFFFFF
        )
    }
}
