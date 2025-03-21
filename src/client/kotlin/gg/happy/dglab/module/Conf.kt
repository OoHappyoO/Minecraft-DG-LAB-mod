package gg.happy.dglab.module;

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Config(name = "dg-lab")
class Conf : ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
    var webSocket = WebSocket()

    @ConfigEntry.Gui.CollapsibleObject
    var pulse = CollapsedPulse()

    @ConfigEntry.Gui.CollapsibleObject
    var hud = HUD()

    @ConfigEntry.Gui.Excluded
    var preset = mutableMapOf<String, CollapsedPulse>()

    override fun validatePostLoad()
    {
        //TODO
    }

    class WebSocket
    {
        @ConfigEntry.Gui.Tooltip
        var useHttps = false
        var address = "AUTO"
        var port = 8080

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.CollapsibleObject
        var message = Message()

        @ConfigEntry.Gui.CollapsibleObject
        var qrCode = QRCode()

        class Message
        {
            var length = 5
            var lagCompensation = 100
        }

        class QRCode
        {
            @ConfigEntry.ColorPicker
            var color = 0xF9E49C

            @ConfigEntry.ColorPicker
            var background = 0x121212

            var size = 256
        }
    }

    @DeepCopy
    data class CollapsedPulse(
        @ConfigEntry.Gui.CollapsibleObject
        var a: Pulse = Pulse(),

        @ConfigEntry.Gui.CollapsibleObject
        var b: Pulse = Pulse(),

        @ConfigEntry.Gui.CollapsibleObject
        var others: Others = Others()
    )

    @DeepCopy
    data class Pulse(
        var frequency: Int = 100, //TODO

        var maximum: Double = 1.5,
        var increaseRate: Double = 0.2,
        var decreaseRate: Double = 0.04,
        var compressor: Double = 0.5,
        var multiplier: Double = 1.0,

        @ConfigEntry.Gui.CollapsibleObject
        var onEvent: OnEvent = OnEvent()
    )

    @DeepCopy
    data class OnEvent(
        var onDeath: Double = 0.5,
        var onTotemPop: Double = 0.3
    )

    //TODO Overlay wave

    @DeepCopy
    data class Others(
        @ConfigEntry.Gui.Tooltip
        var rawDamageInput: Boolean = false
    )

    class HUD
    {
        @ConfigEntry.Gui.CollapsibleObject
        var info = Info()

        @ConfigEntry.Gui.CollapsibleObject
        var qrCode = QRCode()

        class Info
        {
            var enabled = false
            var x = 10
            var y = 10
        }

        class QRCode
        {
            @ConfigEntry.Gui.Tooltip
            var enabled = true
            var x = 10
            var y = 10
            var size = 147

            @ConfigEntry.ColorPicker(allowAlpha = true)
            var shadowColor = 0x7F3E3E3E
        }
    }
}
