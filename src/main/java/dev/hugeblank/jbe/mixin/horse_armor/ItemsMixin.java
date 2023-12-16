package dev.hugeblank.jbe.mixin.horse_armor;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Items.class)
public class ItemsMixin {

    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/HorseArmorItem;<init>(ILjava/lang/String;Lnet/minecraft/item/Item$Settings;)V"), method = "<clinit>")
    private static void jbe$modifyHorseArmor(Args args) {
        jbe$modifyArmor(args);
    }

    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/DyeableHorseArmorItem;<init>(ILjava/lang/String;Lnet/minecraft/item/Item$Settings;)V"), method = "<clinit>")
    private static void jbe$modifyDyeableHorseArmor(Args args) {
        jbe$modifyArmor(args);
    }

    @Unique
    private static void jbe$modifyArmor(Args args) {
        for (ArmorMaterials material : ArmorMaterials.values()) {
            if (material.asString().equals(args.get(1))) {
                Item.Settings settings = args.get(2);
                settings.maxDamageIfAbsent(material.getDurability(ArmorItem.Type.LEGGINGS));
                break;
            }
        }
    }
}
