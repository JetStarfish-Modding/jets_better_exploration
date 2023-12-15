package dev.hugeblank.jbe;

import com.chocohead.mm.api.ClassTinkerers;

public class MainEarlyRiser implements Runnable {
    @Override
    public void run() {
        //<init>(Ljava/lang/String;ZIZZ)V
        ClassTinkerers
                .enumBuilder("net/minecraft/item/map/MapIcon$Type", String.class, "Z", "I", "Z", "Z")
                .addEnum("ANCIENT_CITY", "ancient_city", true, 18761, false, true)
                .build();
    }
}
