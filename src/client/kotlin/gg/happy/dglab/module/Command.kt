package gg.happy.dglab.module

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import gg.happy.dglab.DGLABClient
import gg.happy.dglab.module.api.ChannelType
import gg.happy.dglab.module.api.Registrable
import gg.happy.dglab.module.hud.InfoHud
import gg.happy.dglab.module.hud.QRCodeHud
import gg.happy.dglab.module.outputer.OutputterManager
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
import java.util.concurrent.CompletableFuture

object Command : Registrable
{
    private val conf = DGLABClient.conf

    override fun register() =
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
                                        context.send("text.dg-lab.cannot-get-address")
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
                        context.send("text.dg-lab.scan")
                        return@executes 1
                    })
                    .then(literal("disconnect").executes { context ->
                        if (Server.isConnected)
                            Server.disconnect()
                        else
                        {
                            QRCodeHud.enabled = false
                            context.send("text.dg-lab.isn't-connected")
                        }
                        return@executes 1
                    })
                    .then(
                        literal("preset")
                            .then(
                                literal("saveTo")
                                    .then(
                                        argument("name", StringArgumentType.string())
                                            .suggests(PresetSuggestionProvider())
                                            .executes { context ->
                                                val name = StringArgumentType.getString(context, "name")
                                                conf.preset[name] = conf.pulse.deepCopy()
                                                DGLABClient.configHolder.save()
                                                context.send("text.dg-lab.preset.save-to", name)
                                                return@executes 1
                                            })
                            )
                            .then(
                                literal("apply")
                                    .then(
                                        argument("name", StringArgumentType.string())
                                            .suggests(PresetSuggestionProvider())
                                            .executes { context ->
                                                val name = StringArgumentType.getString(context, "name")
                                                conf.preset[name]?.let {
                                                    conf.pulse = it.deepCopy()
                                                    DGLABClient.configHolder.save()
                                                    context.send("text.dg-lab.preset.apply", name)
                                                } ?: context.send("text.dg-lab.preset.cannot-find", name)
                                                return@executes 1
                                            })
                            )
                            .then(
                                literal("rename")
                                    .then(
                                        argument("name", StringArgumentType.string())
                                            .suggests(PresetSuggestionProvider())
                                            .then(argument("newName", StringArgumentType.string()).executes { context ->
                                                val name = StringArgumentType.getString(context, "name")
                                                val newName = StringArgumentType.getString(context, "newName")
                                                if (!conf.preset.containsKey(name))
                                                {
                                                    context.send("text.dg-lab.preset.cannot-find", name)
                                                    return@executes 1
                                                }
                                                if (conf.preset.containsKey(newName))
                                                {
                                                    context.send("text.dg-lab.preset.already-exists", newName)
                                                    return@executes 1
                                                }
                                                conf.preset.remove(name)?.let {
                                                    conf.preset[newName] = it
                                                    DGLABClient.configHolder.save()
                                                    context.send("text.dg-lab.preset.rename", name, newName)
                                                }
                                                return@executes 1
                                            })
                                    )
                            )
                            .then(
                                literal("delete")
                                    .then(
                                        argument("name", StringArgumentType.string())
                                            .suggests(PresetSuggestionProvider())
                                            .executes { context ->
                                                val name = StringArgumentType.getString(context, "name")
                                                conf.preset.remove(name)?.let {
                                                    context.send("text.dg-lab.preset.delete", name)
                                                    DGLABClient.configHolder.save()
                                                } ?: context.send("text.dg-lab.preset.cannot-find", name)
                                                return@executes 1
                                            })
                            )
                    )
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

    private fun CommandContext<FabricClientCommandSource>.send(key: String, vararg args: Any) =
        source.sendFeedback(Text.translatable(key, *args))

    private fun commandSetStrength(type: ChannelType, context: CommandContext<FabricClientCommandSource>)
    {
        if (!Server.isConnected)
        {
            context.send("text.dg-lab.isn't-connected")
            return
        }
        val value = IntegerArgumentType.getInteger(context, "value").coerceIn(0, Strength.getMaxStrength(type))
        Server.setStrength(type, value)
        context.send("text.dg-lab.set-strength", type.id, value)
    }

    class PresetSuggestionProvider : SuggestionProvider<FabricClientCommandSource>
    {
        @Throws(CommandSyntaxException::class)
        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource>,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions>
        {
            conf.preset.forEach { (key, _) ->
                builder.suggest(key)
            }
            return builder.buildFuture()
        }
    }
}