package gg.happy.dglab.module

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import gg.happy.dglab.module.api.ChannelType
import gg.happy.dglab.util.QRCodeUtil
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
    val conf = AutoConfig.getConfigHolder(Conf::class.java).config

    fun register()
    {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("dglab")
                    .then(literal("connect").executes { context ->
                        if (!Server.isRunning)
                            Server.start()
                        val url =
                            "https://www.dungeon-lab.com/app-download.php#DGLAB-SOCKET#${if (conf.webSocket.useHttps) "wss" else "ws"}://${conf.webSocket.address}:${conf.webSocket.port}/${Server.clientID}"
                        QRCodeUtil.generateQRCodeAndOpen(url)
                        context.source.sendFeedback(Text.literal("扫描二维码"))
                        return@executes 1
                    })
                    .then(literal("disconnect").executes { context ->
                        if (Server.isConnected)
                        {
                            Server.disconnect()
                            context.source.sendFeedback(Text.literal("断开连接"))
                        }
                        else
                            context.source.sendFeedback(Text.literal("未连接"))
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
    }

    private fun commandSetStrength(channelType: ChannelType, context: CommandContext<FabricClientCommandSource>)
    {
        if (!Server.isConnected)
        {
            context.source.sendFeedback(Text.literal("未连接"))
            return
        }
        Server.setStrength(
            channelType,
            IntegerArgumentType.getInteger(context, "value")
        )
    }
}