package dev.apexstudios.placementpreview.api.provider;

import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.mixin.VineBlockAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class WallAttachmentBlockStateProvider implements BlockStateProvider {
    public static final BlockStateProvider HORIZONTAL = new WallAttachmentBlockStateProvider() {
        @Override
        protected boolean validAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            return direction.getAxis().isHorizontal();
        }

        @Override
        protected BlockState setAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            return blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, direction);
        }
    };

    public static final BlockStateProvider HORIZONTAL_ALT = new WallAttachmentBlockStateProvider() {
        @Override
        protected boolean validAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            return direction.getAxis().isHorizontal();
        }

        @Override
        protected BlockState setAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            return blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getOpposite());
        }
    };

    /// Requires: [WallHangingSignBlock]
    public static final BlockStateProvider WALL_HANGING_SIGN = new WallAttachmentBlockStateProvider() {
        @Override
        protected boolean validAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            var axis = direction.getAxis();
            return axis.isHorizontal() || axis.test(context.getClickedFace());
        }

        @Override
        protected BlockState setAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            return blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getOpposite());
        }

        @Override
        protected boolean canAttachmentSurvive(BlockPlaceContext context, BlockState blockState, Direction direction) {
            if (!super.canAttachmentSurvive(context, blockState, direction)) {
                return false;
            }

            return ((WallHangingSignBlock) blockState.getBlock())
                    .canPlace(blockState, context.getLevel(), context.getClickedPos());
        }
    };

    public static final BlockStateProvider FACING_HORIZONTAL = new WallAttachmentBlockStateProvider() {
        @Override
        protected boolean validAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            return true;
        }

        @Override
        protected BlockState setAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            var attached = AttachFace.WALL;
            var facingDirection = direction.getOpposite();

            if(direction.getAxis().isVertical()) {
                attached = direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR;
                facingDirection = context.getHorizontalDirection();
            }

            return blockState.setValue(BlockStateProperties.ATTACH_FACE, attached)
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facingDirection);
        }
    };

    /// Requires: [VineBlock]
    public static final BlockStateProvider VINE = new WallAttachmentBlockStateProvider() {
        @Override
        protected boolean validAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            return direction != Direction.DOWN;
        }

        @Override
        protected BlockState setAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            var existingBlockState = context.getLevel().getBlockState(context.getClickedPos());

            return (existingBlockState.is(blockState.getBlock()) ? existingBlockState : blockState)
                    .setValue(getPropertyForFace(direction), true);
        }

        @Override
        protected boolean canAttachmentSurvive(BlockPlaceContext context, BlockState blockState, Direction direction) {
            var level = context.getLevel();
            var pos = context.getClickedPos();

            var existingBlockState = level.getBlockState(pos);
            var faceOccupied = existingBlockState.is(blockState.getBlock()) && existingBlockState.getValue(getPropertyForFace(direction));

            return !faceOccupied && ((VineBlockAccessor) blockState.getBlock())
                    .PlacementPreview$canSupportAtFace(level, pos, direction);
        }

        private static BooleanProperty getPropertyForFace(Direction direction) {
            return VineBlock.getPropertyForFace(direction);
        }
    };

    /// Requires: [MultifaceBlock]
    public static final BlockStateProvider MULTI_FACE = new WallAttachmentBlockStateProvider() {
        @Override
        protected boolean validAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            return true;
        }

        @Override
        protected BlockState setAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction) {
            var existingBlockState = context.getLevel().getBlockState(context.getClickedPos());

            return (existingBlockState.is(blockState.getBlock()) ? existingBlockState : blockState)
                    .setValue(MultifaceBlock.getFaceProperty(direction), true);
        }

        @Override
        protected boolean canAttachmentSurvive(BlockPlaceContext context, BlockState blockState, Direction direction) {
            var level = context.getLevel();
            var pos = context.getClickedPos();

            return ((MultifaceBlock) blockState.getBlock())
                    .isValidStateForPlacement(level, level.getBlockState(pos), pos, direction);
        }
    };

    @Override
    public PlacementResult<BlockState> apply(BlockPlaceContext context, BlockState blockState) {
        var directions = attachmentDirections(context, blockState);

        for(var direction : directions) {
            if(!validAttachmentDirection(context, blockState, direction)) {
                continue;
            }

            var attachmentBlockState = setAttachmentDirection(context, blockState, direction);

            if(canAttachmentSurvive(context, attachmentBlockState, direction)) {
                return PlacementResult.success(attachmentBlockState);
            }
        }

        return PlacementResult.failure(blockState);
    }

    protected Direction[] attachmentDirections(BlockPlaceContext context, BlockState blockState) {
        return context.getNearestLookingDirections();
    }

    protected abstract boolean validAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction);

    protected abstract BlockState setAttachmentDirection(BlockPlaceContext context, BlockState blockState, Direction direction);

    protected boolean canAttachmentSurvive(BlockPlaceContext context, BlockState blockState, Direction direction) {
        return blockState.canSurvive(context.getLevel(), context.getClickedPos());
    }
}
