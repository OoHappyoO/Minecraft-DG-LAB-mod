package gg.happy.dglab.module

import gg.happy.dglab.DGLABClient
import gg.happy.dglab.module.api.ChannelType
import gg.happy.dglab.module.api.message.Message
import gg.happy.dglab.module.api.message.MessageType
import gg.happy.dglab.module.outputer.OutputterManager
import gg.happy.dglab.util.toArrayString
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.text.Text
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.util.*
import java.util.regex.Pattern

object Server : WebSocketServer(
    InetSocketAddress(AutoConfig.getConfigHolder(Conf::class.java).config.webSocket.port)
)
{
    var isRunning = false
    var isConnected = false

    var client: WebSocket? = null
    var clientID = UUID.randomUUID().toString()
    var targetID = ""
    private val strengthPatten: Pattern =
        Pattern.compile("strength-(\\d+)\\+(\\d+)\\+(\\d+)\\+(\\d+)", Pattern.MULTILINE)

    private var heartbeatJob: Job? = null

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?)
    {
        if (isConnected)
        {
            conn?.send("{\"type\":\"error\",\"message\":\"400\"}")
            return
        }
        client = conn
        isConnected = true
        sendMessage(Message.bind("targetId"))

        OutputterManager.initJob()
        heartbeatJob = DGLABClient.scope.launch {
            var next = System.currentTimeMillis()
            while (isConnected)
            {
                next += 60000
                delay(next - System.currentTimeMillis())
                sendMessage(Message.heartbeat())
            }
        }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean)
    {
        if (conn != client)
            return

        OutputterManager.stopJob()

        isConnected = false
        heartbeatJob?.cancel()
        client = null
        DGLABClient.mc.player?.sendMessage(Text.literal("disconnected: $reason"), false)
    }

    override fun onMessage(conn: WebSocket?, messageText: String?)
    {
        if (conn != client)
            return
        val message = DGLABClient.GSON.fromJson(messageText, Message::class.java)
        when (message.type)
        {
            MessageType.BIND ->
            {
                if (message.message == "DGLAB")
                {
                    targetID = message.targetId
                    sendMessage(Message.bind("200"))
                }
            }

            MessageType.MSG ->
            {
                val matcher = strengthPatten.matcher(message.message)
                DGLABClient.strength.byMatcher(matcher)
            }

            else -> Unit
        }
    }

    override fun onError(conn: WebSocket?, e: Exception?)
    {
        e?.printStackTrace()
    }

    override fun onStart()
    {
        isRunning = true
    }

    private fun sendMessage(message: Message) =
        client?.send(message.toJson())

    fun disconnect() =
        sendMessage(Message(MessageType.BREAK))

    fun setStrength(channelType: ChannelType, value: Int) =
        sendMessage(Message.msg("strength-${channelType.id}+2+$value"))

    fun addStrength(channelType: ChannelType, value: Int) =
        sendMessage(Message.msg("strength-${channelType.id}+1+$value"))


    fun reduceStrength(channelType: ChannelType, value: Int) =
        sendMessage(Message.msg("strength-${channelType.id}+0+$value"))


    fun addPulse(channelType: ChannelType, pulse: List<String>) =
        sendMessage(Message.msg("pulse-${channelType.name}:${toArrayString(pulse)}"))


    fun cleanPulse(channelType: ChannelType) =
        sendMessage(Message.msg("clear-${channelType.id}"))
}