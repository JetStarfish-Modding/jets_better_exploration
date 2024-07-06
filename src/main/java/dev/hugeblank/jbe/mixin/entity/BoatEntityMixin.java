package dev.hugeblank.jbe.mixin.entity;

import dev.hugeblank.jbe.MainInit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BoatEntity.class)
public class BoatEntityMixin {
    @Unique
    private float slip = 0;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 1), method = "getNearbySlipperiness()F", locals = LocalCapture.CAPTURE_FAILHARD)
    private void jbe$checkSlipperiness(CallbackInfoReturnable<Float> cir, Box box, Box box2, int i, int j, int k, int l, int m, int n, VoxelShape voxelShape, float f, int o, BlockPos.Mutable mutable, int p, int q, int r, int s, BlockState blockState) {
        if (blockState.getBlock().getSlipperiness() > 0.6F && !((Entity)(Object)this).getWorld().getGameRules().getBoolean(MainInit.ALLOW_ICE_BOAT_SPEED)) { // TODO is this warning true?
            slip += blockState.getBlock().getSlipperiness() - 0.6F;
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(at = @At(value = "RETURN", ordinal = 0), method = "getNearbySlipperiness()F", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void jbe$removeSlipperiness(CallbackInfoReturnable<Float> cir, Box box, Box box2, int i, int j, int k, int l, int m, int n, VoxelShape voxelShape, float f, int o) {
        if (!((Entity)(Object)this).getWorld().getGameRules().getBoolean(MainInit.ALLOW_ICE_BOAT_SPEED)) {
            cir.setReturnValue((f-slip)/o);
            slip = 0;
        }
    }
}
