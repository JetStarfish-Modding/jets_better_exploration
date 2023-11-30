package dev.hugeblank.jbe;

import dev.hugeblank.jbe.item.SculkVialItem;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemUsage;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainInit implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String ID = "jbe";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final SculkVialItem SCULK_VIAL;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registries.ITEM, new Identifier(ID, "sculk_vial"), SCULK_VIAL);
	}

	static {
		SCULK_VIAL = new SculkVialItem(new FabricItemSettings()
				.maxCount(1)
		);
	}
}