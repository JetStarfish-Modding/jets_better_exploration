package dev.hugeblank.jbe.mixin.entity.strider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin {

    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/SpawnRestriction;getLocation(Lnet/minecraft/entity/EntityType;)Lnet/minecraft/entity/SpawnRestriction$Location;"), method = "getEntitySpawnPos(Lnet/minecraft/world/WorldView;Lnet/minecraft/entity/EntityType;II)Lnet/minecraft/util/math/BlockPos;", index = 5)
    private static BlockPos.Mutable jbe$fixVanillaBug(BlockPos.Mutable value, WorldView world) {
        return world.getDimension().hasCeiling() ? value.move(Direction.UP) : value;
    }

    @Inject(at = @At("HEAD"), method = "canSpawn(Lnet/minecraft/entity/SpawnRestriction$Location;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z", cancellable = true)
    private static void jbe$freeTheStriders(SpawnRestriction.Location location, WorldView world, BlockPos pos, @Nullable EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (entityType != null && entityType.equals(EntityType.STRIDER) && world.getWorldBorder().contains(pos) && world.getFluidState(pos.down()).isIn(FluidTags.LAVA)) {
            cir.setReturnValue(true);
        }
    }
}