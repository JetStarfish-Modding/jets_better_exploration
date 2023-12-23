package dev.hugeblank.jbe.mixin.entity.strider;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SpawnRestriction.class)
public class SpawnRestrictionMixin {

    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/SpawnRestriction;register(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/SpawnRestriction$Location;Lnet/minecraft/world/Heightmap$Type;Lnet/minecraft/entity/SpawnRestriction$SpawnPredicate;)V", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityType;STRIDER:Lnet/minecraft/entity/EntityType;")), method = "<clinit>")
    private static void jbe$swapStriderRestriction(Args args) {
        args.set(1, SpawnRestriction.Location.ON_GROUND);
        args.set(3, (SpawnRestriction.SpawnPredicate<StriderEntity>) SpawnRestrictionMixin::jbe$canStriderSpawn);
    }

    @Unique
    private static boolean jbe$canStriderSpawn(EntityType<StriderEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        pos = pos.down();
        BlockState blockState = world.getBlockState(pos);
        return world.getFluidState(pos).isIn(FluidTags.LAVA) || blockState.isOf(Blocks.NETHERRACK) || blockState.isOf(Blocks.BASALT) || blockState.isOf(Blocks.CRIMSON_NYLIUM) || blockState.isOf(Blocks.MAGMA_BLOCK);
    }
}
