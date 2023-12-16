package dev.hugeblank.jbe.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(at = @At("HEAD"), method = "hasRandomTicks", cancellable = true)
    private void jbe$yesIceCanRandomTick(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        // Force all ice blocks to be random ticked
        if (state.isOf(Blocks.BLUE_ICE) || state.isOf(Blocks.PACKED_ICE)) {
            cir.setReturnValue(true);
        }
    }
}
