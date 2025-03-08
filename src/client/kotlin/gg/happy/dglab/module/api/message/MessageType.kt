package gg.happy.dglab.module.api.message

import com.google.gson.annotations.SerializedName

enum class MessageType
{
    @SerializedName("heartbeat")
    HEARTBEAT,

    @SerializedName("bind")
    BIND,

    @SerializedName("msg")
    MSG,

    @SerializedName("break")
    BREAK,

    @SerializedName("error")
    ERROR
}