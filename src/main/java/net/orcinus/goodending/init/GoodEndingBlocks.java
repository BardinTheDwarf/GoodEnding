package net.orcinus.goodending.init;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodenButtonBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.orcinus.goodending.GoodEnding;
import net.orcinus.goodending.blocks.CattailBlock;
import net.orcinus.goodending.blocks.InundatedSaplingBlock;
import net.orcinus.goodending.blocks.PurpleMushroomBlock;
import net.orcinus.goodending.blocks.PurpleMushroomFullBlock;
import net.orcinus.goodending.world.gen.features.generators.CypressSaplingGenerator;
import net.orcinus.goodending.world.gen.features.generators.SwampSaplingGenerator;

import java.util.Map;

public class GoodEndingBlocks {

    private static final Map<Identifier, Block> BLOCKS = Maps.newLinkedHashMap();

    public static final Block SWAMP_LOG = registerBlock("swamp_log", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.GREEN : MapColor.GRAY).strength(2.0F).sounds(BlockSoundGroup.WOOD)));
    public static final Block STRIPPED_SWAMP_LOG = registerBlock("stripped_swamp_log", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.GREEN).strength(2.0F).sounds(BlockSoundGroup.WOOD)));
    public static final Block SWAMP_WOOD = registerBlock("swamp_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.GRAY).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Block STRIPPED_SWAMP_WOOD = registerBlock("stripped_swamp_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.GREEN).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Block SWAMP_PLANKS = registerBlock("swamp_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.DARK_GREEN).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Block SWAMP_SLAB = registerBlock("swamp_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.DARK_GREEN).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Block SWAMP_STAIRS = registerBlock("swamp_stairs", new StairsBlock(SWAMP_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(SWAMP_PLANKS)));
    public static final Block SWAMP_BUTTON = registerBlock("swamp_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOD)));
    public static final Block SWAMP_PRESSURE_PLATE = registerBlock("swamp_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.WOOD, SWAMP_PLANKS.getDefaultMapColor()).noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOD)));
    public static final Block SWAMP_DOOR = registerBlock("swamp_door", new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, SWAMP_PLANKS.getDefaultMapColor()).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque()));
    public static final Block SWAMP_FENCE = registerBlock("swamp_fence", new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, SWAMP_PLANKS.getDefaultMapColor()).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque()));
    public static final Block SWAMP_FENCE_GATE = registerBlock("swamp_fence_gate", new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, SWAMP_PLANKS.getDefaultMapColor()).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque()));
    public static final Block SWAMP_TRAPDOOR = registerBlock("swamp_trapdoor", new TrapdoorBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.DARK_GREEN).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning((state, world, pos, type) -> false)));
    public static final Block SWAMP_SAPLING = registerBlock("swamp_sapling", new InundatedSaplingBlock(new SwampSaplingGenerator(), AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)));
    public static final Block SWAMP_LEAVES = registerBlock("swamp_leaves", new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES)));
    public static final Block CYPRESS_LOG = registerBlock("cypress_log", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.BROWN : MapColor.LIGHT_GRAY).strength(2.0F).sounds(BlockSoundGroup.WOOD)));
    public static final Block STRIPPED_CYPRESS_LOG = registerBlock("stripped_cypress_log", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).strength(2.0F).sounds(BlockSoundGroup.WOOD)));
    public static final Block CYPRESS_WOOD = registerBlock("cypress_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.LIGHT_GRAY).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Block STRIPPED_CYPRESS_WOOD = registerBlock("stripped_cypress_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.BROWN).strength(2.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Block CYPRESS_PLANKS = registerBlock("cypress_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.TERRACOTTA_BROWN).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Block CYPRESS_SLAB = registerBlock("cypress_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.TERRACOTTA_BROWN).strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Block CYPRESS_STAIRS = registerBlock("cypress_stairs", new StairsBlock(CYPRESS_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(CYPRESS_PLANKS)));
    public static final Block CYPRESS_BUTTON = registerBlock("cypress_button", new WoodenButtonBlock(AbstractBlock.Settings.of(Material.DECORATION).noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOD)));
    public static final Block CYPRESS_PRESSURE_PLATE = registerBlock("cypress_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.WOOD, CYPRESS_PLANKS.getDefaultMapColor()).noCollision().strength(0.5f).sounds(BlockSoundGroup.WOOD)));
    public static final Block CYPRESS_DOOR = registerBlock("cypress_door", new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.TERRACOTTA_BROWN).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque()));
    public static final Block CYPRESS_FENCE = registerBlock("cypress_fence", new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, CYPRESS_PLANKS.getDefaultMapColor()).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque()));
    public static final Block CYPRESS_FENCE_GATE = registerBlock("cypress_fence_gate", new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, CYPRESS_PLANKS.getDefaultMapColor()).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque()));
    public static final Block CYPRESS_TRAPDOOR = registerBlock("cypress_trapdoor", new TrapdoorBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.TERRACOTTA_BROWN).strength(3.0f).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning((state, world, pos, type) -> false)));
    public static final Block CYPRESS_SAPLING = registerBlock("cypress_sapling", new InundatedSaplingBlock(new CypressSaplingGenerator(), AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)));
    public static final Block CYPRESS_LEAVES = registerBlock("cypress_leaves", new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES)));
    public static final Block PURPLE_MUSHROOM = registerBlock("purple_mushroom", new PurpleMushroomBlock(AbstractBlock.Settings.of(Material.PLANT, MapColor.LIME).breakInstantly().noCollision().sounds(BlockSoundGroup.FUNGUS)));
    public static final Block PURPLE_MUSHROOM_BLOCK = registerBlock("purple_mushroom_block", new PurpleMushroomFullBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.LIME).luminance(state -> 6).strength(0.2f).sounds(BlockSoundGroup.SLIME)));
    public static final Block DUCKWEED = registerNoTabBlock("duckweed", new LilyPadBlock(AbstractBlock.Settings.of(Material.REPLACEABLE_PLANT).breakInstantly().sounds(BlockSoundGroup.MOSS_CARPET).noCollision().nonOpaque()));
    public static final Block CATTAIL = registerBlock("cattail", new CattailBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().breakInstantly().sounds(BlockSoundGroup.SMALL_DRIPLEAF).offsetType(AbstractBlock.OffsetType.XYZ)));
    public static final Block POTTED_SWAMP_SAPLING = registerNoTabBlock("potted_swamp_sapling", new FlowerPotBlock(SWAMP_SAPLING, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque()));
    public static final Block POTTED_CYPRESS_SAPLING = registerNoTabBlock("potted_cypress_sapling", new FlowerPotBlock(CYPRESS_SAPLING, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque()));
    public static final Block POTTED_PURPLE_MUSHROOM = registerNoTabBlock("potted_purple_mushroom", new FlowerPotBlock(PURPLE_MUSHROOM, AbstractBlock.Settings.of(Material.DECORATION).breakInstantly().nonOpaque()));
    public static final Block SWAMP_SIGN = registerNoTabBlock("swamp_sign", new SignBlock(AbstractBlock.Settings.of(Material.WOOD, SWAMP_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), GoodEndingSignTypes.SWAMP));
    public static final Block SWAMP_WALL_SIGN = registerNoTabBlock("swamp_wall_sign", new WallSignBlock(AbstractBlock.Settings.of(Material.WOOD, SWAMP_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(SWAMP_SIGN), GoodEndingSignTypes.SWAMP));
    public static final Block CYPRESS_SIGN = registerNoTabBlock("cypress_sign", new SignBlock(FabricBlockSettings.of(Material.WOOD, CYPRESS_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), GoodEndingSignTypes.CYPRESS));
    public static final Block CYPRESS_WALL_SIGN = registerNoTabBlock("cypress_wall_sign", new WallSignBlock(AbstractBlock.Settings.of(Material.WOOD, CYPRESS_LOG.getDefaultMapColor()).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(CYPRESS_SIGN), GoodEndingSignTypes.CYPRESS));

    public static <B extends Block> B registerBlock(String name, B block) {
        Identifier id = new Identifier(GoodEnding.MODID, name);
        BLOCKS.put(id, block);
        GoodEndingItems.ITEMS.put(id, new BlockItem(block, new Item.Settings().group(GoodEnding.TAB)));
        return block;
    }

    public static <B extends Block> B registerNoTabBlock(String name, B block) {
        BLOCKS.put(new Identifier(GoodEnding.MODID, name), block);
        return block;
    }

    public static void init() {
        BLOCKS.forEach((identifier, block) -> Registry.register(Registry.BLOCK, identifier, block));
    }

}
