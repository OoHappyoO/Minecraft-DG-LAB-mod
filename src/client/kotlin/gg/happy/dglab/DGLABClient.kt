package gg.happy.dglab

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.logging.LogUtils
import gg.happy.dglab.module.Command
import gg.happy.dglab.module.Conf
import gg.happy.dglab.module.Strength
import gg.happy.dglab.module.hud.InfoHud
import gg.happy.dglab.module.listener.JoinListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger

object DGLABClient : ClientModInitializer
{
    val LOGGER: Logger = LogUtils.getLogger()
    val GSON: Gson = GsonBuilder().create()
    val mc: MinecraftClient = MinecraftClient.getInstance()
    val scope = CoroutineScope(Dispatchers.Default)

    override fun onInitializeClient()
    {
        AutoConfig.register(Conf::class.java, ::JanksonConfigSerializer)
        Command.register()
        JoinListener.register()
        InfoHud.register()

        //TODO QRCode HUD
    }
}