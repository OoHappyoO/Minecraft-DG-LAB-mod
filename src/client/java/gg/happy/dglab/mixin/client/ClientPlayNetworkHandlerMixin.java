package gg.happy.dglab.mixin.client;

import gg.happy.dglab.module.outputer.OutputterManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler
{
    @Shadow
    private ClientWorld world;

    public ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState)
    {
        super(client, clientConnection, clientConnectionState);
    }

    @Inject(method = "onEntityStatus", at = @At("RETURN"))
    void entityStatus(EntityStatusS2CPacket packet, CallbackInfo ci)
    {
        if(world==null) return;
        Entity entity = packet.getEntity(world);
        if (entity != null && packet.getStatus() == 35 && entity == this.client.player)
            OutputterManager.onTotemPop();
    }

    @Inject(method = "onDeathMessage", at = @At("RETURN"))
    public void deathMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
        Entity entity = this.world.getEntityById(packet.playerId());
        if (entity == this.client.player) {
            OutputterManager.onDeath();
        }
    }
}
