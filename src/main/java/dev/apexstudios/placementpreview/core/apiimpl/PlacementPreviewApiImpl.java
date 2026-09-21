package dev.apexstudios.placementpreview.core.apiimpl;

import dev.apexstudios.placementpreview.api.PlacementPreview;
import dev.apexstudios.placementpreview.api.handler.MultiBlockItemHandler;
import dev.apexstudios.placementpreview.api.handler.RegisterUseOnHandlersEvent;
import dev.apexstudios.placementpreview.api.handler.UseOnHandler;
import dev.apexstudios.placementpreview.api.provider.BlockStateProvider;
import dev.apexstudios.placementpreview.api.provider.BlockStateProviders;
import dev.apexstudios.placementpreview.api.provider.ConnectionBlockStateProvider;
import dev.apexstudios.placementpreview.api.provider.RegisterBlockStateProvidersEvent;
import dev.apexstudios.placementpreview.api.provider.WallAttachmentBlockStateProvider;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CushionItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemFrameItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.AbstractBedBlock;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.CopperChestBlock;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.InitializeClientRegistriesEvent;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("deprecation")
public final class PlacementPreviewApiImpl implements PlacementPreview {
    // TODO: AT for these debug flag methods should be moved upstream into neoforge
    private final boolean disableFallbackProvider = SharedConstants.debugFlag("PLACEMENT_PREVIEW_DISABLE_FALLBACK_PROVIDER");
    // fallback to try use vanillas 'getStateForPlacement' methods
    // this is a nullable route and under some invalid placements will result in the 'defaultBlockState' being displayed
    // while this may work for most blocks it is still highly recommended to register a provider for your block
    // see 'registerBuiltInBlockStateProviders' below for vanilla registrations
    private final BlockStateProvider fallbackBlockStateProvider = disableFallbackProvider ? BlockStateProvider.SUCCESS : BlockStateProvider.fromVanilla((context, blockState) -> blockState.getBlock().getStateForPlacement(context));
    private final Map<Block, BlockStateProvider> blockStateProviders = new IdentityHashMap<>();
    private final Map<Item, UseOnHandler> useOnHandlers = new IdentityHashMap<>();

    public void register(IEventBus modBus) {
        modBus.addListener(InitializeClientRegistriesEvent.class, event -> registerAll());

        modBus.addListener(EventPriority.HIGH, this::registerBuiltInBlockStateProviders);
        modBus.addListener(EventPriority.HIGH, this::registerBuiltInUseOnHandlers);
    }

    public void registerAll() {
        blockStateProviders.clear();
        useOnHandlers.clear();

        ModLoader.postEvent(new RegisterBlockStateProvidersEvent((block, provider) -> {
            if(blockStateProviders.putIfAbsent(block, provider) != null) {
                throw new IllegalArgumentException("Duplicate BlockStateProvider registration: " + block.builtInRegistryHolder().key().identifier());
            }
        }));

        ModLoader.postEvent(new RegisterUseOnHandlersEvent((item, handler) -> {
            if(useOnHandlers.putIfAbsent(item, handler) != null) {
                throw new IllegalArgumentException("Duplicate UseOnHandler registration: " + item.builtInRegistryHolder().key().identifier());
            }
        }));
    }

    @Override
    public BlockStateProvider getBlockStateProvider(Block block) {
        return blockStateProviders.getOrDefault(block, fallbackBlockStateProvider);
    }

    @Override
    public @Nullable UseOnHandler getUseOnHandler(Item item) {
        return useOnHandlers.get(item);
    }

    private void registerBuiltInBlockStateProviders(RegisterBlockStateProvidersEvent event) {
        event.register(Blocks.GRASS_BLOCK, BlockStateProviders.SNOWY);
        event.register(Blocks.PODZOL, BlockStateProviders.SNOWY);
        event.register(Blocks.PALE_OAK_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.MANGROVE_PROPAGULE, BlockStateProviders.Blocks.MANGROVE_PROPAGULE);
        event.register(Blocks.OAK_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.SPRUCE_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.BIRCH_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.JUNGLE_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.ACACIA_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.CHERRY_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.DARK_OAK_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.PALE_OAK_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.MANGROVE_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.MANGROVE_ROOTS, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.MUDDY_MANGROVE_ROOTS, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.BAMBOO_BLOCK, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_SPRUCE_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_BIRCH_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_JUNGLE_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_ACACIA_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_CHERRY_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_DARK_OAK_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_PALE_OAK_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_OAK_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_MANGROVE_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_BAMBOO_BLOCK, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.OAK_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.SPRUCE_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.BIRCH_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.JUNGLE_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.ACACIA_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.CHERRY_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.DARK_OAK_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.MANGROVE_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_OAK_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_SPRUCE_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_BIRCH_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_JUNGLE_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_ACACIA_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_CHERRY_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_DARK_OAK_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_PALE_OAK_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_MANGROVE_WOOD, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.DISPENSER, BlockStateProviders.FACING_ALT);
        event.register(Blocks.NOTE_BLOCK, BlockStateProviders.NOTE_BLOCK_INTRUMENT);
        event.register(Blocks.BED, BlockStateProviders.HORIZONTAL_FACING);
        event.register(Blocks.POWERED_RAIL, BlockStateProviders.Blocks.POWERED_RAIL);
        event.register(Blocks.DETECTOR_RAIL, BlockStateProviders.Blocks.DETECTOR_RAIL);
        event.register(Blocks.STICKY_PISTON, BlockStateProviders.Blocks.PISTON);
        event.register(Blocks.SEAGRASS, BlockStateProviders.INSIDE_WATER);
        event.register(Blocks.TALL_SEAGRASS, BlockStateProviders.INSIDE_WATER);
        event.register(Blocks.PISTON, BlockStateProviders.Blocks.PISTON);
        event.register(Blocks.CHISELED_BOOKSHELF, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.ACACIA_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.BAMBOO_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.BIRCH_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.CHERRY_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.CRIMSON_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.DARK_OAK_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.JUNGLE_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.MANGROVE_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.OAK_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.PALE_OAK_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.SPRUCE_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.WARPED_SHELF, BlockStateProviders.Blocks.SHELF);
        event.register(Blocks.WALL_TORCH, BlockStateProviders.WALL_ATTACHMENT_ALT);
        event.register(Blocks.FIRE, BlockStateProviders.FIRE_BLOCK);
        event.register(Blocks.CREAKING_HEART, BlockStateProviders.Blocks.CREAKING_HEART);
        event.register(Blocks.REDSTONE_WIRE, BlockStateProviders.Blocks.REDSTONE_WIRE);
        event.register(Blocks.FARMLAND, BlockStateProviders.INVALID_TO_DIRT);
        event.register(Blocks.FURNACE, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.LADDER, BlockStateProviders.Blocks.LADDER);
        event.register(Blocks.RAIL, BlockStateProviders.Blocks.RAIL);
        event.register(Blocks.LEVER, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.REDSTONE_TORCH, BlockStateProviders.REDSTONE_TORCH_LIT);
        event.register(Blocks.REDSTONE_WALL_TORCH, BlockStateProviders.Blocks.REDSTONE_WALL_TORCH);
        event.register(Blocks.SNOW, BlockStateProviders.SNOW_LAYERS);
        event.register(Blocks.BASALT, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.POLISHED_BASALT, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.SOUL_WALL_TORCH, BlockStateProviders.WALL_ATTACHMENT_ALT);
        event.register(Blocks.COPPER_WALL_TORCH, BlockStateProviders.WALL_ATTACHMENT_ALT);
        event.register(Blocks.CARVED_PUMPKIN, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.JACK_O_LANTERN, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.REPEATER, BlockStateProviders.Blocks.REPEATER);
        event.register(Blocks.BROWN_MUSHROOM_BLOCK, ConnectionBlockStateProvider.MUSHROOM);
        event.register(Blocks.RED_MUSHROOM_BLOCK, ConnectionBlockStateProvider.MUSHROOM);
        event.register(Blocks.MUSHROOM_STEM, ConnectionBlockStateProvider.MUSHROOM);
        event.register(Blocks.IRON_BARS, BlockStateProviders.Blocks.IRON_BAR);
        event.register(Blocks.COPPER_BARS, BlockStateProviders.Blocks.IRON_BAR);
        event.register(Blocks.IRON_CHAIN, BlockStateProviders.Blocks.CHAIN);
        event.register(Blocks.COPPER_CHAIN, BlockStateProviders.Blocks.CHAIN);
        event.register(Blocks.GLASS_PANE, BlockStateProviders.Blocks.IRON_BAR);
        event.register(Blocks.VINE, WallAttachmentBlockStateProvider.VINE);
        event.register(Blocks.GLOW_LICHEN, WallAttachmentBlockStateProvider.MULTI_FACE);
        event.register(Blocks.RESIN_CLUMP, WallAttachmentBlockStateProvider.MULTI_FACE);
        event.register(Blocks.MYCELIUM, BlockStateProviders.SNOWY);
        event.register(Blocks.END_PORTAL_FRAME, BlockStateProviders.Blocks.END_PORTAL_FRAME);
        event.register(Blocks.REDSTONE_LAMP, BlockStateProviders.LIT);
        event.register(Blocks.COCOA, BlockStateProviders.WALL_ATTACHMENT);
        event.register(Blocks.ENDER_CHEST, BlockStateProviders.Blocks.ENDER_CHEST);
        event.register(Blocks.TRIPWIRE_HOOK, BlockStateProviders.WALL_ATTACHMENT_ALT);
        event.register(Blocks.TRIPWIRE, ConnectionBlockStateProvider.TRIPWIRE);
        event.register(Blocks.COMMAND_BLOCK, BlockStateProviders.FACING_ALT);
        event.register(Blocks.COMPARATOR, BlockStateProviders.Blocks.COMPARATOR);
        event.register(Blocks.HOPPER, BlockStateProviders.Blocks.HOPPER);
        event.register(Blocks.QUARTZ_PILLAR, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.ACTIVATOR_RAIL, BlockStateProviders.Blocks.POWERED_RAIL);
        event.register(Blocks.DROPPER, BlockStateProviders.FACING_ALT);
        event.register(Blocks.STAINED_GLASS_PANE, ConnectionBlockStateProvider.IRON_BARS);
        event.register(Blocks.BARRIER, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.HAY_BLOCK, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.BANNER, BlockStateProviders.ROTATION_ALT);
        event.register(Blocks.WALL_BANNER, BlockStateProviders.WALL_ATTACHMENT_ALT);
        event.register(Blocks.END_ROD, BlockStateProviders.END_ROD_FACING);
        event.register(Blocks.CHORUS_PLANT, BlockStateProviders.CHORUS_PLANT_CONNECTIONS);
        event.register(Blocks.PURPUR_PILLAR, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.DIRT_PATH, BlockStateProviders.INVALID_TO_DIRT);
        event.register(Blocks.REPEATING_COMMAND_BLOCK, BlockStateProviders.FACING_ALT);
        event.register(Blocks.CHAIN_COMMAND_BLOCK, BlockStateProviders.FACING_ALT);
        event.register(Blocks.BONE_BLOCK, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.OBSERVER, BlockStateProviders.FACING);
        event.register(Blocks.SHULKER_BOX, BlockStateProviders.Blocks.SHULKER_BOX);
        event.register(Blocks.DYED_SHULKER_BOX, BlockStateProviders.Blocks.SHULKER_BOX);
        event.register(Blocks.GLAZED_TERRACOTTA, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.CONCRETE_POWDER, BlockStateProviders.Blocks.CONCRETE_POWDER);
        event.register(Blocks.KELP, BlockStateProviders.Blocks.KELP);
        event.register(Blocks.KELP_PLANT, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.TURTLE_EGG, BlockStateProviders.EGGS);
        event.register(Blocks.DRIED_GHAST, BlockStateProviders.Blocks.DRIED_GHAST);
        event.register(Blocks.DEAD_TUBE_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_BRAIN_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_BUBBLE_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_FIRE_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_HORN_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_TUBE_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_BRAIN_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_BUBBLE_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_FIRE_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_HORN_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.SEA_PICKLE, BlockStateProviders.Blocks.SEA_PICKLE);
        event.register(Blocks.CONDUIT, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.BAMBOO, BlockStateProviders.Blocks.BAMBOO_STALK);
        event.register(Blocks.SCAFFOLDING, BlockStateProviders.Blocks.SCAFFOLDING);
        event.register(Blocks.LOOM, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.BARREL, BlockStateProviders.FACING_ALT);
        event.register(Blocks.SMOKER, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.BLAST_FURNACE, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.GRINDSTONE, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.LECTERN, BlockStateProviders.Blocks.LECTERN);
        event.register(Blocks.STONECUTTER, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.BELL, BlockStateProviders.Blocks.BELL);
        event.register(Blocks.WARPED_STEM, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_WARPED_STEM, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.WARPED_HYPHAE, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_WARPED_HYPHAE, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.CRIMSON_STEM, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_CRIMSON_STEM, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.CRIMSON_HYPHAE, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_CRIMSON_HYPHAE, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.WEEPING_VINES, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.WEEPING_VINES_PLANT, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.TWISTING_VINES, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.TWISTING_VINES_PLANT, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.JIGSAW, BlockStateProviders.Blocks.JIGSAW);
        event.register(Blocks.BEE_NEST, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.BEEHIVE, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.POTENT_SULFUR, BlockStateProviders.Blocks.POTENT_SULFUR);
        event.register(Blocks.SCULK_SENSOR, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.CALIBRATED_SCULK_SENSOR, BlockStateProviders.Blocks.CALIBRATED_SCULK_SENSOR);
        event.register(Blocks.SCULK_VEIN, WallAttachmentBlockStateProvider.MULTI_FACE);
        event.register(Blocks.SCULK_SHRIEKER, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.COPPER_GRATE, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.COPPER_BULB, BlockStateProviders.Blocks.COPPER_BULB);
        event.register(Blocks.COPPER_GOLEM_STATUE, BlockStateProviders.Blocks.COPPER_GOLEM);
        event.register(Blocks.LIGHTNING_ROD, BlockStateProviders.Blocks.LIGHTNING_ROD);
        event.register(Blocks.CAVE_VINES, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.CAVE_VINES_PLANT, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.PINK_PETALS, BlockStateProviders.Blocks.FLOWER_BED);
        event.register(Blocks.WILDFLOWERS, BlockStateProviders.Blocks.FLOWER_BED);
        event.register(Blocks.LEAF_LITTER, BlockStateProviders.Blocks.FLOWER_BED);
        event.register(Blocks.BIG_DRIPLEAF, BlockStateProviders.Blocks.DRIP_LEAF);
        event.register(Blocks.SMALL_DRIPLEAF, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.HANGING_ROOTS, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEEPSLATE, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.INFESTED_DEEPSLATE, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.OCHRE_FROGLIGHT, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.VERDANT_FROGLIGHT, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.PEARLESCENT_FROGLIGHT, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.DECORATED_POT, BlockStateProviders.Blocks.DECORATED_POT);
        event.register(Blocks.CRAFTER, BlockStateProviders.Blocks.CRAFTER);
        event.register(Blocks.VAULT, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.HEAVY_CORE, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.PALE_MOSS_CARPET, BlockStateProviders.Blocks.MOSSY_CARPET);

        Predicate<Block> isCopperChest = CopperChestBlock.class::isInstance;
        Predicate<Block> isChest = AbstractChestBlock.class::isInstance;
        Predicate<Block> isCoralFan = CoralFanBlock.class::isInstance;
        Predicate<Block> isWallCoralFan = BaseCoralWallFanBlock.class::isInstance;
        Predicate<Block> isDeadWallCoralFan = CoralWallFanBlock.class::isInstance;

        registerForEachVanilla(BuiltInRegistries.BLOCK, SlabBlock.class::isInstance, event::register, BlockStateProviders.Blocks.SLAB);
        registerForEachVanilla(BuiltInRegistries.BLOCK, StairBlock.class::isInstance, event::register, BlockStateProviders.Blocks.STAIR);
        registerForEachVanilla(BuiltInRegistries.BLOCK, DoorBlock.class::isInstance, event::register, BlockStateProviders.Blocks.DOOR);
        registerForEachVanilla(BuiltInRegistries.BLOCK, SkullBlock.class::isInstance, event::register, BlockStateProviders.Blocks.SKULL);
        registerForEachVanilla(BuiltInRegistries.BLOCK, WallSkullBlock.class::isInstance, event::register, BlockStateProviders.Blocks.WALL_SKULL);
        registerForEachVanilla(BuiltInRegistries.BLOCK, AnvilBlock.class::isInstance, event::register, BlockStateProviders.HORIZONTAL_FACING_CLOCKWISE);

        registerForEachVanilla(
                BuiltInRegistries.BLOCK,
                isChest.and(isCopperChest.negate())
                        .and(block -> block != Blocks.ENDER_CHEST),
                event::register,
                BlockStateProviders.Blocks.CHEST
        );

        registerForEachVanilla(BuiltInRegistries.BLOCK, isCopperChest, event::register, BlockStateProviders.Blocks.COPPER_CHEST);
        registerForEachVanilla(BuiltInRegistries.BLOCK, ButtonBlock.class::isInstance, event::register, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        registerForEachVanilla(BuiltInRegistries.BLOCK, FenceGateBlock.class::isInstance, event::register, BlockStateProviders.Blocks.FENCE_GATE);
        registerForEachVanilla(BuiltInRegistries.BLOCK, FenceBlock.class::isInstance, event::register, BlockStateProviders.Blocks.FENCE);
        registerForEachVanilla(BuiltInRegistries.BLOCK, TrapDoorBlock.class::isInstance, event::register, BlockStateProviders.Blocks.TRAPDOOR);
        registerForEachVanilla(BuiltInRegistries.BLOCK, WallBlock.class::isInstance, event::register, BlockStateProviders.Blocks.WALL);
        registerForEachVanilla(BuiltInRegistries.BLOCK, StandingSignBlock.class::isInstance, event::register, BlockStateProviders.Blocks.STANDING_SIGN);
        registerForEachVanilla(BuiltInRegistries.BLOCK, WallSignBlock.class::isInstance, event::register, BlockStateProviders.Blocks.WALL_SIGN);
        registerForEachVanilla(BuiltInRegistries.BLOCK, CeilingHangingSignBlock.class::isInstance, event::register, BlockStateProviders.Blocks.HANGING_SIGN);
        registerForEachVanilla(BuiltInRegistries.BLOCK, WallHangingSignBlock.class::isInstance, event::register, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        registerForEachVanilla(BuiltInRegistries.BLOCK, LeavesBlock.class::isInstance, event::register, BlockStateProviders.Blocks.LEAVES);
        registerForEachVanilla(BuiltInRegistries.BLOCK, SpeleothemBlock.class::isInstance, event::register, BlockStateProviders.Blocks.SPELEOTHEM_BLOCK);
        registerForEachVanilla(BuiltInRegistries.BLOCK, AmethystClusterBlock.class::isInstance, event::register, BlockStateProviders.Blocks.AMETHYST_CLUSTER);
        registerForEachVanilla(BuiltInRegistries.BLOCK, CandleBlock.class::isInstance, event::register, BlockStateProviders.Blocks.CANDLE);
        registerForEachVanilla(BuiltInRegistries.BLOCK, CampfireBlock.class::isInstance, event::register, BlockStateProviders.Blocks.CAMPFIRE);
        registerForEachVanilla(BuiltInRegistries.BLOCK, LanternBlock.class::isInstance, event::register, BlockStateProviders.Blocks.LANTERN);
        registerForEachVanilla(BuiltInRegistries.BLOCK, CoralBlock.class::isInstance, event::register, BlockStateProviders.Blocks.CORAL);
        registerForEachVanilla(BuiltInRegistries.BLOCK, CoralPlantBlock.class::isInstance, event::register, BlockStateProviders.Blocks.CORAL_PLANT);
        registerForEachVanilla(BuiltInRegistries.BLOCK, isCoralFan.and(isWallCoralFan.negate()), event::register, BlockStateProviders.Blocks.CORAL_FAN);
        registerForEachVanilla(BuiltInRegistries.BLOCK, isWallCoralFan, event::register, BlockStateProviders.Blocks.DEAD_WALL_CORAL_FAN);
        registerForEachVanilla(BuiltInRegistries.BLOCK, isDeadWallCoralFan.and(isWallCoralFan.negate()), event::register, BlockStateProviders.Blocks.WALL_CORAL_FAN);
    }

    private void registerBuiltInUseOnHandlers(RegisterUseOnHandlersEvent event) {
        Predicate<Item> needsPlacingOnWater = item -> item == Items.LILY_PAD || item == Items.FROGSPAWN;
        Predicate<Item> isPiston = item -> item == Items.PISTON || item == Items.STICKY_PISTON;
        Predicate<Item> isStandingWall = StandingAndWallBlockItem.class::isInstance;
        Predicate<Item> isDoubleHigh = DoubleHighBlockItem.class::isInstance;
        Predicate<Item> isBed = isBlockItemWithBlock(AbstractBedBlock.class::isInstance);
        Predicate<Item> isBlockItem = BlockItem.class::isInstance;

        event.register(Items.ARMOR_STAND, new ArmorStandHandler());
        event.register(Items.END_CRYSTAL, new EndCrystalHandler());
        event.register(Items.PAINTING, new PaintingHandler());

        registerForEachVanilla(
                BuiltInRegistries.ITEM,
                isBlockItem.and(needsPlacingOnWater.negate())
                        .and(isStandingWall.negate())
                        .and(isBed.negate())
                        .and(isDoubleHigh.negate())
                        .and(isPiston.negate()),
                event::register,
                UseOnHandler.BLOCK_ITEM
        );
        registerForEachVanilla(BuiltInRegistries.ITEM, isStandingWall, event::register, UseOnHandler.STANDING_WALL);
        registerForEachVanilla(BuiltInRegistries.ITEM, needsPlacingOnWater, event::register, UseOnHandler.PLACE_ON_WATER);
        registerForEachVanilla(BuiltInRegistries.ITEM, isDoubleHigh, event::register, MultiBlockItemHandler.DOUBLE_HIGH);
        registerForEachVanilla(BuiltInRegistries.ITEM, isBed, event::register, MultiBlockItemHandler.BED);
        registerForEachVanilla(BuiltInRegistries.ITEM, isPiston, event::register, MultiBlockItemHandler.PISTON);
        registerForEachVanilla(BuiltInRegistries.ITEM, SpawnEggItem.class::isInstance, event::register, UseOnHandler.SPAWN_EGG);
        registerForEachVanilla(BuiltInRegistries.ITEM, CushionItem.class::isInstance, event::register, UseOnHandler.CUSHION);
        registerForEachVanilla(BuiltInRegistries.ITEM, ItemFrameItem.class::isInstance, event::register, new ItemFrameHandler());

        // TODO: handlers for the following items
        // Overriders of `Item.useOn`
        //  AxeItem
        //  BoneMealItem
        //  DebugStickItem
        //  EnderEyeItem*
        //  FireChargeItem
        //  FireworkRocketItem*
        //  FlintAndSteelItem
        //  HoeItem
        //  HoneycombItem
        //  LeadITem
        //  MapItem
        //  MinecartItem
        //  PotionItem
        //  ShearsItem
        //  ShovelItem
        //  SolidBucketItem
        // Overriders of `Item.use`
        //  BoatItem
        //  BucketItem
        //  EggItem*
        //  EnderEyeItem*
        //  EnderpearlItem*
        //  ExperienceBottleItem*
        //  LingeringPotionItem*
        //  SnowballItem*
        //  SplashPotionItem*
        //  ThrowablePotionItem*
        //  WindChargeItem*
        // * - questionable, summons projectile entity at players eyes
    }

    private <TKey, TValue> void registerForEachVanilla(Registry<TKey> registry, Predicate<TKey> filter, BiConsumer<TKey, TValue> registrar, TValue value) {
        for(var key : registry) {
            var registryKey = registry.getKey(key);

            if(registryKey == null || !isVanilla(registryKey)) {
                continue;
            }

            if(filter.test(key)) {
                registrar.accept(key, value);
            }
        }
    }

    private Predicate<Item> isBlockItemWithBlock(Predicate<Block> test) {
        return item -> item instanceof BlockItem blockItem && test.test(blockItem.getBlock());
    }

    private boolean isVanilla(Identifier registryName) {
        return registryName.getNamespace().equals(Identifier.DEFAULT_NAMESPACE);
    }
}
