package dev.hugeblank.jbe.mixin.village;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(TradeOffers.EnchantBookFactory.class)
public class EnchantBookFactoryMixin {

    @Shadow
    @Final
    private int experience;

    @Unique
    private static final Map<VillagerType, List<Enchantment>> NORMAL_TRADE_OFFERS = new HashMap<>();
    @Unique
    private static final Map<VillagerType, EnchantmentLevelEntry> SPECIAL_TRADE_OFFERS = new HashMap<>();

    @Inject(at = @At("HEAD"), method = "create(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/random/Random;)Lnet/minecraft/village/TradeOffer;", cancellable = true)
    private void typedCreate(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        if (entity instanceof VillagerEntity villager && random.nextBoolean()) {
            EnchantmentLevelEntry leveledEnchantment;
            if (experience >= 30) {
                leveledEnchantment = SPECIAL_TRADE_OFFERS.get(villager.getVillagerData().getType());
            } else {
                List<Enchantment> list = NORMAL_TRADE_OFFERS.get(villager.getVillagerData().getType());
                Enchantment enchantment = list.get(random.nextInt(list.size()));
                int level = MathHelper.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
                leveledEnchantment = new EnchantmentLevelEntry(enchantment, level);
            }
            ItemStack stack = EnchantedBookItem.forEnchantment(leveledEnchantment);
            int price = 2 + random.nextInt(5 + leveledEnchantment.level * 10) + 3 * leveledEnchantment.level;
            if (leveledEnchantment.enchantment.isTreasure()) {
                price *= 2;
            }
            if (price > 64) {
                price = 64;
            }
            cir.setReturnValue(new TradeOffer(new ItemStack(Items.EMERALD, price), new ItemStack(Items.BOOK), stack, 12, this.experience, 0.2f));
        }
        // https://minecraft.wiki/w/Villager_Trade_Rebalance
    }

    static {
        NORMAL_TRADE_OFFERS.put(VillagerType.DESERT, List.of(
                Enchantments.FIRE_PROTECTION,
                Enchantments.THORNS,
                Enchantments.INFINITY
        ));
        NORMAL_TRADE_OFFERS.put(VillagerType.PLAINS, List.of(
                Enchantments.PUNCH,
                Enchantments.SMITE,
                Enchantments.BANE_OF_ARTHROPODS
        ));
        NORMAL_TRADE_OFFERS.put(VillagerType.SAVANNA, List.of(
                Enchantments.KNOCKBACK,
                Enchantments.BINDING_CURSE,
                Enchantments.SWEEPING
        ));
        NORMAL_TRADE_OFFERS.put(VillagerType.SNOW, List.of(
                Enchantments.AQUA_AFFINITY,
                Enchantments.LOOTING,
                Enchantments.FROST_WALKER
        ));
        NORMAL_TRADE_OFFERS.put(VillagerType.TAIGA, List.of(
                Enchantments.BLAST_PROTECTION,
                Enchantments.FIRE_ASPECT,
                Enchantments.FLAME
        ));
        NORMAL_TRADE_OFFERS.put(VillagerType.JUNGLE, List.of(
                Enchantments.FEATHER_FALLING,
                Enchantments.PROJECTILE_PROTECTION,
                Enchantments.POWER
        ));
        NORMAL_TRADE_OFFERS.put(VillagerType.SWAMP, List.of(
                Enchantments.DEPTH_STRIDER,
                Enchantments.RESPIRATION,
                Enchantments.VANISHING_CURSE
        ));
        SPECIAL_TRADE_OFFERS.put(VillagerType.DESERT, new EnchantmentLevelEntry(Enchantments.EFFICIENCY, 3));
        SPECIAL_TRADE_OFFERS.put(VillagerType.PLAINS, new EnchantmentLevelEntry(Enchantments.PROTECTION, 3));
        SPECIAL_TRADE_OFFERS.put(VillagerType.SAVANNA, new EnchantmentLevelEntry(Enchantments.SHARPNESS, 3));
        SPECIAL_TRADE_OFFERS.put(VillagerType.SNOW, new EnchantmentLevelEntry(Enchantments.SILK_TOUCH, 1));
        SPECIAL_TRADE_OFFERS.put(VillagerType.TAIGA, new EnchantmentLevelEntry(Enchantments.FORTUNE, 2));
        SPECIAL_TRADE_OFFERS.put(VillagerType.JUNGLE, new EnchantmentLevelEntry(Enchantments.UNBREAKING, 2));
        SPECIAL_TRADE_OFFERS.put(VillagerType.SWAMP, new EnchantmentLevelEntry(Enchantments.MENDING, 1));
    }
}
