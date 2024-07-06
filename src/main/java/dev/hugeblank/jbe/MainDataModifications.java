package dev.hugeblank.jbe;

import com.google.common.collect.ImmutableList;
import dev.hugeblank.jbe.mixin.ItemEntryAccessor;
import dev.hugeblank.jbe.mixin.LootPoolBuilderAccessor;
import net.fabricmc.fabric.api.biome.v1.*;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Items;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.*;
import java.util.function.Predicate;

public class MainDataModifications {
    public static final Map<RegistryKey<Biome>, List<Block>> BIOME_CROP_BONUSES;
    private static final Map<Predicate<BiomeSelectionContext>, Map<RegistryKey<PlacedFeature>, RegistryKey<PlacedFeature>>> FEATURES_TO_BIOMES;
    private static final List<String> KEYNAMES;
    private static final BiomeModification MODIFICATIONS = BiomeModifications.create(new Identifier(MainInit.ID, "biome_modifications"));

    public static void init() {}

    static {
        KEYNAMES = List.of(
                "coal_lower", "coal_upper", "copper", "diamond", "diamond_buried", "diamond_large",
                "diamond", "iron_middle", "iron_small", "iron_upper", "lapis", "lapis_buried",
                "redstone", "redstone_lower"
        );

        FEATURES_TO_BIOMES = Map.of(
                BiomeSelectors.tag(TagKey.of(RegistryKeys.BIOME, new Identifier("is_ocean"))), keysFromPrefix("copper"),
                BiomeSelectors.tag(TagKey.of(RegistryKeys.BIOME, new Identifier("is_mountain"))), keysFromPrefix("iron"),
                BiomeSelectors.includeByKey(BiomeKeys.BIRCH_FOREST, BiomeKeys.OLD_GROWTH_BIRCH_FOREST), keysFromPrefix("iron"),
                BiomeSelectors.tag(TagKey.of(RegistryKeys.BIOME, new Identifier("is_taiga"))), keysFromPrefix("lapis"),
                BiomeSelectors.includeByKey(BiomeKeys.DARK_FOREST, BiomeKeys.LUSH_CAVES), keysFromPrefix("lapis"),
                BiomeSelectors.tag(TagKey.of(RegistryKeys.BIOME, new Identifier("is_jungle"))), keysFromPrefix("diamond"),
                BiomeSelectors.includeByKey(BiomeKeys.SWAMP, BiomeKeys.MANGROVE_SWAMP), keysFromPrefix("redstone"),
                BiomeSelectors.tag(TagKey.of(RegistryKeys.BIOME, new Identifier("is_savanna"))), keysFromPrefix("coal"),
                BiomeSelectors.includeByKey(BiomeKeys.DESERT), keysFromPrefix("coal")
        );

        FEATURES_TO_BIOMES.forEach((predicate, map) -> MODIFICATIONS.add( // Ore bonus distribution
                ModificationPhase.REPLACEMENTS,
                predicate,
                (context) -> map.forEach((from, to) -> {
                    if (context.getGenerationSettings().removeFeature(from)) {
                        context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, to);
                    }
                })
        ));

        MODIFICATIONS.add( // Donkey spawn adjustment - matches with rates of horses
                ModificationPhase.REPLACEMENTS,
                BiomeSelectors.spawnsOneOf(EntityType.DONKEY),
                (context) -> {
                    if (context.getSpawnSettings().removeSpawnsOfEntityType(EntityType.DONKEY)) {
                        context.getSpawnSettings().addSpawn(
                                SpawnGroup.CREATURE,
                                new SpawnSettings.SpawnEntry(EntityType.DONKEY, 5, 2,6 )
                        );
                    }
                }
        );

        MODIFICATIONS.add( // Strider spawn probability adjustment
                ModificationPhase.REPLACEMENTS,
                BiomeSelectors.spawnsOneOf(EntityType.STRIDER),
                (context) -> context.getSpawnSettings().setCreatureSpawnProbability(0.15f)
        );

        LootTableEvents.MODIFY.register((ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source) -> {
            if (source.isBuiltin()) {
                if (id.equals(new Identifier("chests/ancient_city"))) {
                    tableBuilder.modifyPools((pool) -> {
                        boolean replace = false; // now this is just stupid...
                        List<LootPoolEntry> builder = new ArrayList<>();
                        for (LootPoolEntry entry : ((LootPoolBuilderAccessor) pool).getEntries()) {
                            if (entry instanceof ItemEntry && ((ItemEntryAccessor) entry).getItem().equals(Items.DIAMOND_HOE)) {
                                EnchantWithLevelsLootFunction.Builder enchantLootFunctionBuilder = EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(30, 50)).allowTreasureEnchantments();
                                Collections.addAll(builder, ItemEntry.builder(Items.DIAMOND_HELMET).apply(enchantLootFunctionBuilder).build(),
                                        ItemEntry.builder(Items.DIAMOND_CHESTPLATE).apply(enchantLootFunctionBuilder).build(),
                                        ItemEntry.builder(Items.DIAMOND_LEGGINGS).apply(enchantLootFunctionBuilder).build(),
                                        ItemEntry.builder(Items.DIAMOND_BOOTS).apply(enchantLootFunctionBuilder).build(),
                                        ItemEntry.builder(Items.DIAMOND_PICKAXE).apply(enchantLootFunctionBuilder).build(),
                                        ItemEntry.builder(Items.DIAMOND_SWORD).apply(enchantLootFunctionBuilder).build(),
                                        ItemEntry.builder(Items.DIAMOND_AXE).apply(enchantLootFunctionBuilder).build(),
                                        ItemEntry.builder(Items.DIAMOND_SHOVEL).apply(enchantLootFunctionBuilder).build(),
                                        ItemEntry.builder(Items.DIAMOND_HOE).apply(enchantLootFunctionBuilder).build());
                                replace = true;
                            } else if (entry instanceof ItemEntry && ((ItemEntryAccessor) entry).getItem().equals(Items.DIAMOND_HORSE_ARMOR)) {
                                EnchantWithLevelsLootFunction.Builder enchantLootFunctionBuilder = EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(0, 50)).allowTreasureEnchantments();
                                builder.add(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).apply(enchantLootFunctionBuilder).build());
                                replace = true;
                            } else {
                                builder.add(entry);
                            }
                        }
                        if (replace) {
                            ((LootPoolBuilderAccessor) pool).setEntries(builder);
                        }
                    });
                } else if (id.equals(new Identifier("chests/woodland_mansion"))) {
                    tableBuilder.pool(
                            LootPool.builder()
                                    .rolls(ConstantLootNumberProvider.create(1))
                                    .with(EmptyEntry.builder())
                                    .with(EmptyEntry.builder())
                                    .with(ItemEntry.builder(Items.RECOVERY_COMPASS))
                                    .with(ItemEntry.builder(Items.RECOVERY_COMPASS))
                    );
                }
            }

        });


        BIOME_CROP_BONUSES = new HashMap<>();
        registerCropsToBiomes(
                List.of(Blocks.KELP),
                BiomeKeys.WARM_OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.OCEAN, BiomeKeys.DEEP_OCEAN,
                BiomeKeys.COLD_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.FROZEN_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN
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

    private static Map<RegistryKey<PlacedFeature>, RegistryKey<PlacedFeature>> keysFromPrefix(String prefix) {
        Iterator<RegistryKey<PlacedFeature>> vanillaFeatureKeys = KEYNAMES.stream().filter(
                (str) -> str.startsWith(prefix)
        ).map(
                (str) -> "ore_" + str
        ).map(
                Identifier::new
        ).map(
                (identifier) -> RegistryKey.of(RegistryKeys.PLACED_FEATURE, identifier)
        ).iterator();

        Iterator<RegistryKey<PlacedFeature>> bonusFeatureKeys = KEYNAMES.stream().filter(
                (str) -> str.startsWith(prefix)
        ).map(
                (str) -> "ore_" + str + "_bonus"
        ).map(
                (str) -> new Identifier(MainInit.ID, str)
        ).map(
                (identifier) -> RegistryKey.of(RegistryKeys.PLACED_FEATURE, identifier)
        ).iterator();

        Map<RegistryKey<PlacedFeature>, RegistryKey<PlacedFeature>> out = new HashMap<>();

        while (vanillaFeatureKeys.hasNext() && bonusFeatureKeys.hasNext()) {
            out.put(vanillaFeatureKeys.next(), bonusFeatureKeys.next());
        }
        return out;
    }
}
