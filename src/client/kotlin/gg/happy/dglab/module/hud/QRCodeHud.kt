package gg.happy.dglab.module.hud

import gg.happy.dglab.DGLABClient
import gg.happy.dglab.util.QRCodeUtil
import me.x150.renderer.util.RendererUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier

object QRCodeHud : HudAdapter("qr-code")
{
    private val identifier = Identifier.of("dg-lab", "qr-code")
    
    private val qrCode = conf.hud.qrCode

    var url = ""
        set(value)
        {
            RendererUtils.registerBufferedImageTexture(identifier, QRCodeUtil.generateQRCode(value, qrCode.size))
            field = value
        }

    override fun render(context: DrawContext, tickCounter: RenderTickCounter)
    {
        if (!enabled)
            return
        context.fill(
            qrCode.x + 1,
            qrCode.y + 1,
            qrCode.x+ qrCode.size + 1,
            qrCode.y+ qrCode.size + 1,
            qrCode.shadowColor
        )
        context.drawTexture(
            RenderLayer::getGuiTextured,
            identifier,
            qrCode.x,
            qrCode.y,
            0.0f,
            0.0f,
            qrCode.size,
            qrCode.size,
            qrCode.size,
            qrCode.size
        )
    }
}