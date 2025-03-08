package gg.happy.dglab.mixin.client;

import gg.happy.dglab.module.listener.ClientDamageListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "tick", at = @At("RETURN"))
    public void onTick(CallbackInfo info)
    {
        if (player != null)
            ClientDamageListener.onTick(player.getHealth(), player.getAbsorptionAmount());
    }
}
