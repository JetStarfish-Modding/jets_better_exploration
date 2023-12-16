package dev.hugeblank.jbe.mixin.entity;

import dev.hugeblank.jbe.entity.StaminaMount;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity implements StaminaMount {

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    @Shadow public abstract boolean isTame();

    @Shadow protected SimpleInventory items;
    @Unique
    private static TrackedData<Integer> HORSE_STAMINA;

    @Unique
    private Vec3d prevPos = this.getPos();

    protected AbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.getWorld().isClient() ? super.getArmorItems() : List.of(this.items.getStack(1));
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

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    private void jbe$initTrackedStamina(CallbackInfo ci) {
        if (!this.dataTracker.containsKey(HORSE_STAMINA)) {
            this.dataTracker.startTracking(HORSE_STAMINA, 6000);
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void jbe$tickStamina(CallbackInfo ci) {
        if (!this.getWorld().isClient()) {
            LivingEntity controller = this.getControllingPassenger();
            int staminaTicks = this.dataTracker.get(HORSE_STAMINA);
            if ((this.getPos().squaredDistanceTo(prevPos) == 0 || controller == null) && staminaTicks < 6000) {
                this.dataTracker.set(HORSE_STAMINA, Math.min(staminaTicks + 5, 6000));
            } else if (staminaTicks > 0 && this.isTame()){
                this.dataTracker.set(HORSE_STAMINA, staminaTicks-1);
            }
            if (staminaTicks == 0 && this.getControllingPassenger() != null) {
                this.getControllingPassenger().stopRiding();
            }
            prevPos = this.getPos();
        }
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    private void jbe$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("StaminaTicks", this.dataTracker.get(HORSE_STAMINA));
    }

    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    private void jbe$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!this.dataTracker.containsKey(HORSE_STAMINA)) {
            this.dataTracker.startTracking(HORSE_STAMINA, nbt.getInt("StaminaTicks"));
        } else {
            this.dataTracker.set(HORSE_STAMINA, nbt.getInt("StaminaTicks"));
        }
    }

    @Unique
    public int jbe$getStamina() {
        return this.dataTracker.get(HORSE_STAMINA);
    }

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void jbe$clinit(CallbackInfo ci) {
        //noinspection WrongEntityDataParameterClass
        HORSE_STAMINA = DataTracker.registerData(AbstractHorseEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
