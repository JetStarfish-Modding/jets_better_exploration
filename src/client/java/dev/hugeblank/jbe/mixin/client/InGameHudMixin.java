package dev.hugeblank.jbe.mixin.client;

import dev.hugeblank.jbe.MainInit;
import dev.hugeblank.jbe.entity.StaminaMount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private static final Identifier STAMINA_BAR_BACKGROUND_TEXTURE = new Identifier(MainInit.ID, "textures/gui/sprites/hud/stamina_bar_background.png");
    @Unique
    private static final Identifier STAMINA_BAR_PROGRESS_TEXTURE = new Identifier(MainInit.ID, "textures/gui/sprites/hud/stamina_bar_progress.png");

    @Shadow @Final private MinecraftClient client;

    @Shadow private int scaledHeight;

    @Unique
    public void jbe$renderMountStaminaBar(StaminaMount mount, DrawContext context, int x) {
        this.client.getProfiler().push("staminaBar");
        float f = (float) mount.jbe$getStamina()/((AbstractHorseEntity)mount).getWorld().getGameRules().getInt(MainInit.HORSE_STAMINA);
        int j = (int)(f * 182.0F);
        int k = this.scaledHeight - 32 + 3;
        context.drawTexture(STAMINA_BAR_BACKGROUND_TEXTURE, x, k, 0, 0, 182, 5, 182, 5);
        if (j > 0) {
            context.drawTexture(STAMINA_BAR_PROGRESS_TEXTURE, x, k, 0, 0, j, 5, 182, 5);
        }

        this.client.getProfiler().pop();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0), method = "renderMountJumpBar", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void jbe$hideJumpBar(JumpingMount mount, DrawContext context, int x, CallbackInfo ci, float f, int i, int j, int k) {
        if (j == 0) {
            ci.cancel();
            this.client.getProfiler().pop();
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/entity/JumpingMount;Lnet/minecraft/client/gui/DrawContext;I)V"), method = "render", locals = LocalCapture.CAPTURE_FAILHARD)
    private void jbe$addStaminaBar(DrawContext context, float tickDelta, CallbackInfo ci, Window window, TextRenderer textRenderer, float f, float g, int i, JumpingMount jumpingMount) {
        jbe$renderMountStaminaBar((StaminaMount) jumpingMount, context, i);
    }
}
