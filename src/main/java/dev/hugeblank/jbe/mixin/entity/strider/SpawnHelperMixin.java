package dev.hugeblank.jbe.mixin.entity.strider;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin {

    @Shadow
    public static boolean isClearForSpawn(BlockView blockView, BlockPos pos, BlockState state, FluidState fluidState, EntityType<?> entityType) {
        return true;
    }

    @Inject(at = @At("HEAD"),method = "canSpawn(Lnet/minecraft/entity/SpawnRestriction$Location;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z", cancellable = true)
    private static void jbe$freeTheStriders(SpawnRestriction.Location location, WorldView world, BlockPos pos, @Nullable EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (entityType != null && entityType.equals(EntityType.STRIDER) && world.getWorldBorder().contains(pos)) {
            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);
            BlockPos blockPos = pos.up();
            BlockPos blockPos2 = pos.down();
            BlockState blockState2 = world.getBlockState(blockPos2);
            if (!blockState2.allowsSpawning(world, blockPos2, entityType)) {
                cir.setReturnValue(fluidState.isIn(FluidTags.LAVA));
            } else {
                cir.setReturnValue(isClearForSpawn(world, pos, blockState, fluidState, entityType) && isClearForSpawn(world, blockPos, world.getBlockState(blockPos), world.getFluidState(blockPos), entityType));
            }
        }
    }
}
