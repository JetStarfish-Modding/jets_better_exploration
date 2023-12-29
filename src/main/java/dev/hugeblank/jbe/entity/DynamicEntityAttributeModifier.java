package dev.hugeblank.jbe.entity;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;

public class DynamicEntityAttributeModifier extends EntityAttributeModifier {
    private double value;

    // When using, do not forget to initialize value on world/server load!
    public DynamicEntityAttributeModifier(String name, Operation operation) {
        super(name, 0d, operation);
    }
    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "AttributeModifier{amount=" + getValue() + ", operation=" + getOperation() + ", name='" + getName() + "', id=" + getId() + "}";
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbtCompound = super.toNbt();
        nbtCompound.remove("Amount");
        return nbtCompound;
    }
}
