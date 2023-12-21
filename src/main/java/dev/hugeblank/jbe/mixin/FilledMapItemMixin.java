package dev.hugeblank.jbe.mixin;

import net.minecraft.block.MapColor;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FilledMapItem.class)
public class FilledMapItemMixin {
    @ModifyVariable(at = @At(value = "FIELD", target = "Lnet/minecraft/block/MapColor;CLEAR:Lnet/minecraft/block/MapColor;", ordinal = 1), method = "fillExplorationMap", index = 14)
    private static MapColor jbe$setCaveMapColors(MapColor color, ServerWorld world, ItemStack map) {
        return switch (color.id) {
            case 15 -> MapColor.DARK_AQUA; // MapColor.ORANGE
            case 26 -> MapColor.TEAL; // MapColor.BROWN
            default -> color;
        };
    }
}
