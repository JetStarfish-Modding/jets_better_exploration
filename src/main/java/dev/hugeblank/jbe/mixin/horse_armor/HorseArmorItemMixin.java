package dev.hugeblank.jbe.mixin.horse_armor;

import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HorseArmorItem.class)
public class HorseArmorItemMixin extends Item {

    @Shadow @Final private int bonus;

    public HorseArmorItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public int getEnchantability() {
        return switch (this.bonus) {
            case 3 -> ArmorMaterials.LEATHER.getEnchantability();
            case 5 -> ArmorMaterials.GOLD.getEnchantability();
            case 7 -> ArmorMaterials.IRON.getEnchantability();
            case 11 -> ArmorMaterials.DIAMOND.getEnchantability();
            default -> 0;
        };
    }
}
