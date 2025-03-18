package gg.happy.dglab.module.hud

import gg.happy.dglab.util.QRCodeUtil
import me.x150.renderer.util.RendererUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier

object QRCodeHud : HudAdapter("qr-code")
{
    private val identifier = Identifier.of("dg-lab", "qr-code")

    var url = ""
        set(value)
        {
            RendererUtils.registerBufferedImageTexture(identifier, QRCodeUtil.generateQRCode(value))
            field = value
        }

    override fun render(context: DrawContext, tickCounter: RenderTickCounter)
    {
        if (!enabled)
            return
        context.fill(
            conf.hud.qrCode.x + 1,
            conf.hud.qrCode.y + 1,
            conf.hud.qrCode.x + 257,
            conf.hud.qrCode.y + 257,
            conf.hud.qrCode.shadowColor
        )
        context.drawTexture(
            RenderLayer::getGuiTextured,
            identifier,
            conf.hud.qrCode.x,
            conf.hud.qrCode.y,
            0.0f,
            0.0f,
            256,
            256,
            256,
            256
        )
    }
}