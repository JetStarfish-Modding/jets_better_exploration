package dev.hugeblank.jbe.mixin.client;

import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(SplashTextResourceSupplier.class)
public class InconspicuousSplashTextResourceSupplierMixin {
    @Shadow @Final private Session session;

    @Inject(at = @At("HEAD"), method = "get", cancellable = true)
    private void jbe$heehee(CallbackInfoReturnable<SplashTextRenderer> cir) {
        UUID uuid = session.getUuidOrNull();
        if (uuid != null && (uuid.equals(UUID.fromString("a4809359-f386-4b1e-a65b-5fd21d758f38")) || uuid.equals(UUID.fromString("87f035c6-df44-4351-a396-c9698a293ce3")))) {
            cir.setReturnValue(new SplashTextRenderer("https://patreon.com/JetStarFish!"));
        }
    }
}
