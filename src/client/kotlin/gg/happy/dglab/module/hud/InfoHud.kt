package gg.happy.dglab.module.hud

import gg.happy.dglab.module.Strength
import gg.happy.dglab.module.outputer.OutputterManager
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

object InfoHud : HudAdapter("info-hud")
{
    private val A = OutputterManager.outputterA
    private val B = OutputterManager.outputterB

    override fun render(context: DrawContext, tickCounter: RenderTickCounter)
    {
        if (!conf.hud.info.enabled || !enabled)
            return
        context.drawTextWithShadow(
            mc.textRenderer,
            "A: ${A.strength}% * ${Strength.aCurrentStrength}",
            conf.hud.info.x,
            conf.hud.info.y,
            0xFFFFFF
        )
        context.drawTextWithShadow(
            mc.textRenderer,
            "B: ${B.strength}% * ${Strength.bCurrentStrength}",
            conf.hud.info.x,
            conf.hud.info.y + 10,
            0xFFFFFF
        )
    }
}
