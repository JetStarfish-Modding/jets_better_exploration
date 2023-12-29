package dev.hugeblank.jbe.mixin;

import dev.hugeblank.jbe.MainInit;
import net.minecraft.block.MapColor;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FilledMapItem.class)
public class FilledMapItemMixin {
    @ModifyVariable(at = @At(value = "FIELD", target = "Lnet/minecraft/block/MapColor;CLEAR:Lnet/minecraft/block/MapColor;", ordinal = 2), method = "fillExplorationMap", index = 14)
    private static MapColor jbe$setCaveMapColors(MapColor color, ServerWorld world, ItemStack map) {
        if (map.isOf(MainInit.FILLED_CAVE_MAP)) {
            return switch (color.id) {
                case 15 -> MapColor.DARK_AQUA; // MapColor.ORANGE
                case 26 -> MapColor.TEAL; // MapColor.BROWN
                default -> color;
            };
        }
        return color;
    }
}
