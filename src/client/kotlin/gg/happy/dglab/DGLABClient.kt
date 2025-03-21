package gg.happy.dglab

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.logging.LogUtils
import gg.happy.dglab.module.Command
import gg.happy.dglab.module.Conf
import gg.happy.dglab.module.hud.InfoHud
import gg.happy.dglab.module.hud.QRCodeHud
import gg.happy.dglab.module.listener.JoinListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger

object DGLABClient : ClientModInitializer
{
    val logger: Logger = LogUtils.getLogger()
    val gson: Gson = GsonBuilder().create()
    val mc: MinecraftClient = MinecraftClient.getInstance()
    val scope = CoroutineScope(Dispatchers.Default)
    lateinit var configHolder: ConfigHolder<Conf>
    lateinit var conf: Conf

    override fun onInitializeClient()
    {
        AutoConfig.register(Conf::class.java, ::JanksonConfigSerializer)
        configHolder = AutoConfig.getConfigHolder(Conf::class.java)
        conf = configHolder.config
        listOf(Command, JoinListener, InfoHud, QRCodeHud).forEach { it.register() }
        //TODO QRCode HUD
    }
}