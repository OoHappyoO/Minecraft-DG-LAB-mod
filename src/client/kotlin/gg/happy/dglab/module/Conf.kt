package gg.happy.dglab.module;

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Config(name = "dg-lab")
class Conf : ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
    var webSocket = WebSocketSetting()

    @ConfigEntry.Gui.CollapsibleObject
    var pulse = CollapsedPulseSetting()

    @ConfigEntry.Gui.CollapsibleObject
    var hud = HUDSetting()

    override fun validatePostLoad()
    {
        //TODO
    }

    class WebSocketSetting
    {
        var useHttps = false
        var address = "AUTO"
        var port = 8080

        @ConfigEntry.Gui.CollapsibleObject
        var messageSetting = MessageSetting()

        class MessageSetting
        {
            var pulseListLength = 5
            var lagCompensation = 100
        }
    }

    class CollapsedPulseSetting
    {
        @ConfigEntry.Gui.CollapsibleObject
        var a = PulseSetting()

        @ConfigEntry.Gui.CollapsibleObject
        var b = PulseSetting()

        @ConfigEntry.Gui.CollapsibleObject
        var others = Others()

        class Others
        {
            var rawDamageInput = false
        }
    }

    class PulseSetting
    {
        var frequency = 100 //TODO

        var maximum = 1.5
        var increaseRate = 0.2
        var decreaseRate = 0.04
        var compressor = 0.5
        var multiple = 1.0

        @ConfigEntry.Gui.CollapsibleObject
        var onEvent = OnEvent()

        //TODO Overlay wave

        class OnEvent
        {
            var onDeath = 0.5
            var onTotemPop = 0.3
        }
    }

    class HUDSetting
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
            var enabled = true
            var x = 10
            var y = 10

            @ConfigEntry.ColorPicker(allowAlpha = true)
            var shadowColor = 0x7F3E3E3E
        }
    }
}
