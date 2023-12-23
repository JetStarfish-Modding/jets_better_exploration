package dev.hugeblank.jbe.mixin.entity.strider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StriderEntity.class)
public abstract class StriderEntityMixin extends AnimalEntity {

    protected StriderEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/StriderEntity;setCold(Z)V"), method = "tick")
    private boolean jbe$giveStriderHandwarmers(boolean cold) {
        return !this.getWorld().getDimension().ultrawarm() && cold;
    }
}
