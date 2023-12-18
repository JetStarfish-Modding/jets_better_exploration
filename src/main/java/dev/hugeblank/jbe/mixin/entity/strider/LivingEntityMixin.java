package dev.hugeblank.jbe.mixin.entity.strider;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"), method = "addSoulSpeedBoostIfNeeded", index = 3)
    private ItemStack jbe$horsePOWER(ItemStack value) {
        if ((Object) this instanceof AbstractHorseEntity horse) {
            return horse.getArmorItems().iterator().next();
        }
        return value;
    }

    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)V"), method = "addSoulSpeedBoostIfNeeded()V")
    private void jbe$unhingedHorseSpeed(Args args) {
        if ((Object) this instanceof AbstractHorseEntity horse) {
            if (horse.hasPassengers()) {
                double level = (((double)args.get(2)/0.03f)-1.0f)/0.35;
                args.set(2, 0.30+(level*0.105));
                args.set(3, EntityAttributeModifier.Operation.MULTIPLY_BASE);
            }
        }
    }
}
