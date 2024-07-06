package dev.hugeblank.jbe.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPool.Builder.class)
public interface LootPoolBuilderAccessor {
    @Accessor
    List<LootPoolEntry> getEntries();

    @Mutable
    @Accessor
    void setEntries(List<LootPoolEntry> entries);
}
