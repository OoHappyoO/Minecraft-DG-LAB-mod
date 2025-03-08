package gg.happy.dglab.mixin.client;

import gg.happy.dglab.module.outputer.OutputterManager;
import gg.happy.dglab.module.listener.ClientDamageListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
    public ClientPlayerEntityMixin(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting)
    {
        super(world, networkHandler.getProfile());
    }

    @Inject(method = "updateHealth", at = @At("HEAD"))
    public void onHealthUpdate(float health, CallbackInfo ci)
    {
        ClientDamageListener.onHealthUpdate(health);
    }
}
