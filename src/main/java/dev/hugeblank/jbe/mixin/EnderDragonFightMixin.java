package dev.hugeblank.jbe.mixin;

import dev.hugeblank.jbe.MainInit;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"), method = "dragonKilled(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;)V")
    private void jbe$unlockAncientCityMapTrade(EnderDragonEntity dragon, CallbackInfo ci) {
        // This inject is in a conditional that checks if this is the first time the dragon has been killed.
        MainInit.registerAncientCityMapTrade();
    }
}
