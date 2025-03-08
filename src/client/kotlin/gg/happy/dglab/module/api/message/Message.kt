package gg.happy.dglab.module.api.message

import gg.happy.dglab.DGLABClient
import gg.happy.dglab.module.Server

class Message(
    var type: MessageType,
    var clientId: String = Server.clientID,
    var targetId: String = Server.targetID,
    var message: String = ""
)
{
    fun toJson(): String = DGLABClient.GSON.toJson(this)

    companion object
    {
        fun heartbeat(message: String = "200", clientID: String = Server.clientID, targetID: String = Server.targetID) =
            Message(MessageType.HEARTBEAT, clientID, targetID, message)

        fun msg(message: String, clientID: String = Server.clientID, targetID: String = Server.targetID) =
            Message(MessageType.MSG, clientID, targetID, message)

        fun bind(message: String, clientID: String = Server.clientID, targetID: String = Server.targetID) =
            Message(MessageType.BIND, clientID, targetID, message)
    }
}