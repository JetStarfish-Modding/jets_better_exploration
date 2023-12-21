package dev.hugeblank.jbe.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"), method = "addSoulSpeedBoostIfNeeded", cancellable = true)
    private void jbe$horsePOWER(CallbackInfo ci) {
        if ((Object) this instanceof HorseEntity horse) {
            horse.getArmorItems().iterator().next().damage(1, horse, horseEntity -> horseEntity.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
            ci.cancel();
        }
    }

    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)V"), method = "addSoulSpeedBoostIfNeeded()V")
    private void jbe$unhingedHorseSpeed(Args args) {
        if ((Object) this instanceof HorseEntity horse) {
            if (horse.hasPassengers()) {
                double level = (((double)args.get(2)/0.03f)-1.0f)/0.35;
                args.set(2, 0.30+(level*0.105));
                args.set(3, EntityAttributeModifier.Operation.MULTIPLY_BASE);
            }
        }
    }
}
