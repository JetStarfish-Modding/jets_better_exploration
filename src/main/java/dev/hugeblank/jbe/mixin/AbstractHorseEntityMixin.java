package dev.hugeblank.jbe.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity {

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    @Shadow public abstract boolean isTame();

    @Unique
    private int staminaTicks = 6000;

    @Unique
    private Vec3d prevPos = this.getPos();

    protected AbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void jbe$tickStamina(CallbackInfo ci) {
        if ((this.getPos().squaredDistanceTo(prevPos) == 0 || this.getControllingPassenger() == null) && staminaTicks < 6000) {
            staminaTicks = Math.min(staminaTicks + 5, 6000);
        } else if (staminaTicks > 0 && this.getControllingPassenger() != null && this.isTame()){
            staminaTicks--;
            if (staminaTicks == 0) {
                this.getControllingPassenger().stopRiding();
            }
        }
        prevPos = this.getPos();
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    private void jbe$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("StaminaTicks", staminaTicks);
    }

    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    private void jbe$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.staminaTicks = nbt.getInt("StaminaTicks");
    }


}
