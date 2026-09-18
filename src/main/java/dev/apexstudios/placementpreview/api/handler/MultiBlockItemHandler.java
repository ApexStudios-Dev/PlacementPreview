package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.BlockStateHelper;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.validator.PlacementValidators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

// TODO: Render block outlines for invalid mutli block placements?
@FunctionalInterface
public interface MultiBlockItemHandler extends BlockItemHandler {
    UseOnHandler DOUBLE_HIGH = MultiBlockItemHandler.forProperty(
            BlockStateProperties.DOUBLE_BLOCK_HALF,
            DoubleBlockHalf::getOtherHalf,
            DoubleBlockHalf::getDirectionToOther
    );

    UseOnHandler BED = forPropertyState(
            BlockStateProperties.BED_PART,
            part -> switch (part) {
                case HEAD -> BedPart.FOOT;
                case FOOT -> BedPart.HEAD;
            },
            (pos, blockState) -> pos.relative(BedBlock.getConnectedDirection(blockState))
    );

    UseOnHandler PISTON = new MultiBlockItemHandler() {
        @Override
        public void setAdditionalBlocks(GhostLevel level, BlockPlaceContext context, PlacementResult<BlockState> blockStateResult) {
            var blockState = blockStateResult.value();

            if(!blockState.getValue(BlockStateProperties.EXTENDED)) {
                return;
            }

            var facing = blockState.getValue(BlockStateProperties.FACING);
            var headBlockState = BlockStateHelper.copyFrom(blockState, BlockStateHelper.getDefaultBlockState(context.getItemInHand(), Blocks.PISTON_HEAD))
                    .setValue(BlockStateProperties.PISTON_TYPE, blockState.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT);

            setMultiBlock(
                    level,
                    context,
                    context.getClickedPos().relative(facing),
                    PlacementResult.of(headBlockState, blockStateResult.isSuccess())
            );
        }
    };

    void setAdditionalBlocks(GhostLevel level, BlockPlaceContext context, PlacementResult<BlockState> blockStateResult);

    default void setMultiBlock(GhostLevel level, BlockPlaceContext context, BlockPos pos, PlacementResult<BlockState> blockState) {
        if(blockState.isSuccess()) {
            var passed = PlacementValidators.PLACEABLE_MULTI.test(new BlockPlaceContext(context) {
                @Override
                public BlockPos getClickedPos() {
                    return pos;
                }
            }, blockState.value());

            if(!passed) {
                blockState = blockState.asFailure();
            }
        }

        setBlock(level, context, pos, blockState);
    }

    @Override
    default @Nullable PlacementResult<BlockState> accept(GhostLevel level, BlockPlaceContext context, BlockItem item, boolean initialSuccess) {
        var result = BlockItemHandler.super.accept(level, context, item, initialSuccess);

        if(result != null) {
            setAdditionalBlocks(level, context, result);
        }

        return result;
    }

    static <TValue extends Comparable<TValue>> UseOnHandler forPropertyState(Property<TValue> property, UnaryOperator<TValue> otherValuerMapper, BiFunction<BlockPos, BlockState, BlockPos> offsetPosMapper) {
        return new MultiBlockItemHandler() {
            @Override
            public void setAdditionalBlocks(GhostLevel level, BlockPlaceContext context, PlacementResult<BlockState> blockStateResult) {
                setMultiBlock(
                        level,
                        context,
                        offsetPosMapper.apply(context.getClickedPos(), blockStateResult.value()),
                        blockStateResult.map(blockState -> blockState.setValue(property, otherValuerMapper.apply(blockState.getValue(property))))
                );
            }
        };
    }

    static <TValue extends Comparable<TValue>> UseOnHandler forProperty(Property<TValue> property, UnaryOperator<TValue> otherValuerMapper, BiFunction<BlockPos, TValue, BlockPos> offsetPosMapper) {
        return forPropertyState(property, otherValuerMapper, (pos, blockState) -> offsetPosMapper.apply(pos, blockState.getValue(property)));
    }

    static <TValue extends Comparable<TValue>> UseOnHandler forProperty(Property<TValue> property, UnaryOperator<TValue> otherValuerMapper, Function<TValue, Direction> offsetMapper) {
        return forProperty(
                property,
                otherValuerMapper,
                (pos, value) -> pos.relative(offsetMapper.apply(value))
        );
    }}
