package dev.hugeblank.jbe.mixin.client;

import dev.hugeblank.jbe.MainInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Unique
    private static final RenderLayer CAVE_MAP_BACKGROUND = RenderLayer.getText(new Identifier("textures/map/cave_map_background.png"));

    @Unique
    private static final RenderLayer CAVE_MAP_BACKGROUND_CHECKERBOARD = RenderLayer.getText(new Identifier("textures/map/cave_map_background_checkerboard.png"));

    @Shadow protected abstract void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract float getMapAngle(float tickDelta);

    @Shadow protected abstract void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm);

    @Shadow private ItemStack mainHand;

    @Shadow private ItemStack offHand;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"), method = "renderFirstPersonItem", cancellable = true)
    private void renderCaveMap(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.isOf(MainInit.FILLED_CAVE_MAP)) {
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            if (bl && this.offHand.isEmpty()) {
                jbe$renderCaveMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
            } else {
                jbe$renderCaveMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
            }
            matrices.pop(); // big oof, maybe fix later...?
            ci.cancel();
        }
    }

    @Unique
    private void jbe$renderCaveMapInOneHand(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack
    ) {
        float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
        matrices.translate(f * 0.125F, -0.125F, 0.0F);
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 10.0F));
            this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            matrices.pop();
        }

        matrices.push();
        matrices.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75F);
        float g = MathHelper.sqrt(swingProgress);
        float h = MathHelper.sin(g * (float) Math.PI);
        float i = -0.5F * h;
        float j = 0.4F * MathHelper.sin(g * (float) (Math.PI * 2));
        float k = -0.3F * MathHelper.sin(swingProgress * (float) Math.PI);
        matrices.translate(f * i, j - 0.3F * h, k);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h * -45.0F));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * h * -30.0F));
        jbe$renderFirstPersonCaveMap(matrices, vertexConsumers, light, stack);
        matrices.pop();
    }

    @Unique
    private void jbe$renderCaveMapInBothHands(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress
    ) {
        float f = MathHelper.sqrt(swingProgress);
        float g = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
        float h = -0.4F * MathHelper.sin(f * (float) Math.PI);
        matrices.translate(0.0F, -g / 2.0F, h);
        float i = this.getMapAngle(pitch);
        matrices.translate(0.0F, 0.04F + equipProgress * -1.2F + i * -0.5F, -0.72F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i * -85.0F));
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
            this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
            this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
            matrices.pop();
        }

        float j = MathHelper.sin(f * (float) Math.PI);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * 20.0F));
        matrices.scale(2.0F, 2.0F, 2.0F);
        jbe$renderFirstPersonCaveMap(matrices, vertexConsumers, light, this.mainHand);
    }

    @Unique
    private void jbe$renderFirstPersonCaveMap(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int swingProgress, ItemStack stack) {
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrices.scale(0.38F, 0.38F, 0.38F);
        matrices.translate(-0.5F, -0.5F, 0.0F);
        matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
        Integer integer = FilledMapItem.getMapId(stack);
        MapState mapState = FilledMapItem.getMapState(integer, this.client.world);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mapState == null ? CAVE_MAP_BACKGROUND : CAVE_MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(swingProgress).next();
        vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(swingProgress).next();
        vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(swingProgress).next();
        vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(swingProgress).next();
        if (mapState != null) {
            this.client.gameRenderer.getMapRenderer().draw(matrices, vertexConsumers, integer, mapState, false, swingProgress);
        }
    }
}
