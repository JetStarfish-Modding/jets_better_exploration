package dev.hugeblank.jbe.mixin.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HorseArmorFeatureRenderer.class)
public class HorseArmorFeatureRendererMixin{

    @Shadow @Final private HorseEntityModel<HorseEntity> model;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/HorseEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/HorseEntity;FFFFFF)V", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void jbe$renderGlint(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntity horseEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack itemStack, HorseArmorItem horseArmorItem, float n, float o, float p, VertexConsumer vertexConsumer) {
        this.model.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getArmorCutoutNoCull(horseArmorItem.getEntityTexture())), i, OverlayTexture.DEFAULT_UV, n, o, p, 1.0F);
        if (itemStack.hasGlint()) {
            this.model.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getArmorEntityGlint()), i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        ci.cancel(); // sorry!
    }
}
