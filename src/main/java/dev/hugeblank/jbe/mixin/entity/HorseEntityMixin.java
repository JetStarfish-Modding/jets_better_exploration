package dev.hugeblank.jbe.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(HorseEntity.class)
public abstract class HorseEntityMixin extends AbstractHorseEntityMixin{

    @Shadow public abstract ItemStack getArmorType();

    protected HorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return List.of(this.getArmorType());
    }

    @Override
    public void damageArmor(DamageSource source, float amount) {
        if (!(amount <= 0.0F)) {
            amount /= 4.0F;
            if (amount < 1.0F) {
                amount = 1.0F;
            }
            ItemStack itemStack = this.items.getStack(1);
            if ((!source.isIn(DamageTypeTags.IS_FIRE) || !itemStack.getItem().isFireproof()) && itemStack.getItem() instanceof HorseArmorItem) {
                itemStack.damage((int)amount, this, entity -> {
                    if (!entity.getWorld().isClient()) entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                });
            }
        }
    }


}
