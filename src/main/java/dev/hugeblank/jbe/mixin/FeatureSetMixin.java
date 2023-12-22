package dev.hugeblank.jbe.mixin;

import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FeatureSet.class)
public class FeatureSetMixin {

    @Inject(at = @At(value = "RETURN", ordinal = 1), method = "contains(Lnet/minecraft/resource/featuretoggle/FeatureFlag;)Z", cancellable = true)
    private void jbe$forceTradeRebalance(FeatureFlag feature, CallbackInfoReturnable<Boolean> cir) {
        if (FeatureFlags.TRADE_REBALANCE.equals(feature)) {
            cir.setReturnValue(true);
        }
    }
}
