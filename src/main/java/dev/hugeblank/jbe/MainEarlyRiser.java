package dev.hugeblank.jbe;

import com.chocohead.mm.api.ClassTinkerers;
import net.minecraft.entity.EquipmentSlot;

public class MainEarlyRiser implements Runnable {
    @Override
    public void run() {
        //<init>(Ljava/lang/String;ZIZZ)V
        ClassTinkerers
                .enumBuilder("net/minecraft/item/map/MapIcon$Type", String.class, "Z", "I", "Z", "Z")
                .addEnum("ANCIENT_CITY", "ancient_city", true, 18761, false, true)
                .build();

        ClassTinkerers
                .enumBuilder("net/minecraft/entity/EquipmentSlot", "Lnet/minecraft/entity/EquipmentSlot$Type;", "I", "I", String.class)
                .addEnum("HORSE", EquipmentSlot.Type.ARMOR, 0, 0, "horse")
                .build();
    }
}
