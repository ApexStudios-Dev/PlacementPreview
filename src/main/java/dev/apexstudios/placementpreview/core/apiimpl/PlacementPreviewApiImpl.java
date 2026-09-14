package dev.apexstudios.placementpreview.core.apiimpl;

import dev.apexstudios.placementpreview.api.PlacementPreview;
import dev.apexstudios.placementpreview.api.PsudeoRegistry;
import dev.apexstudios.placementpreview.api.handler.MultiBlockItemHandler;
import dev.apexstudios.placementpreview.api.handler.RegisterUseOnHandlersEvent;
import dev.apexstudios.placementpreview.api.handler.UseOnHandler;
import dev.apexstudios.placementpreview.api.provider.BlockStateProvider;
import dev.apexstudios.placementpreview.api.provider.BlockStateProviders;
import dev.apexstudios.placementpreview.api.provider.ConnectionBlockStateProvider;
import dev.apexstudios.placementpreview.api.provider.RegisterBlockStateProvidersEvent;
import dev.apexstudios.placementpreview.api.provider.WallAttachmentBlockStateProvider;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.InitializeClientRegistriesEvent;

@SuppressWarnings("deprecation")
public final class PlacementPreviewApiImpl implements PlacementPreview {
    // TODO: AT for these debug flag methods should be moved upstream into neoforge
    private final boolean disableFallbackProvider = SharedConstants.debugFlag("PLACEMENT_PREVIEW_DISABLE_FALLBACK_PROVIDER");
    // fallback to try use vanillas 'getStateForPlacement' methods
    // this is a nullable route and under some invalid placements will result in the 'defaultBlockState' being displayed
    // while this may work for most blocks it is still highly recommended to register a provider for your block
    // see 'registerBuiltInBlockStateProviders' below for vanilla registrations
    private final BlockStateProvider fallbackBlockStateProvider = disableFallbackProvider ? BlockStateProvider.SUCCESS : BlockStateProvider.fromVanilla((context, blockState) -> blockState.getBlock().getStateForPlacement(context));

    private final PsudeoRegistryImpl.Keyed.Defaulted<Block, BlockStateProvider, RegisterBlockStateProvidersEvent> blockStateProviders = new PsudeoRegistryImpl.Keyed.Defaulted<>(
            RegisterBlockStateProvidersEvent::new,
            Block::builtInRegistryHolder,
            "BlockStateProvider",
            fallbackBlockStateProvider
    );

    private final PsudeoRegistryImpl.Keyed.NoDefault<Item, UseOnHandler, RegisterUseOnHandlersEvent> useOnHandlers = new PsudeoRegistryImpl.Keyed.NoDefault<>(
            RegisterUseOnHandlersEvent::new,
            Item::builtInRegistryHolder,
            "UseOnHandler"
    );

    public void register(IEventBus modBus) {
        modBus.addListener(InitializeClientRegistriesEvent.class, event -> registerAll());

        modBus.addListener(EventPriority.HIGH, this::registerBuiltInBlockStateProviders);
        modBus.addListener(EventPriority.HIGH, this::registerBuiltInUseOnHandlers);
    }

    public void registerAll() {
        blockStateProviders.register();
        useOnHandlers.register();
    }

    @Override
    public PsudeoRegistry.Keyed.Defaulted<Block, BlockStateProvider> blockStateProviders() {
        return blockStateProviders;
    }

    @Override
    public PsudeoRegistry.Keyed.NoDefault<Item, UseOnHandler> useOnHandlers() {
        return useOnHandlers;
    }

    private void registerBuiltInBlockStateProviders(RegisterBlockStateProvidersEvent event) {
        // event.register(Blocks.AIR, );
        // event.register(Blocks.STONE, );
        // event.register(Blocks.GRANITE, );
        // event.register(Blocks.POLISHED_GRANITE, );
        // event.register(Blocks.DIORITE, );
        // event.register(Blocks.POLISHED_DIORITE, );
        // event.register(Blocks.ANDESITE, );
        // event.register(Blocks.POLISHED_ANDESITE, );
        event.register(Blocks.GRASS_BLOCK, BlockStateProviders.SNOWY);
        // event.register(Blocks.DIRT, );
        // event.register(Blocks.COARSE_DIRT, );
        event.register(Blocks.PODZOL, BlockStateProviders.SNOWY);
        // event.register(Blocks.COBBLESTONE, );
        // event.register(Blocks.OAK_PLANKS, );
        // event.register(Blocks.SPRUCE_PLANKS, );
        // event.register(Blocks.BIRCH_PLANKS, );
        // event.register(Blocks.JUNGLE_PLANKS, );
        // event.register(Blocks.ACACIA_PLANKS, );
        // event.register(Blocks.CHERRY_PLANKS, );
        // event.register(Blocks.DARK_OAK_PLANKS, );
        event.register(Blocks.PALE_OAK_WOOD, BlockStateProviders.ROTATED_PILLAR);
        // event.register(Blocks.PALE_OAK_PLANKS, );
        // event.register(Blocks.MANGROVE_PLANKS, );
        // event.register(Blocks.BAMBOO_PLANKS, );
        // event.register(Blocks.BAMBOO_MOSAIC, );
        // event.register(Blocks.OAK_SAPLING, );
        // event.register(Blocks.SPRUCE_SAPLING, );
        // event.register(Blocks.BIRCH_SAPLING, );
        // event.register(Blocks.JUNGLE_SAPLING, );
        // event.register(Blocks.ACACIA_SAPLING, );
        // event.register(Blocks.CHERRY_SAPLING, );
        // event.register(Blocks.DARK_OAK_SAPLING, );
        // event.register(Blocks.PALE_OAK_SAPLING, );
        event.register(Blocks.MANGROVE_PROPAGULE, BlockStateProviders.Blocks.MANGROVE_PROPAGULE);
        // event.register(Blocks.BEDROCK, );
        // event.register(Blocks.WATER, );
        // event.register(Blocks.LAVA, );
        // event.register(Blocks.SAND, );
        // event.register(Blocks.SUSPICIOUS_SAND, );
        // event.register(Blocks.RED_SAND, );
        // event.register(Blocks.GRAVEL, );
        // event.register(Blocks.SUSPICIOUS_GRAVEL, );
        // event.register(Blocks.GOLD_ORE, );
        // event.register(Blocks.DEEPSLATE_GOLD_ORE, );
        // event.register(Blocks.IRON_ORE, );
        // event.register(Blocks.DEEPSLATE_IRON_ORE, );
        // event.register(Blocks.COAL_ORE, );
        // event.register(Blocks.DEEPSLATE_COAL_ORE, );
        // event.register(Blocks.NETHER_GOLD_ORE, );
        event.register(Blocks.OAK_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.SPRUCE_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.BIRCH_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.JUNGLE_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.ACACIA_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.CHERRY_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.DARK_OAK_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.PALE_OAK_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.MANGROVE_LOG, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.MANGROVE_ROOTS, BlockStateProviders.ROTATED_PILLAR);
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
        event.register(Blocks.OAK_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.SPRUCE_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.BIRCH_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.JUNGLE_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.ACACIA_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.CHERRY_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.DARK_OAK_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.PALE_OAK_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.MANGROVE_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.AZALEA_LEAVES, BlockStateProviders.Blocks.LEAVES);
        event.register(Blocks.FLOWERING_AZALEA_LEAVES, BlockStateProviders.Blocks.LEAVES);
        // event.register(Blocks.SPONGE, );
        // event.register(Blocks.WET_SPONGE, );
        // event.register(Blocks.GLASS, );
        // event.register(Blocks.LAPIS_ORE, );
        // event.register(Blocks.DEEPSLATE_LAPIS_ORE, );
        // event.register(Blocks.LAPIS_BLOCK, );
        event.register(Blocks.DISPENSER, BlockStateProviders.FACING_ALT);
        // event.register(Blocks.SANDSTONE, );
        // event.register(Blocks.CHISELED_SANDSTONE, );
        // event.register(Blocks.CUT_SANDSTONE, );
        event.register(Blocks.NOTE_BLOCK, BlockStateProviders.NOTE_BLOCK_INTRUMENT);
        event.register(Blocks.BED, BlockStateProviders.HORIZONTAL_FACING);
        event.register(Blocks.POWERED_RAIL, BlockStateProviders.Blocks.POWERED_RAIL);
        event.register(Blocks.DETECTOR_RAIL, BlockStateProviders.Blocks.DETECTOR_RAIL);
        event.register(Blocks.STICKY_PISTON, BlockStateProviders.Blocks.PISTON);
        // event.register(Blocks.COBWEB, );
        // event.register(Blocks.SHORT_GRASS, );
        // event.register(Blocks.FERN, );
        // event.register(Blocks.DEAD_BUSH, );
        // event.register(Blocks.BUSH, );
        // event.register(Blocks.SHORT_DRY_GRASS, );
        // event.register(Blocks.TALL_DRY_GRASS, );
        event.register(Blocks.SEAGRASS, BlockStateProviders.INSIDE_WATER);
        event.register(Blocks.TALL_SEAGRASS, BlockStateProviders.INSIDE_WATER);
        event.register(Blocks.PISTON, BlockStateProviders.Blocks.PISTON);
        // event.register(Blocks.PISTON_HEAD, );
        // event.register(Blocks.WOOL, );
        // event.register(Blocks.MOVING_PISTON, );
        // event.register(Blocks.DANDELION, );
        // event.register(Blocks.GOLDEN_DANDELION, );
        // event.register(Blocks.TORCHFLOWER, );
        // event.register(Blocks.POPPY, );
        // event.register(Blocks.BLUE_ORCHID, );
        // event.register(Blocks.ALLIUM, );
        // event.register(Blocks.AZURE_BLUET, );
        // event.register(Blocks.RED_TULIP, );
        // event.register(Blocks.ORANGE_TULIP, );
        // event.register(Blocks.WHITE_TULIP, );
        // event.register(Blocks.PINK_TULIP, );
        // event.register(Blocks.OXEYE_DAISY, );
        // event.register(Blocks.CORNFLOWER, );
        // event.register(Blocks.WITHER_ROSE, );
        // event.register(Blocks.LILY_OF_THE_VALLEY, );
        // event.register(Blocks.BROWN_MUSHROOM, );
        // event.register(Blocks.RED_MUSHROOM, );
        // event.register(Blocks.GOLD_BLOCK, );
        // event.register(Blocks.IRON_BLOCK, );
        // event.register(Blocks.BRICKS, );
        // event.register(Blocks.TNT, );
        // event.register(Blocks.BOOKSHELF, );
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
        // event.register(Blocks.MOSSY_COBBLESTONE, );
        // event.register(Blocks.OBSIDIAN, );
        // event.register(Blocks.TORCH, );
        event.register(Blocks.WALL_TORCH, BlockStateProviders.WALL_ATTACHMENT_ALT);
        event.register(Blocks.FIRE, BlockStateProviders.FIRE_BLOCK);
        // event.register(Blocks.SOUL_FIRE, );
        // event.register(Blocks.SPAWNER, );
        event.register(Blocks.CREAKING_HEART, BlockStateProviders.Blocks.CREAKING_HEART);
        event.register(Blocks.OAK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.CHEST, BlockStateProviders.Blocks.CHEST);
        event.register(Blocks.REDSTONE_WIRE, BlockStateProviders.Blocks.REDSTONE_WIRE);
        // event.register(Blocks.DIAMOND_ORE, );
        // event.register(Blocks.DEEPSLATE_DIAMOND_ORE, );
        // event.register(Blocks.DIAMOND_BLOCK, );
        // event.register(Blocks.CRAFTING_TABLE, );
        // event.register(Blocks.WHEAT, );
        event.register(Blocks.FARMLAND, BlockStateProviders.INVALID_TO_DIRT);
        event.register(Blocks.FURNACE, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.OAK_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.SPRUCE_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.BIRCH_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.ACACIA_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.CHERRY_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.JUNGLE_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.DARK_OAK_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.PALE_OAK_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.MANGROVE_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.BAMBOO_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.OAK_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.LADDER, BlockStateProviders.Blocks.LADDER);
        event.register(Blocks.RAIL, BlockStateProviders.Blocks.RAIL);
        event.register(Blocks.COBBLESTONE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.OAK_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.SPRUCE_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.BIRCH_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.ACACIA_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.CHERRY_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.JUNGLE_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.DARK_OAK_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.PALE_OAK_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.MANGROVE_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.BAMBOO_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.OAK_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.SPRUCE_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.BIRCH_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.ACACIA_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.CHERRY_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.JUNGLE_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.DARK_OAK_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.PALE_OAK_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.CRIMSON_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.WARPED_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.MANGROVE_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.BAMBOO_HANGING_SIGN, BlockStateProviders.Blocks.HANGING_SIGN);
        event.register(Blocks.OAK_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.SPRUCE_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.BIRCH_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.ACACIA_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.CHERRY_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.JUNGLE_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.DARK_OAK_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.PALE_OAK_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.MANGROVE_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.CRIMSON_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.WARPED_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.BAMBOO_WALL_HANGING_SIGN, BlockStateProviders.Blocks.WALL_HANGING_SIGN);
        event.register(Blocks.LEVER, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        // event.register(Blocks.STONE_PRESSURE_PLATE, );
        event.register(Blocks.IRON_DOOR, BlockStateProviders.Blocks.DOOR);
        // event.register(Blocks.OAK_PRESSURE_PLATE, );
        // event.register(Blocks.SPRUCE_PRESSURE_PLATE, );
        // event.register(Blocks.BIRCH_PRESSURE_PLATE, );
        // event.register(Blocks.JUNGLE_PRESSURE_PLATE, );
        // event.register(Blocks.ACACIA_PRESSURE_PLATE, );
        // event.register(Blocks.CHERRY_PRESSURE_PLATE, );
        // event.register(Blocks.DARK_OAK_PRESSURE_PLATE, );
        // event.register(Blocks.PALE_OAK_PRESSURE_PLATE, );
        // event.register(Blocks.MANGROVE_PRESSURE_PLATE, );
        // event.register(Blocks.BAMBOO_PRESSURE_PLATE, );
        // event.register(Blocks.REDSTONE_ORE, );
        // event.register(Blocks.DEEPSLATE_REDSTONE_ORE, );
        event.register(Blocks.REDSTONE_TORCH, BlockStateProviders.REDSTONE_TORCH_LIT);
        event.register(Blocks.REDSTONE_WALL_TORCH, BlockStateProviders.Blocks.REDSTONE_WALL_TORCH);
        event.register(Blocks.STONE_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.SNOW, BlockStateProviders.SNOW_LAYERS);
        // event.register(Blocks.ICE, );
        // event.register(Blocks.SNOW_BLOCK, );
        // event.register(Blocks.CACTUS, );
        // event.register(Blocks.CACTUS_FLOWER, );
        // event.register(Blocks.CLAY, );
        // event.register(Blocks.SUGAR_CANE, );
        // event.register(Blocks.JUKEBOX, );
        event.register(Blocks.OAK_FENCE, BlockStateProviders.Blocks.FENCE);
        // event.register(Blocks.NETHERRACK, );
        // event.register(Blocks.SOUL_SAND, );
        // event.register(Blocks.SOUL_SOIL, );
        event.register(Blocks.BASALT, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.POLISHED_BASALT, BlockStateProviders.ROTATED_PILLAR);
        // event.register(Blocks.SOUL_TORCH, );
        event.register(Blocks.SOUL_WALL_TORCH, BlockStateProviders.WALL_ATTACHMENT_ALT);
        // event.register(Blocks.COPPER_TORCH, );
        event.register(Blocks.COPPER_WALL_TORCH, BlockStateProviders.WALL_ATTACHMENT_ALT);
        // event.register(Blocks.GLOWSTONE, );
        // event.register(Blocks.NETHER_PORTAL, );
        event.register(Blocks.CARVED_PUMPKIN, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.JACK_O_LANTERN, BlockStateProviders.HORIZONTAL_FACING_ALT);
        // event.register(Blocks.CAKE, );
        event.register(Blocks.REPEATER, BlockStateProviders.Blocks.REPEATER);
        // event.register(Blocks.STAINED_GLASS, );
        event.register(Blocks.OAK_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.SPRUCE_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.BIRCH_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.JUNGLE_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.ACACIA_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.CHERRY_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.DARK_OAK_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.PALE_OAK_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.MANGROVE_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.BAMBOO_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        // event.register(Blocks.STONE_BRICKS, );
        // event.register(Blocks.MOSSY_STONE_BRICKS, );
        // event.register(Blocks.CRACKED_STONE_BRICKS, );
        // event.register(Blocks.CHISELED_STONE_BRICKS, );
        // event.register(Blocks.PACKED_MUD, );
        // event.register(Blocks.MUD_BRICKS, );
        // event.register(Blocks.INFESTED_STONE, );
        // event.register(Blocks.INFESTED_COBBLESTONE, );
        // event.register(Blocks.INFESTED_STONE_BRICKS, );
        // event.register(Blocks.INFESTED_MOSSY_STONE_BRICKS, );
        // event.register(Blocks.INFESTED_CRACKED_STONE_BRICKS, );
        // event.register(Blocks.INFESTED_CHISELED_STONE_BRICKS, );
        event.register(Blocks.BROWN_MUSHROOM_BLOCK, ConnectionBlockStateProvider.MUSHROOM);
        event.register(Blocks.RED_MUSHROOM_BLOCK, ConnectionBlockStateProvider.MUSHROOM);
        event.register(Blocks.MUSHROOM_STEM, ConnectionBlockStateProvider.MUSHROOM);
        event.register(Blocks.IRON_BARS, BlockStateProviders.Blocks.IRON_BAR);
        event.register(Blocks.COPPER_BARS, BlockStateProviders.Blocks.IRON_BAR);
        event.register(Blocks.IRON_CHAIN, BlockStateProviders.Blocks.CHAIN);
        event.register(Blocks.COPPER_CHAIN, BlockStateProviders.Blocks.CHAIN);
        event.register(Blocks.GLASS_PANE, BlockStateProviders.Blocks.IRON_BAR);
        // event.register(Blocks.PUMPKIN, );
        // event.register(Blocks.MELON, );
        // event.register(Blocks.ATTACHED_PUMPKIN_STEM, );
        // event.register(Blocks.ATTACHED_MELON_STEM, );
        // event.register(Blocks.PUMPKIN_STEM, );
        // event.register(Blocks.MELON_STEM, );
        event.register(Blocks.VINE, WallAttachmentBlockStateProvider.VINE);
        event.register(Blocks.GLOW_LICHEN, WallAttachmentBlockStateProvider.MULTI_FACE);
        event.register(Blocks.RESIN_CLUMP, WallAttachmentBlockStateProvider.MULTI_FACE);
        event.register(Blocks.OAK_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.STONE_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.MUD_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.MYCELIUM, BlockStateProviders.SNOWY);
        // event.register(Blocks.LILY_PAD, );
        // event.register(Blocks.RESIN_BLOCK, );
        // event.register(Blocks.RESIN_BRICKS, );
        event.register(Blocks.RESIN_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.RESIN_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.RESIN_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.CHISELED_RESIN_BRICKS, );
        // event.register(Blocks.NETHER_BRICKS, );
        event.register(Blocks.NETHER_BRICK_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.NETHER_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        // event.register(Blocks.NETHER_WART, );
        // event.register(Blocks.ENCHANTING_TABLE, );
        // event.register(Blocks.BREWING_STAND, );
        // event.register(Blocks.CAULDRON, );
        // event.register(Blocks.WATER_CAULDRON, );
        // event.register(Blocks.LAVA_CAULDRON, );
        // event.register(Blocks.POWDER_SNOW_CAULDRON, );
        // event.register(Blocks.END_PORTAL, );
        event.register(Blocks.END_PORTAL_FRAME, BlockStateProviders.Blocks.END_PORTAL_FRAME);
        // event.register(Blocks.END_STONE, );
        // event.register(Blocks.DRAGON_EGG, );
        event.register(Blocks.REDSTONE_LAMP, BlockStateProviders.LIT);
        event.register(Blocks.COCOA, BlockStateProviders.WALL_ATTACHMENT);
        event.register(Blocks.SANDSTONE_STAIRS, BlockStateProviders.Blocks.STAIR);
        // event.register(Blocks.EMERALD_ORE, );
        // event.register(Blocks.DEEPSLATE_EMERALD_ORE, );
        event.register(Blocks.ENDER_CHEST, BlockStateProviders.Blocks.ENDER_CHEST);
        event.register(Blocks.TRIPWIRE_HOOK, BlockStateProviders.WALL_ATTACHMENT_ALT);
        event.register(Blocks.TRIPWIRE, ConnectionBlockStateProvider.TRIPWIRE);
        // event.register(Blocks.EMERALD_BLOCK, );
        event.register(Blocks.SPRUCE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.BIRCH_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.JUNGLE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.COMMAND_BLOCK, BlockStateProviders.FACING_ALT);
        // event.register(Blocks.BEACON, );
        event.register(Blocks.COBBLESTONE_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.MOSSY_COBBLESTONE_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.FLOWER_POT, );
        // event.register(Blocks.POTTED_TORCHFLOWER, );
        // event.register(Blocks.POTTED_OAK_SAPLING, );
        // event.register(Blocks.POTTED_SPRUCE_SAPLING, );
        // event.register(Blocks.POTTED_BIRCH_SAPLING, );
        // event.register(Blocks.POTTED_JUNGLE_SAPLING, );
        // event.register(Blocks.POTTED_ACACIA_SAPLING, );
        // event.register(Blocks.POTTED_CHERRY_SAPLING, );
        // event.register(Blocks.POTTED_DARK_OAK_SAPLING, );
        // event.register(Blocks.POTTED_PALE_OAK_SAPLING, );
        // event.register(Blocks.POTTED_MANGROVE_PROPAGULE, );
        // event.register(Blocks.POTTED_FERN, );
        // event.register(Blocks.POTTED_DANDELION, );
        // event.register(Blocks.POTTED_GOLDEN_DANDELION, );
        // event.register(Blocks.POTTED_POPPY, );
        // event.register(Blocks.POTTED_BLUE_ORCHID, );
        // event.register(Blocks.POTTED_ALLIUM, );
        // event.register(Blocks.POTTED_AZURE_BLUET, );
        // event.register(Blocks.POTTED_RED_TULIP, );
        // event.register(Blocks.POTTED_ORANGE_TULIP, );
        // event.register(Blocks.POTTED_WHITE_TULIP, );
        // event.register(Blocks.POTTED_PINK_TULIP, );
        // event.register(Blocks.POTTED_OXEYE_DAISY, );
        // event.register(Blocks.POTTED_CORNFLOWER, );
        // event.register(Blocks.POTTED_LILY_OF_THE_VALLEY, );
        // event.register(Blocks.POTTED_WITHER_ROSE, );
        // event.register(Blocks.POTTED_RED_MUSHROOM, );
        // event.register(Blocks.POTTED_BROWN_MUSHROOM, );
        // event.register(Blocks.POTTED_DEAD_BUSH, );
        // event.register(Blocks.POTTED_CACTUS, );
        // event.register(Blocks.CARROTS, );
        // event.register(Blocks.POTATOES, );
        event.register(Blocks.OAK_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.SPRUCE_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.BIRCH_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.JUNGLE_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.ACACIA_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.CHERRY_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.DARK_OAK_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.PALE_OAK_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.MANGROVE_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.BAMBOO_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.SKELETON_SKULL, BlockStateProviders.Blocks.SKULL);
        event.register(Blocks.SKELETON_WALL_SKULL, BlockStateProviders.Blocks.WALL_SKULL);
        event.register(Blocks.WITHER_SKELETON_SKULL, BlockStateProviders.Blocks.SKULL);
        event.register(Blocks.WITHER_SKELETON_WALL_SKULL, BlockStateProviders.Blocks.WALL_SKULL);
        event.register(Blocks.ZOMBIE_HEAD, BlockStateProviders.Blocks.SKULL);
        event.register(Blocks.ZOMBIE_WALL_HEAD, BlockStateProviders.Blocks.WALL_SKULL);
        event.register(Blocks.PLAYER_HEAD, BlockStateProviders.Blocks.SKULL);
        event.register(Blocks.PLAYER_WALL_HEAD, BlockStateProviders.Blocks.WALL_SKULL);
        event.register(Blocks.CREEPER_HEAD, BlockStateProviders.Blocks.SKULL);
        event.register(Blocks.CREEPER_WALL_HEAD, BlockStateProviders.Blocks.WALL_SKULL);
        event.register(Blocks.DRAGON_HEAD, BlockStateProviders.Blocks.SKULL);
        event.register(Blocks.DRAGON_WALL_HEAD, BlockStateProviders.Blocks.WALL_SKULL);
        event.register(Blocks.PIGLIN_HEAD, BlockStateProviders.Blocks.SKULL);
        event.register(Blocks.PIGLIN_WALL_HEAD, BlockStateProviders.Blocks.WALL_SKULL);
        event.register(Blocks.ANVIL, BlockStateProviders.HORIZONTAL_FACING_CLOCKWISE);
        event.register(Blocks.CHIPPED_ANVIL, BlockStateProviders.HORIZONTAL_FACING_CLOCKWISE);
        event.register(Blocks.DAMAGED_ANVIL, BlockStateProviders.HORIZONTAL_FACING_CLOCKWISE);
        event.register(Blocks.TRAPPED_CHEST, BlockStateProviders.Blocks.CHEST);
        // event.register(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, );
        // event.register(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, );
        event.register(Blocks.COMPARATOR, BlockStateProviders.Blocks.COMPARATOR);
        // event.register(Blocks.DAYLIGHT_DETECTOR, );
        // event.register(Blocks.REDSTONE_BLOCK, );
        // event.register(Blocks.NETHER_QUARTZ_ORE, );
        event.register(Blocks.HOPPER, BlockStateProviders.Blocks.HOPPER);
        // event.register(Blocks.QUARTZ_BLOCK, );
        // event.register(Blocks.CHISELED_QUARTZ_BLOCK, );
        event.register(Blocks.QUARTZ_PILLAR, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.QUARTZ_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.ACTIVATOR_RAIL, BlockStateProviders.Blocks.POWERED_RAIL);
        event.register(Blocks.DROPPER, BlockStateProviders.FACING_ALT);
        // event.register(Blocks.DYED_TERRACOTTA, );
        event.register(Blocks.STAINED_GLASS_PANE, ConnectionBlockStateProvider.IRON_BARS);
        event.register(Blocks.ACACIA_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.CHERRY_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.DARK_OAK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.PALE_OAK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.MANGROVE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.BAMBOO_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.BAMBOO_MOSAIC_STAIRS, BlockStateProviders.Blocks.STAIR);
        // event.register(Blocks.SLIME_BLOCK, );
        event.register(Blocks.BARRIER, BlockStateProviders.WATERLOGGED);
        // event.register(Blocks.LIGHT, );
        event.register(Blocks.IRON_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        // event.register(Blocks.PRISMARINE, );
        // event.register(Blocks.PRISMARINE_BRICKS, );
        // event.register(Blocks.DARK_PRISMARINE, );
        event.register(Blocks.PRISMARINE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.PRISMARINE_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.DARK_PRISMARINE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.PRISMARINE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.PRISMARINE_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.DARK_PRISMARINE_SLAB, BlockStateProviders.Blocks.SLAB);
        // event.register(Blocks.SEA_LANTERN, );
        event.register(Blocks.HAY_BLOCK, BlockStateProviders.ROTATED_PILLAR);
        // event.register(Blocks.CARPET, );
        // event.register(Blocks.TERRACOTTA, );
        // event.register(Blocks.COAL_BLOCK, );
        // event.register(Blocks.PACKED_ICE, );
        // event.register(Blocks.SUNFLOWER, );
        // event.register(Blocks.LILAC, );
        // event.register(Blocks.ROSE_BUSH, );
        // event.register(Blocks.PEONY, );
        // event.register(Blocks.TALL_GRASS, );
        // event.register(Blocks.LARGE_FERN, );
        event.register(Blocks.BANNER, BlockStateProviders.ROTATION_ALT);
        event.register(Blocks.WALL_BANNER, BlockStateProviders.WALL_ATTACHMENT_ALT);
        // event.register(Blocks.RED_SANDSTONE, );
        // event.register(Blocks.CHISELED_RED_SANDSTONE, );
        // event.register(Blocks.CUT_RED_SANDSTONE, );
        event.register(Blocks.RED_SANDSTONE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.OAK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.SPRUCE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.BIRCH_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.JUNGLE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.ACACIA_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.CHERRY_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.DARK_OAK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.PALE_OAK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.MANGROVE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.BAMBOO_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.BAMBOO_MOSAIC_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.STONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.SMOOTH_STONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.SANDSTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.CUT_SANDSTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.PETRIFIED_OAK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.COBBLESTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.STONE_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.MUD_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.NETHER_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.QUARTZ_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.RED_SANDSTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.CUT_RED_SANDSTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.PURPUR_SLAB, BlockStateProviders.Blocks.SLAB);
        // event.register(Blocks.SMOOTH_STONE, );
        // event.register(Blocks.SMOOTH_SANDSTONE, );
        // event.register(Blocks.SMOOTH_QUARTZ, );
        // event.register(Blocks.SMOOTH_RED_SANDSTONE, );
        event.register(Blocks.SPRUCE_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.BIRCH_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.JUNGLE_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.ACACIA_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.CHERRY_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.DARK_OAK_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.PALE_OAK_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.MANGROVE_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.BAMBOO_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.SPRUCE_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.BIRCH_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.JUNGLE_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.ACACIA_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.CHERRY_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.DARK_OAK_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.PALE_OAK_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.MANGROVE_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.BAMBOO_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.SPRUCE_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.BIRCH_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.JUNGLE_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.ACACIA_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.CHERRY_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.DARK_OAK_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.PALE_OAK_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.MANGROVE_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.BAMBOO_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.END_ROD, BlockStateProviders.END_ROD_FACING);
        event.register(Blocks.CHORUS_PLANT, BlockStateProviders.CHORUS_PLANT_CONNECTIONS);
        // event.register(Blocks.CHORUS_FLOWER, );
        // event.register(Blocks.PURPUR_BLOCK, );
        event.register(Blocks.PURPUR_PILLAR, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.PURPUR_STAIRS, BlockStateProviders.Blocks.STAIR);
        // event.register(Blocks.END_STONE_BRICKS, );
        // event.register(Blocks.TORCHFLOWER_CROP, );
        // event.register(Blocks.PITCHER_CROP, );
        // event.register(Blocks.PITCHER_PLANT, );
        // event.register(Blocks.BEETROOTS, );
        event.register(Blocks.DIRT_PATH, BlockStateProviders.INVALID_TO_DIRT);
        // event.register(Blocks.END_GATEWAY, );
        event.register(Blocks.REPEATING_COMMAND_BLOCK, BlockStateProviders.FACING_ALT);
        event.register(Blocks.CHAIN_COMMAND_BLOCK, BlockStateProviders.FACING_ALT);
        // event.register(Blocks.FROSTED_ICE, );
        // event.register(Blocks.MAGMA_BLOCK, );
        // event.register(Blocks.NETHER_WART_BLOCK, );
        // event.register(Blocks.RED_NETHER_BRICKS, );
        event.register(Blocks.BONE_BLOCK, BlockStateProviders.ROTATED_PILLAR);
        // event.register(Blocks.STRUCTURE_VOID, );
        event.register(Blocks.OBSERVER, BlockStateProviders.FACING);
        event.register(Blocks.SHULKER_BOX, BlockStateProviders.Blocks.SHULKER_BOX);
        event.register(Blocks.DYED_SHULKER_BOX, BlockStateProviders.Blocks.SHULKER_BOX);
        event.register(Blocks.GLAZED_TERRACOTTA, BlockStateProviders.HORIZONTAL_FACING_ALT);
        // event.register(Blocks.CONCRETE, );
        event.register(Blocks.CONCRETE_POWDER, BlockStateProviders.Blocks.CONCRETE_POWDER);
        event.register(Blocks.KELP, BlockStateProviders.Blocks.KELP);
        event.register(Blocks.KELP_PLANT, BlockStateProviders.GROWING_PLANT);
        // event.register(Blocks.DRIED_KELP_BLOCK, );
        event.register(Blocks.TURTLE_EGG, BlockStateProviders.EGGS);
        // event.register(Blocks.SNIFFER_EGG, );
        event.register(Blocks.DRIED_GHAST, BlockStateProviders.Blocks.DRIED_GHAST);
        // event.register(Blocks.DEAD_TUBE_CORAL_BLOCK, );
        // event.register(Blocks.DEAD_BRAIN_CORAL_BLOCK, );
        // event.register(Blocks.DEAD_BUBBLE_CORAL_BLOCK, );
        // event.register(Blocks.DEAD_FIRE_CORAL_BLOCK, );
        // event.register(Blocks.DEAD_HORN_CORAL_BLOCK, );
        event.register(Blocks.TUBE_CORAL_BLOCK, BlockStateProviders.Blocks.CORAL);
        event.register(Blocks.BRAIN_CORAL_BLOCK, BlockStateProviders.Blocks.CORAL);
        event.register(Blocks.BUBBLE_CORAL_BLOCK, BlockStateProviders.Blocks.CORAL);
        event.register(Blocks.FIRE_CORAL_BLOCK, BlockStateProviders.Blocks.CORAL);
        event.register(Blocks.HORN_CORAL_BLOCK, BlockStateProviders.Blocks.CORAL);
        event.register(Blocks.DEAD_TUBE_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_BRAIN_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_BUBBLE_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_FIRE_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_HORN_CORAL, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.TUBE_CORAL, BlockStateProviders.Blocks.CORAL_PLANT);
        event.register(Blocks.BRAIN_CORAL, BlockStateProviders.Blocks.CORAL_PLANT);
        event.register(Blocks.BUBBLE_CORAL, BlockStateProviders.Blocks.CORAL_PLANT);
        event.register(Blocks.FIRE_CORAL, BlockStateProviders.Blocks.CORAL_PLANT);
        event.register(Blocks.HORN_CORAL, BlockStateProviders.Blocks.CORAL_PLANT);
        event.register(Blocks.DEAD_TUBE_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_BRAIN_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_BUBBLE_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_FIRE_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.DEAD_HORN_CORAL_FAN, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.TUBE_CORAL_FAN, BlockStateProviders.Blocks.CORAL_FAN);
        event.register(Blocks.BRAIN_CORAL_FAN, BlockStateProviders.Blocks.CORAL_FAN);
        event.register(Blocks.BUBBLE_CORAL_FAN, BlockStateProviders.Blocks.CORAL_FAN);
        event.register(Blocks.FIRE_CORAL_FAN, BlockStateProviders.Blocks.CORAL_FAN);
        event.register(Blocks.HORN_CORAL_FAN, BlockStateProviders.Blocks.CORAL_FAN);
        event.register(Blocks.DEAD_TUBE_CORAL_WALL_FAN, BlockStateProviders.Blocks.DEAD_WALL_CORAL_FAN);
        event.register(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, BlockStateProviders.Blocks.DEAD_WALL_CORAL_FAN);
        event.register(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, BlockStateProviders.Blocks.DEAD_WALL_CORAL_FAN);
        event.register(Blocks.DEAD_FIRE_CORAL_WALL_FAN, BlockStateProviders.Blocks.DEAD_WALL_CORAL_FAN);
        event.register(Blocks.DEAD_HORN_CORAL_WALL_FAN, BlockStateProviders.Blocks.DEAD_WALL_CORAL_FAN);
        event.register(Blocks.TUBE_CORAL_WALL_FAN, BlockStateProviders.Blocks.WALL_CORAL_FAN);
        event.register(Blocks.BRAIN_CORAL_WALL_FAN, BlockStateProviders.Blocks.WALL_CORAL_FAN);
        event.register(Blocks.BUBBLE_CORAL_WALL_FAN, BlockStateProviders.Blocks.WALL_CORAL_FAN);
        event.register(Blocks.FIRE_CORAL_WALL_FAN, BlockStateProviders.Blocks.WALL_CORAL_FAN);
        event.register(Blocks.HORN_CORAL_WALL_FAN, BlockStateProviders.Blocks.WALL_CORAL_FAN);
        event.register(Blocks.SEA_PICKLE, BlockStateProviders.Blocks.SEA_PICKLE);
        // event.register(Blocks.BLUE_ICE, );
        event.register(Blocks.CONDUIT, BlockStateProviders.WATERLOGGED);
        // event.register(Blocks.BAMBOO_SAPLING, );
        event.register(Blocks.BAMBOO, BlockStateProviders.Blocks.BAMBOO_STALK);
        // event.register(Blocks.POTTED_BAMBOO, );
        // event.register(Blocks.VOID_AIR, );
        // event.register(Blocks.CAVE_AIR, );
        // event.register(Blocks.BUBBLE_COLUMN, );
        event.register(Blocks.POLISHED_GRANITE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.MOSSY_STONE_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.POLISHED_DIORITE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.MOSSY_COBBLESTONE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.END_STONE_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.STONE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.SMOOTH_SANDSTONE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.SMOOTH_QUARTZ_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.GRANITE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.ANDESITE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.RED_NETHER_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.POLISHED_ANDESITE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.DIORITE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.POLISHED_GRANITE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.MOSSY_STONE_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.POLISHED_DIORITE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.MOSSY_COBBLESTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.END_STONE_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.SMOOTH_SANDSTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.SMOOTH_QUARTZ_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.GRANITE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.ANDESITE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.RED_NETHER_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.POLISHED_ANDESITE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.DIORITE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.BRICK_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.PRISMARINE_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.RED_SANDSTONE_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.MOSSY_STONE_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.GRANITE_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.STONE_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.MUD_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.NETHER_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.ANDESITE_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.RED_NETHER_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.SANDSTONE_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.END_STONE_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.DIORITE_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.SCAFFOLDING, BlockStateProviders.Blocks.SCAFFOLDING);
        event.register(Blocks.LOOM, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.BARREL, BlockStateProviders.FACING_ALT);
        event.register(Blocks.SMOKER, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.BLAST_FURNACE, BlockStateProviders.HORIZONTAL_FACING_ALT);
        // event.register(Blocks.CARTOGRAPHY_TABLE, );
        // event.register(Blocks.FLETCHING_TABLE, );
        event.register(Blocks.GRINDSTONE, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.LECTERN, BlockStateProviders.Blocks.LECTERN);
        // event.register(Blocks.SMITHING_TABLE, );
        event.register(Blocks.STONECUTTER, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.BELL, BlockStateProviders.Blocks.BELL);
        event.register(Blocks.LANTERN, BlockStateProviders.Blocks.LANTERN);
        event.register(Blocks.SOUL_LANTERN, BlockStateProviders.Blocks.LANTERN);
        event.register(Blocks.COPPER_LANTERN, BlockStateProviders.Blocks.LANTERN);
        event.register(Blocks.CAMPFIRE, BlockStateProviders.Blocks.CAMPFIRE);
        event.register(Blocks.SOUL_CAMPFIRE, BlockStateProviders.Blocks.CAMPFIRE);
        // event.register(Blocks.SWEET_BERRY_BUSH, );
        event.register(Blocks.WARPED_STEM, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_WARPED_STEM, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.WARPED_HYPHAE, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_WARPED_HYPHAE, BlockStateProviders.ROTATED_PILLAR);
        // event.register(Blocks.WARPED_NYLIUM, );
        // event.register(Blocks.WARPED_FUNGUS, );
        // event.register(Blocks.WARPED_WART_BLOCK, );
        // event.register(Blocks.WARPED_ROOTS, );
        // event.register(Blocks.NETHER_SPROUTS, );
        event.register(Blocks.CRIMSON_STEM, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_CRIMSON_STEM, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.CRIMSON_HYPHAE, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.STRIPPED_CRIMSON_HYPHAE, BlockStateProviders.ROTATED_PILLAR);
        // event.register(Blocks.CRIMSON_NYLIUM, );
        // event.register(Blocks.CRIMSON_FUNGUS, );
        // event.register(Blocks.SHROOMLIGHT, );
        event.register(Blocks.WEEPING_VINES, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.WEEPING_VINES_PLANT, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.TWISTING_VINES, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.TWISTING_VINES_PLANT, BlockStateProviders.GROWING_PLANT);
        // event.register(Blocks.CRIMSON_ROOTS, );
        // event.register(Blocks.CRIMSON_PLANKS, );
        // event.register(Blocks.WARPED_PLANKS, );
        event.register(Blocks.CRIMSON_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.WARPED_SLAB, BlockStateProviders.Blocks.SLAB);
        // event.register(Blocks.CRIMSON_PRESSURE_PLATE, );
        // event.register(Blocks.WARPED_PRESSURE_PLATE, );
        event.register(Blocks.CRIMSON_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.WARPED_FENCE, BlockStateProviders.Blocks.FENCE);
        event.register(Blocks.CRIMSON_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.WARPED_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.CRIMSON_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.WARPED_FENCE_GATE, BlockStateProviders.Blocks.FENCE_GATE);
        event.register(Blocks.CRIMSON_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.WARPED_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.CRIMSON_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.WARPED_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.CRIMSON_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.WARPED_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.CRIMSON_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.WARPED_SIGN, BlockStateProviders.Blocks.STANDING_SIGN);
        event.register(Blocks.CRIMSON_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        event.register(Blocks.WARPED_WALL_SIGN, BlockStateProviders.Blocks.WALL_SIGN);
        // event.register(Blocks.STRUCTURE_BLOCK, );
        event.register(Blocks.JIGSAW, BlockStateProviders.Blocks.JIGSAW);
        // vanilla loads the `MODE` directly from the `BLOCK_STATE` component
        // but that is not necessery at all, it is done for us
        // when by applying the whole component to the placed block state
        // this also happens in vanillas placement logic, so i dont understand why they do this... mojang.. why??
        // BlockItem.updateBlockStateFromTag() handles this for you
        // event.register(Blocks.TEST_BLOCK, );
        // event.register(Blocks.TEST_INSTANCE_BLOCK, );
        // event.register(Blocks.COMPOSTER, );
        // event.register(Blocks.TARGET, );
        event.register(Blocks.BEE_NEST, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.BEEHIVE, BlockStateProviders.HORIZONTAL_FACING_ALT);
        // event.register(Blocks.HONEY_BLOCK, );
        // event.register(Blocks.HONEYCOMB_BLOCK, );
        // event.register(Blocks.NETHERITE_BLOCK, );
        // event.register(Blocks.ANCIENT_DEBRIS, );
        // event.register(Blocks.CRYING_OBSIDIAN, );
        // event.register(Blocks.RESPAWN_ANCHOR, );
        // event.register(Blocks.POTTED_CRIMSON_FUNGUS, );
        // event.register(Blocks.POTTED_WARPED_FUNGUS, );
        // event.register(Blocks.POTTED_CRIMSON_ROOTS, );
        // event.register(Blocks.POTTED_WARPED_ROOTS, );
        // event.register(Blocks.LODESTONE, );
        // event.register(Blocks.BLACKSTONE, );
        event.register(Blocks.BLACKSTONE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.BLACKSTONE_WALL, BlockStateProviders.Blocks.WALL);
        event.register(Blocks.BLACKSTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        // event.register(Blocks.POLISHED_BLACKSTONE, );
        // event.register(Blocks.POLISHED_BLACKSTONE_BRICKS, );
        // event.register(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, );
        // event.register(Blocks.CHISELED_POLISHED_BLACKSTONE, );
        event.register(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.GILDED_BLACKSTONE, );
        event.register(Blocks.POLISHED_BLACKSTONE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.POLISHED_BLACKSTONE_SLAB, BlockStateProviders.Blocks.SLAB);
        // event.register(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, );
        event.register(Blocks.POLISHED_BLACKSTONE_BUTTON, WallAttachmentBlockStateProvider.FACING_HORIZONTAL);
        event.register(Blocks.POLISHED_BLACKSTONE_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.CHISELED_NETHER_BRICKS, );
        // event.register(Blocks.CRACKED_NETHER_BRICKS, );
        // event.register(Blocks.QUARTZ_BRICKS, );
        event.register(Blocks.CANDLE, BlockStateProviders.Blocks.CANDLE);
        event.register(Blocks.DYED_CANDLE, BlockStateProviders.Blocks.CANDLE);
        // event.register(Blocks.CANDLE_CAKE, );
        // event.register(Blocks.DYED_CANDLE_CAKE, );
        // event.register(Blocks.AMETHYST_BLOCK, );
        // event.register(Blocks.BUDDING_AMETHYST, );
        event.register(Blocks.AMETHYST_CLUSTER, BlockStateProviders.Blocks.AMETHYST_CLUSTER);
        event.register(Blocks.LARGE_AMETHYST_BUD, BlockStateProviders.Blocks.AMETHYST_CLUSTER);
        event.register(Blocks.MEDIUM_AMETHYST_BUD, BlockStateProviders.Blocks.AMETHYST_CLUSTER);
        event.register(Blocks.SMALL_AMETHYST_BUD, BlockStateProviders.Blocks.AMETHYST_CLUSTER);
        // event.register(Blocks.TUFF, );
        event.register(Blocks.TUFF_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.TUFF_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.TUFF_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.POLISHED_TUFF, );
        event.register(Blocks.POLISHED_TUFF_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.POLISHED_TUFF_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.POLISHED_TUFF_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.CHISELED_TUFF, );
        // event.register(Blocks.TUFF_BRICKS, );
        event.register(Blocks.TUFF_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.TUFF_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.TUFF_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.CHISELED_TUFF_BRICKS, );
        // event.register(Blocks.SULFUR, );
        event.register(Blocks.POTENT_SULFUR, BlockStateProviders.Blocks.POTENT_SULFUR);
        event.register(Blocks.SULFUR_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.SULFUR_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.SULFUR_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.POLISHED_SULFUR, );
        event.register(Blocks.POLISHED_SULFUR_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.POLISHED_SULFUR_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.POLISHED_SULFUR_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.SULFUR_BRICKS, );
        event.register(Blocks.SULFUR_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.SULFUR_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.SULFUR_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.CHISELED_SULFUR, );
        // event.register(Blocks.CINNABAR, );
        event.register(Blocks.CINNABAR_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.CINNABAR_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.CINNABAR_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.POLISHED_CINNABAR, );
        event.register(Blocks.POLISHED_CINNABAR_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.POLISHED_CINNABAR_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.POLISHED_CINNABAR_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.CINNABAR_BRICKS, );
        event.register(Blocks.CINNABAR_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.CINNABAR_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.CINNABAR_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.CHISELED_CINNABAR, );
        // event.register(Blocks.CALCITE, );
        // event.register(Blocks.TINTED_GLASS, );
        // event.register(Blocks.POWDER_SNOW, );
        event.register(Blocks.SCULK_SENSOR, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.CALIBRATED_SCULK_SENSOR, BlockStateProviders.Blocks.CALIBRATED_SCULK_SENSOR);
        // event.register(Blocks.SCULK, );
        event.register(Blocks.SCULK_VEIN, WallAttachmentBlockStateProvider.MULTI_FACE);
        // event.register(Blocks.SCULK_CATALYST, );
        event.register(Blocks.SCULK_SHRIEKER, BlockStateProviders.WATERLOGGED);
        // event.register(Blocks.COPPER_BLOCK, );
        // event.register(Blocks.COPPER_ORE, );
        // event.register(Blocks.DEEPSLATE_COPPER_ORE, );
        // event.register(Blocks.CUT_COPPER, );
        // event.register(Blocks.CHISELED_COPPER, );
        event.register(Blocks.CUT_COPPER_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.CUT_COPPER_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.COPPER_DOOR, BlockStateProviders.Blocks.DOOR);
        event.register(Blocks.COPPER_TRAPDOOR, BlockStateProviders.Blocks.TRAPDOOR);
        event.register(Blocks.COPPER_GRATE, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.COPPER_BULB, BlockStateProviders.Blocks.COPPER_BULB);
        event.register(Blocks.COPPER_CHEST, BlockStateProviders.Blocks.COPPER_CHEST);
        event.register(Blocks.COPPER_GOLEM_STATUE, BlockStateProviders.Blocks.COPPER_GOLEM);
        event.register(Blocks.LIGHTNING_ROD, BlockStateProviders.Blocks.LIGHTNING_ROD);
        // event.register(Blocks.DRIPSTONE_BLOCK, );
        event.register(Blocks.POINTED_DRIPSTONE, BlockStateProviders.Blocks.SPELEOTHEM_BLOCK);
        event.register(Blocks.SULFUR_SPIKE, BlockStateProviders.Blocks.SPELEOTHEM_BLOCK);
        event.register(Blocks.CAVE_VINES, BlockStateProviders.GROWING_PLANT);
        event.register(Blocks.CAVE_VINES_PLANT, BlockStateProviders.GROWING_PLANT);
        // event.register(Blocks.SPORE_BLOSSOM, );
        // event.register(Blocks.AZALEA, );
        // event.register(Blocks.FLOWERING_AZALEA, );
        // event.register(Blocks.MOSS_CARPET, );
        event.register(Blocks.PINK_PETALS, BlockStateProviders.Blocks.FLOWER_BED);
        event.register(Blocks.WILDFLOWERS, BlockStateProviders.Blocks.FLOWER_BED);
        event.register(Blocks.LEAF_LITTER, BlockStateProviders.Blocks.FLOWER_BED);
        // event.register(Blocks.MOSS_BLOCK, );
        event.register(Blocks.BIG_DRIPLEAF, BlockStateProviders.Blocks.DRIP_LEAF);
        // event.register(Blocks.BIG_DRIPLEAF_STEM, );
        event.register(Blocks.SMALL_DRIPLEAF, BlockStateProviders.WATERLOGGED);
        event.register(Blocks.HANGING_ROOTS, BlockStateProviders.WATERLOGGED);
        // event.register(Blocks.ROOTED_DIRT, );
        // event.register(Blocks.MUD, );
        event.register(Blocks.DEEPSLATE, BlockStateProviders.ROTATED_PILLAR);
        // event.register(Blocks.COBBLED_DEEPSLATE, );
        event.register(Blocks.COBBLED_DEEPSLATE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.COBBLED_DEEPSLATE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.COBBLED_DEEPSLATE_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.POLISHED_DEEPSLATE, );
        event.register(Blocks.POLISHED_DEEPSLATE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.POLISHED_DEEPSLATE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.POLISHED_DEEPSLATE_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.DEEPSLATE_TILES, );
        event.register(Blocks.DEEPSLATE_TILE_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.DEEPSLATE_TILE_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.DEEPSLATE_TILE_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.DEEPSLATE_BRICKS, );
        event.register(Blocks.DEEPSLATE_BRICK_STAIRS, BlockStateProviders.Blocks.STAIR);
        event.register(Blocks.DEEPSLATE_BRICK_SLAB, BlockStateProviders.Blocks.SLAB);
        event.register(Blocks.DEEPSLATE_BRICK_WALL, BlockStateProviders.Blocks.WALL);
        // event.register(Blocks.CHISELED_DEEPSLATE, );
        // event.register(Blocks.CRACKED_DEEPSLATE_BRICKS, );
        // event.register(Blocks.CRACKED_DEEPSLATE_TILES, );
        event.register(Blocks.INFESTED_DEEPSLATE, BlockStateProviders.ROTATED_PILLAR);
        // event.register(Blocks.SMOOTH_BASALT, );
        // event.register(Blocks.RAW_IRON_BLOCK, );
        // event.register(Blocks.RAW_COPPER_BLOCK, );
        // event.register(Blocks.RAW_GOLD_BLOCK, );
        // event.register(Blocks.POTTED_AZALEA, );
        // event.register(Blocks.POTTED_FLOWERING_AZALEA, );
        event.register(Blocks.OCHRE_FROGLIGHT, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.VERDANT_FROGLIGHT, BlockStateProviders.ROTATED_PILLAR);
        event.register(Blocks.PEARLESCENT_FROGLIGHT, BlockStateProviders.ROTATED_PILLAR);
        // event.register(Blocks.FROGSPAWN, );
        // event.register(Blocks.REINFORCED_DEEPSLATE, );
        event.register(Blocks.DECORATED_POT, BlockStateProviders.Blocks.DECORATED_POT);
        event.register(Blocks.CRAFTER, BlockStateProviders.Blocks.CRAFTER);
        // event.register(Blocks.TRIAL_SPAWNER, );
        event.register(Blocks.VAULT, BlockStateProviders.HORIZONTAL_FACING_ALT);
        event.register(Blocks.HEAVY_CORE, BlockStateProviders.WATERLOGGED);
        // event.register(Blocks.PALE_MOSS_BLOCK, );
        event.register(Blocks.PALE_MOSS_CARPET, BlockStateProviders.Blocks.MOSSY_CARPET);
        // event.register(Blocks.PALE_HANGING_MOSS, );
        // event.register(Blocks.OPEN_EYEBLOSSOM, );
        // event.register(Blocks.CLOSED_EYEBLOSSOM, );
        // event.register(Blocks.POTTED_OPEN_EYEBLOSSOM, );
        // event.register(Blocks.POTTED_CLOSED_EYEBLOSSOM, );
        // event.register(Blocks.FIREFLY_BUSH, );
    }

    private void registerBuiltInUseOnHandlers(RegisterUseOnHandlersEvent event) {
        var needsPlacingOnWater = isItem(Items.LILY_PAD, Items.FROGSPAWN);
        var isPiston = isItem(Items.PISTON, Items.STICKY_PISTON);
        var isStandingWall = isItemType(StandingAndWallBlockItem.class);
        var isDoubleHigh = isItemType(DoubleHighBlockItem.class);
        var isBed = isItemType(BedItem.class);

        var isBlockItem = isItemType(BlockItem.class)
                .and(needsPlacingOnWater.negate())
                .and(isStandingWall.negate())
                .and(isBed.negate())
                .and(isDoubleHigh.negate())
                .and(isPiston.negate());

        registerForEachVanilla(BuiltInRegistries.ITEM, isBlockItem, event, UseOnHandler.BLOCK_ITEM);
        registerForEachVanilla(BuiltInRegistries.ITEM, isStandingWall, event, UseOnHandler.STANDING_WALL);
        registerForEachVanilla(BuiltInRegistries.ITEM, needsPlacingOnWater, event, UseOnHandler.PLACE_ON_WATER);
        registerForEachVanilla(BuiltInRegistries.ITEM, isDoubleHigh, event, MultiBlockItemHandler.DOUBLE_HIGH);
        registerForEachVanilla(BuiltInRegistries.ITEM, isBed, event, MultiBlockItemHandler.BED);
        registerForEachVanilla(BuiltInRegistries.ITEM, isPiston, event, MultiBlockItemHandler.PISTON);
        registerForEachVanilla(BuiltInRegistries.ITEM, isItemType(SpawnEggItem.class), event, UseOnHandler.SPAWN_EGG);

        // TODO: handlers for the following items
        // Overriders of `Item.useOn`
        //  ArmorStandItem
        //  AxeItem
        //  BoneMealItem
        //  DebugStickItem
        //  EndCrystalItem
        //  EnderEyeItem*
        //  FireChargeItem
        //  FireworkRocketItem*
        //  FlintAndSteelItem
        //  HangingEntityItem
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

    private <TKey, TValue> void registerForEachVanilla(Registry<TKey> registry, Predicate<? super TKey> filter, PsudeoRegistry.Keyed.Registrar<TKey, TValue> registrar, TValue value) {
        for(var key : registry) {
            var registryKey = registry.getKey(key);

            if(registryKey == null || !isVanilla(registryKey)) {
                continue;
            }

            if(filter.test(key)) {
                registrar.register(key, value);
            }
        }
    }

    private Predicate<Item> isItemType(Class<? extends Item> type) {
        return type::isInstance;
    }

    private Predicate<Item> isItem(Item... items) {
        return item -> {
            for(var testItem : items) {
                if(item == testItem) {
                    return true;
                }
            }

            return false;
        };
    }

    private boolean isVanilla(Identifier registryName) {
        return registryName.getNamespace().equals(Identifier.DEFAULT_NAMESPACE);
    }
}
