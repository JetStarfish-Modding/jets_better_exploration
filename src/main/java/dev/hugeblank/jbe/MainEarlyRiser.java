package dev.hugeblank.jbe;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;

public class MainEarlyRiser implements Runnable {

    @Override
    public void run() {
        //<init>(ZIZ)V
        ClassTinkerers
                .enumBuilder(FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_20$class_21"), "Z", "I", "Z")
                .addEnum("ANCIENT_CITY", true, 0x236e86, false)
                .build();
    }
}
