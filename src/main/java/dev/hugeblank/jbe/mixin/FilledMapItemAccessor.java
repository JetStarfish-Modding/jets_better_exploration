package dev.hugeblank.jbe.mixin;

import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FilledMapItem.class)
public interface FilledMapItemAccessor {
    @Invoker
    static void callCreateMapState(
            ItemStack stack, World world, int x, int z, int scale, boolean showIcons, boolean unlimitedTracking, RegistryKey<World> dimension
    ) {
        throw new UnsupportedOperationException();
    }
}
