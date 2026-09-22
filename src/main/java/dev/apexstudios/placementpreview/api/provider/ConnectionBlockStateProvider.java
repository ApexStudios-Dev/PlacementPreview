package dev.apexstudios.placementpreview.api.provider;

import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.mixin.WallBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.redstone.NeighborUpdater;

public abstract class ConnectionBlockStateProvider implements BlockStateProvider {
    public static final BlockStateProvider IRON_BARS = new ConnectionBlockStateProvider() {
        @Override
        protected boolean connectsTo(LevelReader level, BlockPos pos, BlockState blockState, Direction connectionDir) {
            var relativePos = pos.relative(connectionDir);
            var relativeBlockState = level.getBlockState(relativePos);
            return ((IronBarsBlock) blockState.getBlock()).attachsTo(relativeBlockState, relativeBlockState.isFaceSturdy(level, relativePos, connectionDir.getOpposite()));
        }
    };

    public static final BlockStateProvider WALL = new ConnectionBlockStateProvider() {
        @Override
        protected BlockState updateConnections(LevelReader level, BlockPos pos, BlockState blockState) {
            var abovePos = pos.above();

            return ((WallBlockAccessor) blockState.getBlock()).PlacementPreview$updateShape(
                    level,
                    blockState,
                    abovePos,
                    level.getBlockState(abovePos),
                    connectsTo(level, pos, blockState, Direction.NORTH),
                    connectsTo(level, pos, blockState, Direction.EAST),
                    connectsTo(level, pos, blockState, Direction.SOUTH),
                    connectsTo(level, pos, blockState, Direction.WEST)
            );
        }

        @Override
        protected boolean connectsTo(LevelReader level, BlockPos pos, BlockState blockState, Direction connectionDir) {
            var connectionOpposite = connectionDir.getOpposite();
            var relativePos = pos.relative(connectionDir);
            var relativeBlockState = level.getBlockState(relativePos);
            return ((WallBlockAccessor) blockState.getBlock()).PlacementPreview$connectsTo(relativeBlockState, relativeBlockState.isFaceSturdy(level, relativePos, connectionOpposite), connectionOpposite);
        }
    };

    public static final BlockStateProvider FENCE = new ConnectionBlockStateProvider() {
        @Override
        protected boolean connectsTo(LevelReader level, BlockPos pos, BlockState blockState, Direction connectionDir) {
            var connectionOpposite = connectionDir.getOpposite();
            var relativePos = pos.relative(connectionDir);
            var relativeBlockState = level.getBlockState(relativePos);
            return ((FenceBlock) blockState.getBlock()).connectsTo(relativeBlockState, relativeBlockState.isFaceSturdy(level, relativePos, connectionOpposite), connectionOpposite);
        }
    };

    public static final BlockStateProvider MUSHROOM = new ConnectionBlockStateProvider() {
        @Override
        protected BlockState updateConnections(LevelReader level, BlockPos pos, BlockState blockState) {
            var result = blockState;

            for(var connectionDir : NeighborUpdater.UPDATE_ORDER) {
                result = result.setValue(property(connectionDir), connectsTo(level, pos, blockState, connectionDir));
            }

            return result;
        }

        @Override
        protected boolean connectsTo(LevelReader level, BlockPos pos, BlockState blockState, Direction connectionDir) {
            return !level.getBlockState(pos.relative(connectionDir)).is(blockState.getBlock());
        }
    };

    public static final BlockStateProvider TRIPWIRE = new ConnectionBlockStateProvider() {
        @Override
        protected boolean connectsTo(LevelReader level, BlockPos pos, BlockState blockState, Direction connectionDir) {
            return ((TripWireBlock) blockState.getBlock()).shouldConnectTo(level.getBlockState(pos.relative(connectionDir)), connectionDir);
        }
    };

    @Override
    public PlacementResult<BlockState> apply(BlockPlaceContext context, BlockState blockState) {
        return PlacementResult.success(updateConnections(context.getLevel(), context.getClickedPos(), blockState));
    }

    protected BlockState updateConnections(LevelReader level, BlockPos pos, BlockState blockState) {
        var result = blockState;

        for(var connectionDir : Direction.Plane.HORIZONTAL) {
            result = result.setValue(property(connectionDir), connectsTo(level, pos, blockState, connectionDir));
        }

        return result;
    }

    protected abstract boolean connectsTo(LevelReader level, BlockPos pos, BlockState blockState, Direction connectionDir);

    protected static Property<Boolean> property(Direction direction) {
        return switch (direction) {
            case UP -> BlockStateProperties.UP;
            case DOWN -> BlockStateProperties.DOWN;
            case NORTH -> BlockStateProperties.NORTH;
            case EAST -> BlockStateProperties.EAST;
            case SOUTH -> BlockStateProperties.SOUTH;
            case WEST -> BlockStateProperties.WEST;
        };
    }
}
