package dev.hugeblank.jbe.mixin.entity;

import dev.hugeblank.jbe.MainInit;
import dev.hugeblank.jbe.entity.StaminaMount;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity implements StaminaMount {


    @Unique
    private static final UUID HORSE_EXHAUSTED_ID = UUID.fromString("87f035c6-df44-4351-a396-c9698a293ce3");
    @Unique
    private static final EntityAttributeModifier HORSE_EXHAUSTED_MOD = new EntityAttributeModifier(HORSE_EXHAUSTED_ID, "Horse Exhaustion Debuff", -0.25d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    @Unique
    private final EntityAttributeInstance HORSE_MOVEMENT_SPEED = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    @Shadow public abstract boolean isTame();

    @Shadow protected SimpleInventory items;

    @Shadow public abstract boolean isSaddled();

    @Unique
    private static TrackedData<Integer> HORSE_STAMINA;
    @Unique
    private static TrackedData<Boolean> HORSE_EXHAUSTED;

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

    @Override
    public boolean canSprintAsVehicle() {
        return !this.dataTracker.get(HORSE_EXHAUSTED);
    }

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    private void jbe$initTrackedStamina(CallbackInfo ci) {
        if (!this.dataTracker.containsKey(HORSE_STAMINA) || !this.dataTracker.containsKey(HORSE_EXHAUSTED)) {
            this.dataTracker.startTracking(HORSE_STAMINA, MainInit.HORSE_STAMINA);
            this.dataTracker.startTracking(HORSE_EXHAUSTED, false);
        }
    }

    @Inject(at = @At("TAIL"), method = "tickMovement")
    private void jbe$tickStamina(CallbackInfo ci) {
        if (!this.getWorld().isClient() && HORSE_MOVEMENT_SPEED != null) {
            LivingEntity controller = this.getControllingPassenger();
            int staminaTicks = this.dataTracker.get(HORSE_STAMINA);
            boolean exhausted = this.dataTracker.get(HORSE_EXHAUSTED);
            // if nobody is mounted, or the controller is not sprinting, THEN canRecover is true.
            boolean canRecover = controller == null || !controller.isSprinting();
            if (canRecover && staminaTicks < MainInit.HORSE_STAMINA) {
                this.dataTracker.set(HORSE_STAMINA, Math.min(staminaTicks + 2, MainInit.HORSE_STAMINA)); // regenerate stamina
                this.setSprinting(false);
            } else if (!canRecover && staminaTicks > 0 && this.isTame() && this.isSaddled()){
                this.dataTracker.set(HORSE_STAMINA, staminaTicks-1); // decrement stamina
                this.setSprinting(true);
            }
            if (staminaTicks == 0 && !exhausted) {
                this.dataTracker.set(HORSE_EXHAUSTED, true);
                if (controller != null) {
                    controller.setSprinting(false);
                }
                if (!HORSE_MOVEMENT_SPEED.hasModifier(HORSE_EXHAUSTED_MOD)) {
                    HORSE_MOVEMENT_SPEED.addTemporaryModifier(HORSE_EXHAUSTED_MOD);
                }
                if (this.isSprinting()) {
                    this.setSprinting(false);
                }
            } else if (staminaTicks == MainInit.HORSE_STAMINA && exhausted) {
                this.dataTracker.set(HORSE_EXHAUSTED, false);
                HORSE_MOVEMENT_SPEED.removeModifier(HORSE_EXHAUSTED_ID);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    private void jbe$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("StaminaTicks", this.dataTracker.get(HORSE_STAMINA));
        nbt.putBoolean("Exhausted", this.dataTracker.get(HORSE_EXHAUSTED));
    }

    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    private void jbe$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!this.dataTracker.containsKey(HORSE_STAMINA) || !this.dataTracker.containsKey(HORSE_EXHAUSTED)) {
            this.dataTracker.startTracking(HORSE_STAMINA, nbt.getInt("StaminaTicks"));
            this.dataTracker.startTracking(HORSE_EXHAUSTED, nbt.getBoolean("Exhausted"));
        } else {
            this.dataTracker.set(HORSE_STAMINA, nbt.getInt("StaminaTicks"));
            this.dataTracker.set(HORSE_EXHAUSTED, nbt.getBoolean("Exhausted"));
        }
    }

    @Unique
    public int jbe$getStamina() {
        return this.dataTracker.get(HORSE_STAMINA);
    }

    @SuppressWarnings("WrongEntityDataParameterClass")
    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void jbe$clinit(CallbackInfo ci) {
        HORSE_STAMINA = DataTracker.registerData(AbstractHorseEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HORSE_EXHAUSTED = DataTracker.registerData(AbstractHorseEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
