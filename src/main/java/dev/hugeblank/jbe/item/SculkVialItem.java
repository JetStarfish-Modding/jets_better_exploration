package dev.hugeblank.jbe.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SculkVialItem extends Item {

    public SculkVialItem(Settings settings) {
        super(settings);
    }

    protected int getVialExperience(ItemStack itemStack) {
        if (itemStack.getNbt() == null) {
            itemStack.setNbt(new NbtCompound());
        }
        NbtCompound nbt = itemStack.getNbt();
        if (!nbt.contains("experience")) {
            nbt.putInt("experience", 0);
        }
        return nbt.getInt("experience");
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        int experience = getVialExperience(itemStack);
        NbtCompound nbt = itemStack.getNbt();
        if (user.isSneaking() && experience < 30) {
            if (user.experienceLevel > 0) {
                if (user instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) user).setExperienceLevel(user.experienceLevel-1);
                }
                nbt.putInt("experience", experience+1);
                return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
            }
        } else if (!user.isSneaking() && experience > 0) {
            nbt.putInt("experience", 0);
            user.addExperienceLevels(experience);
            return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
        }
        return new TypedActionResult<>(ActionResult.PASS, itemStack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(itemStack, world, tooltip, context);
        tooltip.add(Text.translatable("item.jbe.sculk_vial.levels", Text.literal(Objects.toString(getVialExperience(itemStack))).setStyle(Style.EMPTY.withColor(TextColor.parse("gray"))))
                .setStyle(Style.EMPTY.withColor(TextColor.parse("dark_gray")))
        );
        if (world.isClient()) {
            tooltip.add(Text.translatable("item.jbe.sculk_vial.usage.fill", Text.keybind("key.sneak"), Text.keybind("key.use"))
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("dark_gray")))
            );
            tooltip.add(Text.translatable("item.jbe.sculk_vial.usage.drain", Text.keybind("key.use"))
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("dark_gray")))
            );
        }
    }


}
