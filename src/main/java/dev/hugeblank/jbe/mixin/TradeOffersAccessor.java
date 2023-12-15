package dev.hugeblank.jbe.mixin;

import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.class)
public interface TradeOffersAccessor {
    @Accessor
    static TradeOffers.SellMapFactory getSELL_DESERT_VILLAGE_MAP_TRADE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TradeOffers.SellMapFactory getSELL_SAVANNA_VILLAGE_MAP_TRADE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TradeOffers.SellMapFactory getSELL_PLAINS_VILLAGE_MAP_TRADE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TradeOffers.SellMapFactory getSELL_TAIGA_VILLAGE_MAP_TRADE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TradeOffers.SellMapFactory getSELL_SNOWY_VILLAGE_MAP_TRADE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TradeOffers.SellMapFactory getSELL_JUNGLE_TEMPLE_MAP_TRADE() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TradeOffers.SellMapFactory getSELL_SWAMP_HUT_MAP_TRADE() {
        throw new UnsupportedOperationException();
    }
}
