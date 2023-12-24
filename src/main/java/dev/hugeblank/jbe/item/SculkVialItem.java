package dev.hugeblank.jbe.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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
            nbt.putInt("level", 0);
        }
        return nbt.getInt("experience");
    }

    protected int getVialLevel(ItemStack itemStack) {
        int points = getVialExperience(itemStack);
        int level = 0;
        while (points >= 0) {
            points -= getLevelUpExperience(level++);
        }
        return level-1;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        int experience = getVialExperience(itemStack);
        NbtCompound nbt = itemStack.getNbt();
        if (user.isSneaking() && experience < MAX_XP) {
            if (getPlayerTotalExperience(user) > 0 || user.isCreative()) {
                int addXp = Math.min(getLevelUpExperience(user.experienceLevel-1), MAX_XP-experience);
                if (user instanceof ServerPlayerEntity && !user.isCreative()) {
                    user.addExperience(-addXp);
                }
                nbt.putInt("experience", experience+addXp);
                nbt.putInt("level", getVialLevel(itemStack));
                return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
            }
        } else if (!user.isSneaking() && experience > 0) {
            nbt.putInt("experience", 0);
            nbt.putInt("level", 0);
            user.addExperience(experience);
            return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
        }
        return new TypedActionResult<>(ActionResult.PASS, itemStack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(itemStack, world, tooltip, context);
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null) {
            tooltip.add(Text.translatable("item.jbe.sculk_vial.levels", Text.literal(Objects.toString(nbt.getInt("level"))).setStyle(Style.EMPTY.withColor(TextColor.parse("gray"))))
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("dark_gray")))
            );
        }
        if (world != null && world.isClient()) {
            tooltip.add(Text.translatable("item.jbe.sculk_vial.usage.fill", Text.keybind("key.sneak"), Text.keybind("key.use"))
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("dark_gray")))
            );
            tooltip.add(Text.translatable("item.jbe.sculk_vial.usage.drain", Text.keybind("key.use"))
                    .setStyle(Style.EMPTY.withColor(TextColor.parse("dark_gray")))
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
            points += getLevelUpExperience(levels--);
        }
        points += Math.round((float) user.getNextLevelExperience() * user.experienceProgress);
        return points;
    }
}
