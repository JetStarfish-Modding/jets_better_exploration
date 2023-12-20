package dev.hugeblank.jbe.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(targets = "net/minecraft/structure/EndCityGenerator$3")
public class EndCityGeneratorMixin {
    @Unique
    private static final Random jbe$random = new Random();

    @Shadow
    public boolean shipGenerated;

    @Inject(at = @At("TAIL"), method = "init()V", remap = false)
    private void jbe$nerfShips(CallbackInfo ci) {
        shipGenerated = jbe$random.nextBoolean();
    }
}
