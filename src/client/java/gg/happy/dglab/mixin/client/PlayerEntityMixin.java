package gg.happy.dglab.mixin.client;

import com.mojang.authlib.GameProfile;
import gg.happy.dglab.module.outputer.OutputterManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity
{
    @Shadow
    public abstract float getAbsorptionAmount();

    public PlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile)
    {
        super(EntityType.PLAYER, world);
    }

    @Inject(method = "applyDamage", at = @At("HEAD"))
    private void onApplyDamage(ServerWorld world, DamageSource source, float amount, CallbackInfo ci)
    {
        float damage = this.applyArmorToDamage(source, amount);
        damage = this.modifyAppliedDamage(source, damage);
        OutputterManager.onServerDamage(damage, amount);
    }
}
