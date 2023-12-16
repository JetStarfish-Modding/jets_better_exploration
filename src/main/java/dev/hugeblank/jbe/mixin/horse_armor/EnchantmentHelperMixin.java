package dev.hugeblank.jbe.mixin.horse_armor;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isTreasure()Z"), method = "getPossibleEntries", locals = LocalCapture.CAPTURE_FAILHARD)
    private static void jbe$coercePossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir, List<EnchantmentLevelEntry> list, Item item, boolean bl, Iterator var6, Enchantment enchantment) {
        if (!enchantment.target.isAcceptableItem(item) && (!enchantment.isTreasure() || treasureAllowed) && enchantment.isAvailableForRandomSelection() && enchantment.isAcceptableItem(stack)) {
            for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
                    list.add(new EnchantmentLevelEntry(enchantment, i));
                    break;
                }
            }
        }
    }
}
