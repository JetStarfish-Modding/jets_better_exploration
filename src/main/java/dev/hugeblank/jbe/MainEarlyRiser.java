package dev.hugeblank.jbe;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;

public class MainEarlyRiser implements Runnable {

    @Override
    public void run() {
        //<init>(Ljava/lang/String;ZIZZ)V
        ClassTinkerers
                .enumBuilder(FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_20$class_21"), String.class, "Z", "I", "Z", "Z")
                .addEnum("ANCIENT_CITY", "ancient_city", true, 0x236e86, false, true)
                .build();
    }
}
