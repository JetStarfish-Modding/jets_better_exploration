package dev.hugeblank.jbe.mixin.entity.strider;

import net.minecraft.entity.passive.StriderEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StriderEntity.GoBackToLavaGoal.class)
public class GoBackToLavaGoalMixin {

    @Shadow @Final private StriderEntity strider;

    @Inject(at = @At("HEAD"), method = "canStart", cancellable = true)
    private void jbe$unleashTheStrider(CallbackInfoReturnable<Boolean> cir) {
        if (this.strider.getWorld().getDimension().ultrawarm()) {
            cir.setReturnValue(false);
        }
    }
}
