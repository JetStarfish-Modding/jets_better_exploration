package dev.hugeblank.jbe.item;

import dev.hugeblank.jbe.MainInit;
import net.minecraft.block.MapColor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SculkVialItem extends Item {

    public static final int MAX_XP = 1395;

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

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        int vialXP = getVialExperience(stack);
        NbtCompound nbt = stack.getNbt();
        //noinspection DataFlowIssue
        nbt.putInt("experience", 0);
        world.playSound(null, user.getBlockPos(), MainInit.SCULK_VIAL_WITHDRAW, SoundCategory.PLAYERS, 1F, world.random.nextFloat() * 0.1F + 0.9F);
        if (user instanceof PlayerEntity player) {
            player.addExperience(vialXP);
        }
        return stack;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        int vialXP = getVialExperience(itemStack);
        int playerXP = getPlayerTotalExperience(user);
        NbtCompound nbt = itemStack.getNbt();
        if (user.isSneaking() && vialXP <= MAX_XP && (playerXP > 0 || user.isCreative())) {
            int freeSpace = MAX_XP-vialXP;
            int addXp = user.isCreative() ? freeSpace : Math.min(playerXP, freeSpace);
            if (addXp > 0) {
                if (user instanceof ServerPlayerEntity && !user.isCreative()) {
                    user.addExperience(-addXp);
                }
                nbt.putInt("experience", vialXP +addXp);
                world.playSound(null, user.getBlockPos(), MainInit.SCULK_VIAL_DEPOSIT, SoundCategory.PLAYERS, 1F, world.random.nextFloat() * 0.1F + 0.9F);
                return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
            }
        } else if (!user.isSneaking() && vialXP > 0) {
            return ItemUsage.consumeHeldItem(world, user, hand);
        }
        return new TypedActionResult<>(ActionResult.PASS, itemStack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return getVialExperience(stack) > 0 ? UseAction.DRINK : UseAction.NONE;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return getVialExperience(stack) > 0;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(itemStack, world, tooltip, context);
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null) {
            tooltip.add(Text.translatable("item.jbe.sculk_vial.levels", Text.literal(Objects.toString(nbt.getInt("experience")) + "/" + MAX_XP).formatted(Formatting.GRAY)));
        }
        if (world != null && world.isClient()) {
            tooltip.add(Text.translatable("item.jbe.sculk_vial.usage.fill", Text.keybind("key.sneak"), Text.keybind("key.use")).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("item.jbe.sculk_vial.usage.drain", Text.keybind("key.use")).formatted(Formatting.GRAY)
            );
        }
    }

    private static int getLevelUpExperience(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    private static int getPlayerTotalExperience(PlayerEntity user) {
        int levels = user.experienceLevel;
        int points = 0;
        while (levels > 0) {
            points += getLevelUpExperience(--levels);
        }
        points += Math.round(((float) user.getNextLevelExperience()) * user.experienceProgress);
        return points;
    }
}
