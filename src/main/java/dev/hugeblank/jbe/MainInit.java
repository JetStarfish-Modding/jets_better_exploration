package dev.hugeblank.jbe;

import com.chocohead.mm.api.ClassTinkerers;
import dev.hugeblank.jbe.item.SculkVialItem;
import dev.hugeblank.jbe.mixin.TradeOffersAccessor;
import dev.hugeblank.jbe.mixin.TypedWrapperFactoryInvoker;
import dev.hugeblank.jbe.network.JbeStateChangeS2CPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.map.MapIcon;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.structure.Structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainInit implements ModInitializer {
	public static final String ID = "jbe";
	public static final TagKey<Structure> ON_ANCIENT_CITY_MAPS;
	public static final Map<RegistryKey<Biome>, List<Block>> BIOME_CROP_BONUSES;
	public static final GameRules.Key<GameRules.BooleanRule> ALLOW_ICE_BOAT_SPEED;
	public static final SculkVialItem SCULK_VIAL;
	public static final PoweredRailBlock POWERED_RAIL;
	private static final TradeOffers.Factory SELL_ANCIENT_CITY_MAP_TRADE;

	private static boolean registeredTrade = false;

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier(ID, "sculk_vial"), SCULK_VIAL);
		Registry.register(Registries.BLOCK, new Identifier(ID, "powered_rail"), POWERED_RAIL);
		Registry.register(Registries.ITEM, Registries.BLOCK.getId(POWERED_RAIL), new BlockItem(POWERED_RAIL, new Item.Settings()));

		// TODO: Modify/remove rails crafting recipe.
	}

	static {
		SCULK_VIAL = new SculkVialItem(new FabricItemSettings()
				.maxCount(1)
		);

		POWERED_RAIL = new PoweredRailBlock(FabricBlockSettings.copy(Blocks.POWERED_RAIL));

		ON_ANCIENT_CITY_MAPS = TagKey.of(RegistryKeys.STRUCTURE, new Identifier("minecraft", "on_ancient_city_maps"));
		SELL_ANCIENT_CITY_MAP_TRADE = new TradeOffers.SellMapFactory(14, MainInit.ON_ANCIENT_CITY_MAPS, "filled_map.ancient_city", ClassTinkerers.getEnum(MapIcon.Type.class, "ANCIENT_CITY"), 12, 30);

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

	public static void registerAncientCityMapTrade() {
		if (!registeredTrade) {
			TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 5, (trades, rebalance) -> {
				if (!rebalance) {
					trades.add(new TradeOffers.SellMapFactory(14, MainInit.ON_ANCIENT_CITY_MAPS, "filled_map.ancient_city", ClassTinkerers.getEnum(MapIcon.Type.class, "ANCIENT_CITY"), 12, 30));
				} else {
					Int2ObjectMap<TradeOffers.Factory[]> cartTrades = TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(VillagerProfession.CARTOGRAPHER);
					TradeOffers.Factory[] oldOffers = cartTrades.get(2);
					HashMap<VillagerType, TradeOffers.Factory> newOffersTwo = new HashMap<>();
					newOffersTwo.put(VillagerType.DESERT, TradeOffersAccessor.getSELL_JUNGLE_TEMPLE_MAP_TRADE());
					newOffersTwo.put(VillagerType.SAVANNA, TradeOffersAccessor.getSELL_JUNGLE_TEMPLE_MAP_TRADE());
					newOffersTwo.put(VillagerType.PLAINS, SELL_ANCIENT_CITY_MAP_TRADE);
					newOffersTwo.put(VillagerType.TAIGA, TradeOffersAccessor.getSELL_SWAMP_HUT_MAP_TRADE());
					newOffersTwo.put(VillagerType.SNOW, TradeOffersAccessor.getSELL_SWAMP_HUT_MAP_TRADE());
					newOffersTwo.put(VillagerType.JUNGLE, TradeOffersAccessor.getSELL_SWAMP_HUT_MAP_TRADE());
					newOffersTwo.put(VillagerType.SWAMP, TradeOffersAccessor.getSELL_JUNGLE_TEMPLE_MAP_TRADE());
					oldOffers[2] = TypedWrapperFactoryInvoker.createTypedWrapperFactory(newOffersTwo);
				}
			});
			registeredTrade = true;
		}
	}

	public static Block melt(Block block) {
		if (block.equals(Blocks.BLUE_ICE)) {
			return Blocks.PACKED_ICE;
		} else if (block.equals(Blocks.PACKED_ICE)) {
			return Blocks.ICE;
		} else {
			return Blocks.AIR;
		}
	}
}