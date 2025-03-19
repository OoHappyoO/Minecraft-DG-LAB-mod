package gg.happy.dglab.module

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import gg.happy.dglab.DGLABClient
import gg.happy.dglab.module.api.ChannelType
import gg.happy.dglab.module.hud.QRCodeHud
import gg.happy.dglab.util.QRCodeUtil
import gg.happy.dglab.util.getAddressAutoly
import me.shedaniel.autoconfig.AutoConfig
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

object Command
{
    private val conf = DGLABClient.conf

    fun register() =
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("dglab")
                    .then(literal("connect").executes { context ->
                        if (!Server.isRunning)
                            Server.start()
                        val address =
                            if (conf.webSocket.address.uppercase() == "AUTO")
                                getAddressAutoly().also {
                                    if (it == null)
                                    {
                                        context.source.sendFeedback(Text.translatable("text.dg-lab.cannot-get-address"))
                                        return@executes 1
                                    }
                                }
                            else
                                conf.webSocket.address
                        val url =
                            "https://www.dungeon-lab.com/app-download.php#DGLAB-SOCKET#${if (conf.webSocket.useHttps) "wss" else "ws"}://${address}:${conf.webSocket.port}/${Server.clientID}"
                        if (conf.hud.qrCode.enabled)
                        {
                            QRCodeHud.url = url
                            QRCodeHud.enabled = true
                        }
                        else
                            QRCodeUtil.generateQRCodeThenOpen(url)
                        context.source.sendFeedback(Text.translatable("text.dg-lab.scan"))
                        return@executes 1
                    })
                    .then(literal("disconnect").executes { context ->
                        if (Server.isConnected)
                            Server.disconnect()
                        else
                        {
                            QRCodeHud.enabled = false
                            context.source.sendFeedback(Text.translatable("text.dg-lab.isn't-connected"))
                        }
                        return@executes 1
                    })
                    .then(
                        literal("setStrength")
                            .then(
                                literal("A").then(argument("value", IntegerArgumentType.integer()).executes { context ->
                                    commandSetStrength(ChannelType.A, context)
                                    return@executes 1
                                })
                            )
                            .then(
                                literal("B").then(argument("value", IntegerArgumentType.integer()).executes { context ->
                                    commandSetStrength(ChannelType.B, context)
                                    return@executes 1
                                })
                            )
                    )
                    .executes { _ ->
                        MinecraftClient.getInstance().send {
                            val configScreen: Screen = AutoConfig.getConfigScreen(
                                Conf::class.java,
                                MinecraftClient.getInstance().currentScreen
                            ).get()
                            MinecraftClient.getInstance().setScreen(configScreen)
                        }
                        return@executes 1
                    }
            )
        }

    private fun commandSetStrength(type: ChannelType, context: CommandContext<FabricClientCommandSource>)
    {
        if (!Server.isConnected)
        {
            context.source.sendFeedback(Text.translatable("text.dg-lab.isn't-connected"))
            return
        }
        val value = IntegerArgumentType.getInteger(context, "value").coerceIn(0, Strength.getMaxStrength(type))
        Server.setStrength(type, value)
        context.source.sendFeedback(Text.translatable("text.dg-lab.set-strength", type.id, value))
    }
}