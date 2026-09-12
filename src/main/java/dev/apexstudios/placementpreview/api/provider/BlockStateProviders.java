package dev.apexstudios.placementpreview.api.provider;

import com.google.common.base.Predicates;
import dev.apexstudios.placementpreview.api.BlockStateHelper;
import dev.apexstudios.placementpreview.api.PlacementPreview;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.validator.PlacementValidator;
import dev.apexstudios.placementpreview.api.validator.PlacementValidators;
import dev.apexstudios.placementpreview.extensions.RailStateExtension;
import dev.apexstudios.placementpreview.mixin.CampfireBlockAccessor;
import dev.apexstudios.placementpreview.mixin.ChestBlockAccessor;
import dev.apexstudios.placementpreview.mixin.CoralBlockAccessor;
import dev.apexstudios.placementpreview.mixin.DefaultRedstoneWireEvaluatorAccessor;
import dev.apexstudios.placementpreview.mixin.DetectorRailBlockAccessor;
import dev.apexstudios.placementpreview.mixin.DiodeBlockAccessor;
import dev.apexstudios.placementpreview.mixin.DoorBlockAccessor;
import dev.apexstudios.placementpreview.mixin.FenceGateBlockAccessor;
import dev.apexstudios.placementpreview.mixin.FireBlockAccessor;
import dev.apexstudios.placementpreview.mixin.GrowingPlantBlockAccessor;
import dev.apexstudios.placementpreview.mixin.NoteBlockAccessor;
import dev.apexstudios.placementpreview.mixin.PistonBaseBlockAccessor;
import dev.apexstudios.placementpreview.mixin.PoweredRailBlockAccessor;
import dev.apexstudios.placementpreview.mixin.RedStoneTorchBlockAccessor;
import dev.apexstudios.placementpreview.mixin.RedStoneWireBlockAccessor;
import dev.apexstudios.placementpreview.mixin.ScaffoldingBlockAccessor;
import dev.apexstudios.placementpreview.mixin.SpeleothemBlockAccessor;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.TypedInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.BaseCoralFanBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.CopperChestBlock;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.PotentSulfurBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.SegmentableBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyBlock;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.SlabType;

@SuppressWarnings({"resource", "DataFlowIssue", "deprecation"})
public interface BlockStateProviders {
    BlockStateProvider SNOWY = property(BlockStateProperties.SNOWY, (context, blockState, current) -> PlacementResult.success(SnowyBlock.isSnowySetting(context.getLevel().getBlockState(context.getClickedPos().above()))));
    BlockStateProvider ROTATED_PILLAR = property(BlockStateProperties.AXIS, (context, blockState, current) -> PlacementResult.success(context.getClickedFace().getAxis()));
    BlockStateProvider WATERLOGGED = property(BlockStateProperties.WATERLOGGED, (context, blockState, current) -> PlacementResult.success(context.getLevel().isWaterAt(context.getClickedPos())));
    BlockStateProvider WATERLOGGED_FALSE = forced(BlockStateProperties.WATERLOGGED, false);
    BlockStateProvider WATERLOGGED_TRUE = forced(BlockStateProperties.WATERLOGGED, true);
    BlockStateProvider AGE_4_MAX = forced(BlockStateProperties.AGE_4, BlockStateProperties.MAX_AGE_4);
    BlockStateProvider PERSISTENT_FALSE = forced(BlockStateProperties.PERSISTENT, false);
    BlockStateProvider PERSISTENT_TRUE = forced(BlockStateProperties.PERSISTENT, true);
    BlockStateProvider LEAVES_DISTANCE = (context, blockState) -> PlacementResult.success(LeavesBlock.updateDistance(blockState, context.getLevel(), context.getClickedPos()));
    BlockStateProvider CLICKED_FACE = property(BlockStateProperties.FACING, (context, blockState, current) -> PlacementResult.success(context.getClickedFace()));
    BlockStateProvider CLICKED_FACE_ALT = CLICKED_FACE.andThen(property(BlockStateProperties.FACING, (context, blockState, current) -> PlacementResult.success(current.getOpposite())));
    BlockStateProvider FACING = property(BlockStateProperties.FACING, (context, blockState, current) -> PlacementResult.success(context.getNearestLookingDirection()));
    BlockStateProvider FACING_ALT = FACING.andThen(property(BlockStateProperties.FACING, (context, blockState, current) -> PlacementResult.success(current.getOpposite())));
    /// Requires: [NoteBlock]
    BlockStateProvider NOTE_BLOCK_INTRUMENT = (context, blockState) -> PlacementResult.success(((NoteBlockAccessor) blockState.getBlock()).PlacementPreview$setInstrument(context.getLevel(), context.getClickedPos(), blockState));
    BlockStateProvider HORIZONTAL_FACING = property(BlockStateProperties.HORIZONTAL_FACING, (context, blockState, current) -> PlacementResult.success(context.getHorizontalDirection()));
    BlockStateProvider HORIZONTAL_FACING_ALT = HORIZONTAL_FACING.andThen(property(BlockStateProperties.HORIZONTAL_FACING, (context, blockState, current) -> PlacementResult.success(current.getOpposite())));
    BlockStateProvider HORIZONTAL_FACING_CLOCKWISE = HORIZONTAL_FACING.andThen(property(BlockStateProperties.HORIZONTAL_FACING, (context, blockState, current) -> PlacementResult.success(current.getClockWise())));
    /// Requires: [BaseRailBlock]
    BlockStateProvider RAIL_SHAPE = (context, blockState) -> {
        var direction = context.getHorizontalDirection();
        return PlacementResult.success(blockState.setValue(((BaseRailBlock) blockState.getBlock()).getShapeProperty(), direction == Direction.EAST || direction == Direction.WEST ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH));
    };
    /// Requires: [BaseRailBlock]
    BlockStateProvider RAIL_STATE = (context, blockState) -> {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var currentShape = ((BaseRailBlock) blockState.getBlock()).getRailDirection(blockState, level, pos, null);
        var state = new RailState(level, pos, blockState);
        ((RailStateExtension) state).PlacementPreview$allowsSetBlock(false);
        return PlacementResult.success(state.place(level.hasNeighborSignal(pos), true, currentShape).getState());
    };
    /// Requires: [PistonBaseBlock]
    BlockStateProvider PISTON_EXTENDED = property(BlockStateProperties.EXTENDED, (context, blockState, current) -> PlacementResult.success(((PistonBaseBlockAccessor) blockState.getBlock()).PlacementPreview$getNeighborSignal(context.getLevel(), context.getClickedPos(), blockState.getValue(BlockStateProperties.FACING))));
    BlockStateProvider INSIDE_WATER = PlacementValidators.INSIDE_WATER.asProvider();
    BlockStateProvider POWERED = property(BlockStateProperties.POWERED, (context, blockState, current) -> PlacementResult.success(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    BlockStateProvider POWERED_DOUBLE = POWERED.andThen(property(BlockStateProperties.POWERED, (context, blockState, current) -> PlacementResult.success(current || context.getLevel().hasNeighborSignal(context.getClickedPos().above()))));
    BlockStateProvider WALL_ATTACHMENT = orElse(WallAttachmentBlockStateProvider.HORIZONTAL, HORIZONTAL_FACING.asFailure());
    BlockStateProvider WALL_ATTACHMENT_ALT = orElse(WallAttachmentBlockStateProvider.HORIZONTAL_ALT, HORIZONTAL_FACING_ALT.asFailure());
    /// Requires: [FireBlock]
    BlockStateProvider FIRE_BLOCK = (context, blockState) -> PlacementResult.success(((FireBlockAccessor) blockState.getBlock()).PlacementPreview$getStateForPlacement(context.getLevel(), context.getClickedPos()));
    BlockStateProvider CREAKING_HEART_TYPE = (context, blockState) -> PlacementResult.success(CreakingHeartBlock.updateState(blockState, context.getLevel(), context.getClickedPos()));
    /// Requires: [ChestBlock]
    BlockStateProvider CHEST_TYPE = (context, blockState) -> {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var direction = context.getHorizontalDirection().getOpposite();
        var secondaryUse = context.isSecondaryUseActive();
        var clickedFace = context.getClickedFace();
        var clickedFaceAxis = clickedFace.getAxis();
        var chest = (ChestBlockAccessor) blockState.getBlock();
        var type = ChestType.SINGLE;

        if(clickedFaceAxis.isHorizontal() && secondaryUse) {
            var clickedFaceOpposite = clickedFace.getOpposite();
            var neighbourFacing = chest.PlacementPreview$candidatePartnerFacing(level, pos, clickedFaceOpposite);

            if(neighbourFacing != null && neighbourFacing.getAxis() != clickedFaceAxis) {
                direction = neighbourFacing;
                type = direction.getCounterClockWise() == clickedFaceOpposite ? ChestType.RIGHT : ChestType.LEFT;
            }
        }

        if(type == ChestType.SINGLE && !secondaryUse) {
            type = chest.PlacementPreview$getChestType(level, pos, direction);
        }

        return PlacementResult.success(blockState
                .setValue(BlockStateProperties.HORIZONTAL_FACING, direction)
                .setValue(BlockStateProperties.CHEST_TYPE, type)
        );
    };
    /// Requires: [RedStoneWireBlock]
    BlockStateProvider REDSTONE_WIRE_CONNECTIONS = (context, blockState) -> PlacementResult.success(((RedStoneWireBlockAccessor) blockState.getBlock()).PlacementPreview$getConnectionState(context.getLevel(), blockState, context.getClickedPos()));
    BlockStateProvider INVALID_TO_DIRT = transforming(PlacementValidators.CAN_SURVIVE, itemTransformer(Items.DIRT));
    BlockStateProvider ROTATION = property(BlockStateProperties.ROTATION_16, (context, blockState, current) -> PlacementResult.success(RotationSegment.convertToSegment(context.getRotation())));
    BlockStateProvider ROTATION_ALT = property(BlockStateProperties.ROTATION_16, (context, blockState, current) -> PlacementResult.success(RotationSegment.convertToSegment(context.getRotation() + 180F)));
    /// Requires: [DoorBlock]
    BlockStateProvider DOOR_HINGE = property(BlockStateProperties.DOOR_HINGE, (context, blockState, current) -> PlacementResult.success(((DoorBlockAccessor) blockState.getBlock()).PlacementPreview$getHinge(context)));
    BlockStateProvider LADDER_VALIDTION = PlacementValidators.LADDER.asProvider();
    BlockStateProvider HANGING_SIGN_ATTACHMENT = (context, blockState) -> {
        var level = context.getLevel();
        var abovePos = context.getClickedPos().above();
        var aboveBlockState = level.getBlockState(abovePos);
        var secondaryUse = context.isSecondaryUseActive();
        var attachedToMiddle = !Block.isFaceFull(aboveBlockState.getCollisionShape(level, abovePos), Direction.DOWN) || secondaryUse;

        if(aboveBlockState.is(BlockTags.ALL_HANGING_SIGNS) && !secondaryUse) {
            var direction = Direction.fromYRot(context.getRotation());

            if(aboveBlockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                var aboveDirection = aboveBlockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

                if(aboveDirection.getAxis().test(direction)) {
                    attachedToMiddle = false;
                }
            } else if(aboveBlockState.hasProperty(BlockStateProperties.ROTATION_16)) {
                var aboveDirection = RotationSegment.convertToDirection(aboveBlockState.getValue(BlockStateProperties.ROTATION_16));

                if(aboveDirection.isPresent() && aboveDirection.get().getAxis().test(direction)) {
                    attachedToMiddle = false;
                }
            }
        }

        return PlacementResult.success(blockState
                .setValue(BlockStateProperties.ATTACHED, attachedToMiddle)
                .setValue(BlockStateProperties.ROTATION_16, attachedToMiddle ? RotationSegment.convertToSegment(context.getRotation() + 180F) : RotationSegment.convertToSegment(context.getRotation()))
        );
    };
    /// Requires: [RedstoneTorchBlock]
    BlockStateProvider REDSTONE_TORCH_LIT = property(BlockStateProperties.LIT, (context, blockState, current) -> PlacementResult.success(!((RedStoneTorchBlockAccessor) blockState.getBlock()).PlacementPreview$hasNeighborSignal(context.getLevel(), context.getClickedPos(), blockState)));
    BlockStateProvider SNOW_LAYERS = ifTrue(
            PlacementValidators.SAME_BLOCK,
            property(BlockStateProperties.LAYERS, (context, blockState, current) -> {
                var existingBlockState = context.getLevel().getBlockState(context.getClickedPos());
                return PlacementResult.success(Math.min(SnowLayerBlock.MAX_HEIGHT, existingBlockState.getValue(BlockStateProperties.LAYERS) + 1));
            })
    );
    /// Requires: [DiodeBlock]
    BlockStateProvider DIODE_LOCKED = property(BlockStateProperties.LOCKED, (context, blockState, current) -> PlacementResult.success(((DiodeBlock) blockState.getBlock()).isLocked(context.getLevel(), context.getClickedPos(), blockState)));
    BlockStateProvider TRAPDOOR_ROTATION = (context, blockState) -> {
        var clickedFace = context.getClickedFace();
        Direction facingDirection;
        Half half;

        if(!context.replacingClickedOnBlock() && clickedFace.getAxis().isHorizontal()) {
            facingDirection = clickedFace;
            half = context.getClickLocation().y() - context.getClickedPos().getY() > .5D ? Half.TOP : Half.BOTTOM;
        } else {
            facingDirection = context.getHorizontalDirection().getOpposite();
            half = clickedFace == Direction.UP ? Half.BOTTOM : Half.TOP;
        }

        return PlacementResult.success(blockState
                .setValue(BlockStateProperties.HORIZONTAL_FACING, facingDirection)
                .setValue(BlockStateProperties.HALF, half)
        );
    };
    BlockStateProvider OPEN_FROM_POWERED = copyValue(BlockStateProperties.POWERED, BlockStateProperties.OPEN);
    /// Requires: [DetectorRailBlock]
    BlockStateProvider DETECTOR_RAIL_POWERED = property(BlockStateProperties.POWERED, (context, blockState, current) -> PlacementResult.success(!((DetectorRailBlockAccessor) blockState.getBlock()).PlacementPreview$getInteractingMinecartOfType(context.getLevel(), context.getClickedPos(), AbstractMinecart.class, Predicates.alwaysTrue()).isEmpty()));
    /// Requires: [PoweredRailBlock]
    BlockStateProvider POWERED_RAIL_POWERED = property(BlockStateProperties.POWERED, (context, blockState, current) -> {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var hasPower = level.hasNeighborSignal(pos);

        if(!hasPower) {
            var rail = (PoweredRailBlockAccessor) blockState.getBlock();
            hasPower = rail.PlacementPreview$findPoweredRailSignal(level, pos, blockState, true, 0) ||
                    rail.PlacementPreview$findPoweredRailSignal(level, pos, blockState, false, 0);
        }

        return PlacementResult.success(hasPower);
    });
    /// Requires: [FenceGateBlock]
    BlockStateProvider FENCE_GATE_IN_WALL = property(BlockStateProperties.IN_WALL, (context, blockState, current) -> {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var axis = context.getHorizontalDirection().getAxis();
        var block = (FenceGateBlockAccessor) blockState.getBlock();
        var inWall = false;

        if(axis == Direction.Axis.X) {
            inWall = block.PlacementPreview$isWall(level.getBlockState(pos.north())) ||
                    block.PlacementPreview$isWall(level.getBlockState(pos.south()));
        } else if(axis == Direction.Axis.Z) {
            inWall = block.PlacementPreview$isWall(level.getBlockState(pos.east())) ||
                    block.PlacementPreview$isWall(level.getBlockState(pos.west()));
        }

        return PlacementResult.success(inWall);
    });
    BlockStateProvider SLAB_TYPE = property(BlockStateProperties.SLAB_TYPE, (context, blockState, current) -> {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var existingBlockState = level.getBlockState(pos);

        if(existingBlockState.is(blockState.getBlock())) {
            return PlacementResult.success(SlabType.DOUBLE);
        }

        return PlacementResult.success(topOrBottom(context, SlabType.TOP, SlabType.BOTTOM));
    });
    BlockStateProvider HAS_EYE_TRUE = forced(BlockStateProperties.EYE, true);
    BlockStateProvider HAS_EYE_FALSE = forced(BlockStateProperties.EYE, false);
    BlockStateProvider LIT = property(BlockStateProperties.LIT, (context, blockState, current) -> PlacementResult.success(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    BlockStateProvider LIT_TRUE = forced(BlockStateProperties.LIT, true);
    BlockStateProvider LIT_FALSE = forced(BlockStateProperties.LIT, false);
    BlockStateProvider LIT_FROM_POWERED = copyValue(BlockStateProperties.POWERED, BlockStateProperties.LIT);
    BlockStateProvider FACING_HOPPER = property(BlockStateProperties.FACING_HOPPER, (context, blockState, current) -> {
        var direction = context.getClickedFace().getOpposite();
        return PlacementResult.success(direction.getAxis().isVertical() ? Direction.DOWN : direction);
    });
    BlockStateProvider ENABLED_TRUE = forced(BlockStateProperties.ENABLED, true);
    BlockStateProvider ENABLED_FALSE = forced(BlockStateProperties.ENABLED, false);
    /// Requires: [DiodeBlock]
    BlockStateProvider POWERED_COMPARATOR = property(BlockStateProperties.POWERED, (context, blockState, current) -> PlacementResult.success(((DiodeBlockAccessor) blockState.getBlock()).PlacementPreview$shouldTurnOn(context.getLevel(), context.getClickedPos(), blockState)));
    /// Requires: [RedStoneWireBlock]
    BlockStateProvider REDSTONE_WIRE_POWER = property(BlockStateProperties.POWER, (context, blockState, current) -> {
        // experimental redstone while looks more complicated
        // this mostly to gather up all the neighbor signals
        // everything comes down to getting the block and incoming wire signal
        // and using the Math.max() of the 2 values
        // which is what the default evaluator does
        return PlacementResult.success(((DefaultRedstoneWireEvaluatorAccessor) ((RedStoneWireBlock) blockState.getBlock()).evaluator).PlacementPreview$calculateTargetStrength(context.getLevel(), context.getClickedPos()));
    });
    BlockStateProvider END_ROD_FACING = property(BlockStateProperties.FACING, (context, blockState, current) -> {
        var clickedFace = context.getClickedFace();
        var existingBlockState = context.getLevel().getBlockState(context.getClickedPos().relative(clickedFace.getOpposite()));
        return PlacementResult.success(existingBlockState.is(blockState.getBlock()) && existingBlockState.getValue(BlockStateProperties.FACING) == clickedFace ? clickedFace.getOpposite() : clickedFace);
    });
    BlockStateProvider CHORUS_PLANT_CONNECTIONS = (context, blockState) -> PlacementResult.success(ChorusPlantBlock.getStateWithConnections(context.getLevel(), context.getClickedPos(), blockState));
    BlockStateProvider EGGS = transforming(
            PlacementValidators.SAME_BLOCK.negate(),
            property(BlockStateProperties.EGGS, (context, blockState, current) -> PlacementResult.success(Math.min(TurtleEggBlock.MAX_EGGS, context.getLevel().getBlockState(context.getClickedPos()).getValue(BlockStateProperties.EGGS) + 1)))
    );
    BlockStateProvider PICKELS = transforming(
            PlacementValidators.SAME_BLOCK.negate(),
            property(BlockStateProperties.PICKLES, (context, blockState, current) -> PlacementResult.success(Math.min(SeaPickleBlock.MAX_PICKLES, context.getLevel().getBlockState(context.getClickedPos()).getValue(BlockStateProperties.PICKLES) + 1)))
    );
    BlockStateProvider LIT_IF_NOT_WATERLOGGED = either(PlacementValidators.WATERLOGGED, LIT_FALSE, LIT_TRUE);
    BlockStateProvider SIGNAL_FIRE = property(BlockStateProperties.SIGNAL_FIRE, (context, blockState, current) -> PlacementResult.success(((CampfireBlockAccessor) blockState.getBlock()).PlacementPreview$isSmokeSource(context.getLevel().getBlockState(context.getClickedPos().below()))));
    /// Requires: [GrowingPlantBlock]
    BlockStateProvider GROWING_PLANT = (context, blockState) -> {
        var level = context.getLevel();
        var block = (GrowingPlantBlock) blockState.getBlock();
        var accessor = (GrowingPlantBlockAccessor) block;
        var headBlock = accessor.PlacementPreview$getHeadBlock();
        var bodyBlock = accessor.PlacementPreview$getBodyBlock();
        var growingDirectionBlockState = level.getBlockState(context.getClickedPos().relative(block.growthDirection));

        if(!growingDirectionBlockState.is(headBlock) && !growingDirectionBlockState.is(bodyBlock)) {
            return PlacementResult.success(block.getStateForPlacement(level.getRandom()));
        }

        return BlockStateProvider.applyDefaults(context, bodyBlock);
    };
    BlockStateProvider ORIENTATION = property(BlockStateProperties.ORIENTATION, (context, blockState, current) -> {
        var front = context.getClickedFace();
        return PlacementResult.success(FrontAndTop.fromFrontAndTop(front, front.getAxis().isVertical() ? context.getHorizontalDirection().getOpposite() : Direction.UP));
    });
    BlockStateProvider CANDLES = transforming(
            PlacementValidators.SAME_BLOCK.negate(),
            property(BlockStateProperties.CANDLES, (context, blockState, current) -> PlacementResult.success(Math.min(CandleBlock.MAX_CANDLES, context.getLevel().getBlockState(context.getClickedPos()).getValue(BlockStateProperties.CANDLES) + 1)))
    );
    // These fix the flickering when not targeting any block
    BlockStateProvider CLICKED_FACE_FIXED = either(PlacementValidators.CLICKED_EMPTY_BLOCK, FACING_ALT, CLICKED_FACE);
    BlockStateProvider CLICKED_FACE_ALT_FIXED = either(PlacementValidators.CLICKED_EMPTY_BLOCK, FACING, CLICKED_FACE_ALT);
    BlockStateProvider HALF = property(BlockStateProperties.HALF, (context, blockState, current) -> PlacementResult.success(topOrBottom(context, Half.TOP, Half.BOTTOM)));
    BlockStateProvider STAIR_SHAPE = property(BlockStateProperties.STAIRS_SHAPE, (context, blockState, current) -> PlacementResult.success(StairBlock.getStairsShape(blockState, context.getLevel(), context.getClickedPos())));
    BlockStateProvider LEAST_OXIDIZED_COPPER_CHEST = (context, blockState) -> PlacementResult.success(CopperChestBlock.getLeastOxidizedChestOfConnectedBlocks(blockState, context.getLevel(), context.getClickedPos()));
    /// Requires: [SpeleothemBlock]
    BlockStateProvider SPELEOTHEM = (context, blockState) -> {
        var block = (SpeleothemBlockAccessor) blockState.getBlock();
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var defaultTipDirection = context.getNearestLookingVerticalDirection().getOpposite();
        var tipDirection = block.PlacementPreview$calculateTipDirection(level, pos, defaultTipDirection);

        if(tipDirection == null) {
            return PlacementResult.failure(blockState);
        }

        var mergingOpposingTips = !context.isSecondaryUseActive();
        var thickness = block.PlacementPreview$calculateSpeleothemThickness(level, pos, tipDirection, mergingOpposingTips);

        return PlacementResult.success(blockState
                .setValue(BlockStateProperties.VERTICAL_DIRECTION, tipDirection)
                .setValue(BlockStateProperties.SPELEOTHEM_THICKNESS, thickness)
        );
    };
    /// Requires: [SegmentableBlock]
    BlockStateProvider SEGMENTABLE = transforming(
            PlacementValidators.SAME_BLOCK.negate(),
            (context, blockState) -> {
                var existingBlockState = context.getLevel().getBlockState(context.getClickedPos());
                var property = ((SegmentableBlock) blockState.getBlock()).getSegmentAmountProperty();
                return PlacementResult.success(existingBlockState.setValue(property, Math.min(SegmentableBlock.MAX_SEGMENT, existingBlockState.getValue(property) + 1)));
            }
    );

    static <TValue extends Comparable<TValue>> BlockStateProvider property(Property<TValue> property, BlockStateProvider.ForProperty<TValue> mapper) {
        return (context, blockState) -> mapper.apply(context, blockState, blockState.getValue(property))
                .map(newValue -> blockState.setValue(property, newValue), newValue -> blockState);
    }

    static <TValue extends Comparable<TValue>> BlockStateProvider forced(Property<TValue> property, TValue value) {
        return property(property, (context, blockState, current) -> PlacementResult.success(value));
    }

    static <TValue extends Comparable<TValue>> BlockStateProvider copyValue(Property<TValue> source, Property<TValue> destination) {
        return (context, blockState) -> PlacementResult.success(blockState.setValue(destination, blockState.getValue(source)));
    }

    static BlockStateProvider transforming(PlacementValidator validator, BlockStateProvider transformer) {
        return either(validator, BlockStateProvider.SUCCESS, transformer);
    }

    static BlockStateProvider itemTransformer(TypedInstance<Item> item) {
        return itemTransformer(item.typeHolder().value());
    }

    static BlockStateProvider itemTransformer(Item item) {
        return (context, blockState) -> PlacementPreview.API.item2BlockSuppliers().get(item).apply(context, item);
    }

    static BlockStateProvider blockTransformer(Block block) {
        return toBlockTransformer((context, blockState) -> block);
    }

    static BlockStateProvider blockTransformer(BlockState forcedBlockState) {
        return toBlockStateTransformer((context, blockState) -> forcedBlockState);
    }

    static BlockStateProvider toBlockTransformer(BiFunction<BlockPlaceContext, BlockState, Block> mapper) {
        return toBlockStateTransformer((context, blockState) -> BlockStateHelper.getDefaultBlockState(context.getItemInHand(), mapper.apply(context, blockState)));
    }

    static BlockStateProvider toBlockStateTransformer(BiFunction<BlockPlaceContext, BlockState, BlockState> mapper) {
        return (context, blockState) -> {
            var remapped = mapper.apply(context, blockState);
            return PlacementPreview.API.blockStateProviders().get(remapped).apply(context, BlockStateHelper.copyFrom(blockState, remapped));
        };
    }

    static BlockStateProvider either(PlacementValidator validator, BlockStateProvider trueProvider, BlockStateProvider falseProvider) {
        return (context, blockState) -> validator.test(context, blockState) ? trueProvider.apply(context, blockState) : falseProvider.apply(context, blockState);
    }

    static BlockStateProvider ifTrue(PlacementValidator validator, BlockStateProvider provider) {
        return (context, blockState) -> validator.test(context, blockState) ? provider.apply(context, blockState) : PlacementResult.success(blockState);
    }

    static BlockStateProvider ifFalse(PlacementValidator validator, BlockStateProvider provider) {
        return ifTrue(validator.negate(), provider);
    }

    static BlockStateProvider orElse(BlockStateProvider provider, BlockStateProvider elseProvider) {
        return (context, blockState) -> provider.apply(context, blockState).flatMapFailure(state -> elseProvider.apply(context, state));
    }

    static <TValue> TValue topOrBottom(BlockPlaceContext context, TValue top, TValue bottom) {
        var clickedFace = context.getClickedFace();
        return clickedFace != Direction.DOWN && (clickedFace == Direction.UP || !(context.getClickLocation().y() - context.getClickedPos().getY() > .5D)) ? bottom : top;
    }

    interface Blocks {
        BlockStateProvider MANGROVE_PROPAGULE = WATERLOGGED.andThen(AGE_4_MAX);
        BlockStateProvider LEAVES = PERSISTENT_TRUE.andThen(WATERLOGGED).andThen(LEAVES_DISTANCE);
        BlockStateProvider RAIL = RAIL_SHAPE.andThen(WATERLOGGED).andThen(RAIL_STATE);
        BlockStateProvider POWERED_RAIL = RAIL.andThen(POWERED_RAIL_POWERED);
        BlockStateProvider DETECTOR_RAIL = RAIL.andThen(DETECTOR_RAIL_POWERED);
        BlockStateProvider PISTON = FACING_ALT.andThen(PISTON_EXTENDED);
        BlockStateProvider SHELF = HORIZONTAL_FACING_ALT.andThen(POWERED).andThen(WATERLOGGED);
        BlockStateProvider CREAKING_HEART = ROTATED_PILLAR.andThen(CREAKING_HEART_TYPE);
        BlockStateProvider CHEST = CHEST_TYPE.andThen(WATERLOGGED);
        BlockStateProvider ENDER_CHEST = HORIZONTAL_FACING_ALT.andThen(WATERLOGGED);
        BlockStateProvider STANDING_SIGN = ROTATION_ALT.andThen(WATERLOGGED);
        BlockStateProvider DOOR = HORIZONTAL_FACING.andThen(DOOR_HINGE).andThen(POWERED_DOUBLE).andThen(OPEN_FROM_POWERED);
        BlockStateProvider LADDER = WALL_ATTACHMENT_ALT.andThen(LADDER_VALIDTION).andThen(WATERLOGGED);
        BlockStateProvider WALL_SIGN = WALL_ATTACHMENT_ALT.andThen(WATERLOGGED);
        BlockStateProvider HANGING_SIGN = HANGING_SIGN_ATTACHMENT.andThen(WATERLOGGED);
        BlockStateProvider WALL_HANGING_SIGN = WallAttachmentBlockStateProvider.WALL_HANGING_SIGN.andThen(WATERLOGGED);
        BlockStateProvider REDSTONE_WALL_TORCH = WALL_ATTACHMENT_ALT.andThen(REDSTONE_TORCH_LIT);
        BlockStateProvider FENCE = ConnectionBlockStateProvider.FENCE.andThen(WATERLOGGED);
        BlockStateProvider REPEATER = HORIZONTAL_FACING_ALT.andThen(DIODE_LOCKED);
        BlockStateProvider TRAPDOOR = TRAPDOOR_ROTATION.andThen(POWERED).andThen(OPEN_FROM_POWERED).andThen(WATERLOGGED);
        BlockStateProvider IRON_BAR = ConnectionBlockStateProvider.IRON_BARS.andThen(WATERLOGGED);
        BlockStateProvider CHAIN = ROTATED_PILLAR.andThen(WATERLOGGED);
        BlockStateProvider FENCE_GATE = HORIZONTAL_FACING.andThen(FENCE_GATE_IN_WALL).andThen(POWERED).andThen(OPEN_FROM_POWERED);
        BlockStateProvider SLAB = WATERLOGGED.andThen(SLAB_TYPE);
        BlockStateProvider WALL = ConnectionBlockStateProvider.WALL.andThen(WATERLOGGED);
        BlockStateProvider END_PORTAL_FRAME = HORIZONTAL_FACING_ALT.andThen(HAS_EYE_FALSE);
        BlockStateProvider HOPPER = FACING_HOPPER.andThen(ENABLED_TRUE);
        BlockStateProvider COMPARATOR = HORIZONTAL_FACING_ALT.andThen(POWERED_COMPARATOR);
        BlockStateProvider REDSTONE_WIRE = REDSTONE_WIRE_CONNECTIONS.andThen(REDSTONE_WIRE_POWER);
        BlockStateProvider SHULKER_BOX = CLICKED_FACE_FIXED;
        BlockStateProvider CONCRETE_POWDER = transforming(
                (context, blockState) -> {
                    var level = context.getLevel();
                    var pos = context.getClickedPos();
                    return !ConcretePowderBlock.shouldSolidify(level, pos, level.getBlockState(pos));
                },
                toBlockTransformer((context, blockState) -> ((ConcretePowderBlock) blockState.getBlock()).concrete)
        );
        BlockStateProvider DRIED_GHAST = WATERLOGGED.andThen(HORIZONTAL_FACING_ALT);
        BlockStateProvider CORAL = coral(
                (context, blockState) -> ((CoralBlockAccessor) blockState.getBlock()).PlacementPreview$scanForWater(context.getLevel(), context.getClickedPos()),
                block -> ((CoralBlock) block).deadBlock
        );
        BlockStateProvider CORAL_PLANT = WATERLOGGED.andThen(coral(
                (context, blockState) -> BaseCoralFanBlock.scanForWater(blockState, context.getLevel(), context.getClickedPos()),
                block -> ((CoralPlantBlock) block).deadBlock
        ));
        BlockStateProvider CORAL_FAN = WATERLOGGED.andThen(coral(
                (context, blockState) -> BaseCoralFanBlock.scanForWater(blockState, context.getLevel(), context.getClickedPos()),
                block -> ((CoralFanBlock) block).deadBlock
        ));
        BlockStateProvider DEAD_WALL_CORAL_FAN = WATERLOGGED.andThen(WALL_ATTACHMENT_ALT);
        BlockStateProvider WALL_CORAL_FAN = DEAD_WALL_CORAL_FAN.andThen(coral(
                (context, blockState) -> BaseCoralFanBlock.scanForWater(blockState, context.getLevel(), context.getClickedPos()),
                block -> ((CoralWallFanBlock) block).deadBlock
        ));
        BlockStateProvider SEA_PICKLE = PICKELS.andThen(WATERLOGGED);
        BlockStateProvider BAMBOO_STALK = (context, blockState) -> {
            var level = context.getLevel();
            var pos = context.getClickedPos();

            var belowPos = pos.below();
            var belowBlockState = level.getBlockState(belowPos);
            var soilDecision = belowBlockState.canSustainPlant(level, belowPos, Direction.UP, blockState);
            var placeable = soilDecision.isDefault() ? belowBlockState.is(BlockTags.SUPPORTS_BAMBOO) : soilDecision.isTrue();
            var result = blockState;

            if(belowBlockState.is(net.minecraft.world.level.block.Blocks.BAMBOO_SAPLING)) {
                result = blockState.setValue(BlockStateProperties.AGE_1, BambooStalkBlock.STAGE_GROWING);
            } else if(belowBlockState.is(net.minecraft.world.level.block.Blocks.BAMBOO)) {
                var age = belowBlockState.getValue(BlockStateProperties.AGE_1);
                result = blockState.setValue(BlockStateProperties.AGE_1, age > 0 ? BambooStalkBlock.STAGE_DONE_GROWING : BambooStalkBlock.STAGE_GROWING);
            } else {
                var aboveBlockState = level.getBlockState(pos.above());

                if(aboveBlockState.is(net.minecraft.world.level.block.Blocks.BAMBOO)) {
                    result = blockState.setValue(BlockStateProperties.AGE_1, aboveBlockState.getValue(BlockStateProperties.AGE_1));
                } else {
                    var transformed = BlockStateProvider.applyDefaults(context, net.minecraft.world.level.block.Blocks.BAMBOO_SAPLING);
                    result = transformed.value();
                    placeable = transformed.isSuccess();
                }
            }

            if(!level.getFluidState(pos).isEmpty()) {
                placeable = false;
            }

            return PlacementResult.of(result, placeable);
        };
        BlockStateProvider SCAFFOLDING = WATERLOGGED.andThen((context, blockState) -> {
            var level = context.getLevel();
            var pos = context.getClickedPos();
            var distance = ScaffoldingBlock.getDistance(level, pos);
            return PlacementResult.success(blockState
                    .setValue(BlockStateProperties.STABILITY_DISTANCE, distance)
                    .setValue(BlockStateProperties.BOTTOM, ((ScaffoldingBlockAccessor) blockState.getBlock()).PlacementPreview$isBottom(level, pos, distance))
            );
        });
        BlockStateProvider LECTERN = HORIZONTAL_FACING_ALT.andThen(ifTrue(PlacementValidators.GAMEMASTER_ALLOWED, property(BlockStateProperties.HAS_BOOK, (context, blockState, current) -> {
            var blockEntityData = context.getItemInHand().get(DataComponents.BLOCK_ENTITY_DATA);
            return PlacementResult.success(blockEntityData != null && blockEntityData.contains("Book"));
        })));
        BlockStateProvider BELL = (context, blockState) -> {
            var level = context.getLevel();
            var pos = context.getClickedPos();
            var clickedFace = context.getClickedFace();
            var axis = clickedFace.getAxis();
            BlockState result;

            if(axis.isVertical()) {
                result = blockState.setValue(BlockStateProperties.BELL_ATTACHMENT, clickedFace == Direction.DOWN ? BellAttachType.CEILING : BellAttachType.FLOOR)
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection());
            } else {
                var northPos = pos.north();
                var eastPos = pos.east();
                var southPos = pos.south();
                var westPos = pos.west();

                var doubleAttached = (
                        axis == Direction.Axis.X &&
                                level.getBlockState(westPos).isFaceSturdy(level, westPos, Direction.EAST) &&
                                level.getBlockState(eastPos).isFaceSturdy(level, eastPos, Direction.WEST)
                ) || (axis == Direction.Axis.Z &&
                        level.getBlockState(northPos).isFaceSturdy(level, northPos, Direction.SOUTH) &&
                        level.getBlockState(southPos).isFaceSturdy(level, southPos, Direction.NORTH)
                );

                result = blockState.setValue(BlockStateProperties.BELL_ATTACHMENT, doubleAttached ? BellAttachType.DOUBLE_WALL : BellAttachType.SINGLE_WALL)
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, clickedFace.getOpposite());

                if(!result.canSurvive(level, pos)) {
                    var belowPos = pos.below();
                    var canAttachBelow = level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
                    result = blockState.setValue(BlockStateProperties.BELL_ATTACHMENT, canAttachBelow ? BellAttachType.FLOOR : BellAttachType.CEILING);
                }
            }

            return PlacementResult.of(result, result.canSurvive(level, pos));
        };
        BlockStateProvider LANTERN = WallAttachmentBlockStateProvider.LANTERN.andThen(WATERLOGGED);
        BlockStateProvider CAMPFIRE = WATERLOGGED.andThen(SIGNAL_FIRE).andThen(LIT_IF_NOT_WATERLOGGED).andThen(HORIZONTAL_FACING);
        BlockStateProvider KELP = GROWING_PLANT.andThen(INSIDE_WATER);
        BlockStateProvider JIGSAW = either(
                PlacementValidators.CLICKED_EMPTY_BLOCK,
                property(BlockStateProperties.ORIENTATION, (context, blockState, current) -> {
                    var front = context.getNearestLookingDirection().getOpposite();
                    return PlacementResult.success(FrontAndTop.fromFrontAndTop(front, front.getAxis().isVertical() ? context.getHorizontalDirection().getOpposite() : Direction.UP));
                }),
                ORIENTATION
        );
        BlockStateProvider CANDLE = CANDLES.andThen(WATERLOGGED);
        BlockStateProvider AMETHYST_CLUSTER = WATERLOGGED.andThen(CLICKED_FACE_FIXED);
        BlockStateProvider STAIR = HORIZONTAL_FACING.andThen(HALF).andThen(WATERLOGGED).andThen(STAIR_SHAPE);
        BlockStateProvider POTENT_SULFUR = (context, blockState) -> PlacementResult.success(PotentSulfurBlock.validBlockState(blockState, context.getLevel(), context.getClickedPos()));
        BlockStateProvider CALIBRATED_SCULK_SENSOR = WATERLOGGED.andThen(HORIZONTAL_FACING);
        BlockStateProvider COPPER_BULB = POWERED.andThen(LIT_FROM_POWERED);
        BlockStateProvider COPPER_CHEST = CHEST.andThen(LEAST_OXIDIZED_COPPER_CHEST);
        BlockStateProvider COPPER_GOLEM = HORIZONTAL_FACING_ALT.andThen(WATERLOGGED);
        BlockStateProvider LIGHTNING_ROD = CLICKED_FACE_FIXED.andThen(WATERLOGGED);
        BlockStateProvider SPELEOTHEM_BLOCK = SPELEOTHEM.andThen(WATERLOGGED);
        BlockStateProvider FLOWER_BED = HORIZONTAL_FACING_ALT.andThen(SEGMENTABLE);

        static BlockStateProvider coral(PlacementValidator validator, UnaryOperator<Block> deadBlockMapper) {
            return transforming(
                    validator,
                    toBlockTransformer((context, blockState) -> deadBlockMapper.apply(blockState.getBlock()))
            );
        }
    }
}
