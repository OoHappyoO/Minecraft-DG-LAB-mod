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

        var others = Others()

        class Others
        {
            var rawDamageInput = false
        }
    }

    class PulseSetting
    {
        var frequency = 100

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

    class HUDSetting{
        var enabled = false
        var x = 0
        var y = 0
    }
}
