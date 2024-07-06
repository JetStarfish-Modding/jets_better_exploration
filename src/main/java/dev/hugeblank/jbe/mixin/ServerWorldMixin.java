package dev.hugeblank.jbe.mixin;

import dev.hugeblank.jbe.MainDataModifications;
import dev.hugeblank.jbe.MainInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {


    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/ChunkSectionPos;getBlockCoord(I)I"), method = "tickChunk(Lnet/minecraft/world/chunk/WorldChunk;I)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void jbe$biomeBoostRandomTick(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler, ChunkSection[] chunkSections, int l, ChunkSection chunkSection, int m, int n) {
        for(int o = 0; o < Math.round((float) randomTickSpeed / 2f); ++o) {
            World thiz = chunk.getWorld();
            BlockPos blockPos2 = thiz.getRandomPosInChunk(i, n, j, 15);
            profiler.push("biomeBoostRandomTick");
            BlockState blockState = chunkSection.getBlockState(blockPos2.getX() - i, blockPos2.getY() - n, blockPos2.getZ() - j);
            List<Block> blocks = MainDataModifications.BIOME_CROP_BONUSES.get(
                    thiz.getBiome(blockPos2).getKey().orElse(BiomeKeys.THE_VOID)
            );
            if (blocks != null && blocks.contains(blockState.getBlock())) {
                if (blockState.hasRandomTicks()) {
                    blockState.randomTick((ServerWorld) thiz, blockPos2, thiz.random);
                }

                FluidState fluidState = blockState.getFluidState();
                if (fluidState.hasRandomTicks()) {
                    fluidState.onRandomTick(thiz, blockPos2, thiz.random);
                }
            }

            profiler.pop();
        }
    }

//    @Inject(at = @At("RETURN"), method = "getEnabledFeatures()Lnet/minecraft/resource/featuretoggle/FeatureSet;", cancellable = true)
//    private void jbe$modifyEnabledFeatures(CallbackInfoReturnable<FeatureSet> cir) {
//        cir.setReturnValue(MainInit.FORCE_TRADE_REBALANCE);
//    }
}
