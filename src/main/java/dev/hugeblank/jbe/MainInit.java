package dev.hugeblank.jbe;

import dev.hugeblank.jbe.item.SculkVialItem;
import dev.hugeblank.jbe.network.JbeStateChangeS2CPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainInit implements ModInitializer {
	public static final String ID = "jbe";
	public static final Map<RegistryKey<Biome>, List<Block>> BIOME_CROP_BONUSES;
	public static final GameRules.Key<GameRules.BooleanRule> ALLOW_ICE_BOAT_SPEED;
	public static final SculkVialItem SCULK_VIAL;

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier(ID, "sculk_vial"), SCULK_VIAL);
	}

	static {
		SCULK_VIAL = new SculkVialItem(new FabricItemSettings()
				.maxCount(1)
		);
		ALLOW_ICE_BOAT_SPEED = GameRuleRegistry.register("allowIceBoatSpeed", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false, (minecraftServer, booleanRule) -> {
            for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				ServerPlayNetworking.send(serverPlayerEntity, new JbeStateChangeS2CPacket(JbeStateChangeS2CPacket.ALLOW_ICE_BOAT_SPEED, booleanRule.get() ? 1.0F : 0.0F));
            }
		}));

		BIOME_CROP_BONUSES = new HashMap<>();
		registerCropsToBiomes(
				List.of(Blocks.KELP),
				BiomeKeys.WARM_OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.FROZEN_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN
		);
		registerCropsToBiomes(
				List.of(Blocks.PUMPKIN_STEM),
				BiomeKeys.FROZEN_PEAKS, BiomeKeys.JAGGED_PEAKS, BiomeKeys.STONY_PEAKS
		);
		registerCropsToBiomes(
				List.of(Blocks.CARROTS, Blocks.POTATOES, Blocks.SWEET_BERRY_BUSH),
				BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, BiomeKeys.TAIGA, BiomeKeys.SNOWY_TAIGA
		);
		registerCropsToBiomes(
				List.of(Blocks.BEETROOTS),
				BiomeKeys.DARK_FOREST
		);
		registerCropsToBiomes(
				List.of(Blocks.MELON, Blocks.BAMBOO, Blocks.COCOA),
				BiomeKeys.JUNGLE, BiomeKeys.BAMBOO_JUNGLE, BiomeKeys.SPARSE_JUNGLE
		);
		registerCropsToBiomes(
				List.of(Blocks.SUGAR_CANE),
				BiomeKeys.SWAMP, BiomeKeys.MANGROVE_SWAMP
		);
		registerCropsToBiomes(
				List.of(Blocks.CACTUS),
				BiomeKeys.DESERT
		);
		registerCropsToBiomes(
				List.of(Blocks.WHEAT),
				BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.WINDSWEPT_SAVANNA
		);
	}

	@SafeVarargs
	private static void registerCropsToBiomes(List<Block> blocks, RegistryKey<Biome>... biomes) {
		for (RegistryKey<Biome> biome: biomes) {
			BIOME_CROP_BONUSES.put(biome, blocks);
		}
	}
}