package dev.hugeblank.jbe.mixin;

import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(TradeOffers.TypedWrapperFactory.class)
public interface TypedWrapperFactoryInvoker {
    @Invoker("<init>")
    static TradeOffers.TypedWrapperFactory createTypedWrapperFactory(Map<VillagerType, TradeOffers.Factory> map) {
        throw new UnsupportedOperationException();
    }
}
