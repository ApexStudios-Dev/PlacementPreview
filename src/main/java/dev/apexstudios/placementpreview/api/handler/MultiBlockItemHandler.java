package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.validator.PlacementValidators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;

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

    void setAdditionalBlocks(GhostLevel level, BlockPlaceContext context, BlockState blockState, boolean success);

    @Override
    default boolean accept(GhostLevel level, BlockPlaceContext context, BlockItem item, boolean initialSuccess) {
        if(BlockItemHandler.super.accept(level, context, item, initialSuccess)) {
            setAdditionalBlocks(level, context, level.getBlockState(context.getClickedPos()), initialSuccess);
            return true;
        }

        return false;
    }

    static <TValue extends Comparable<TValue>> UseOnHandler forPropertyState(Property<TValue> property, UnaryOperator<TValue> otherValuerMapper, BiFunction<BlockPos, BlockState, BlockPos> offsetPosMapper) {
        return new MultiBlockItemHandler() {
            @Override
            public void setAdditionalBlocks(GhostLevel level, BlockPlaceContext context, BlockState blockState, boolean success) {
                var value = blockState.getValue(property);

                var otherContext = new BlockPlaceContext(context) {
                    @Override
                    public BlockPos getClickedPos() {
                        return offsetPosMapper.apply(context.getClickedPos(), blockState);
                    }
                };

                // TODO: whole block vs multi block validation should be a ghost property
                var isPlaceable = success;
                var otherBlockState = blockState.setValue(property, otherValuerMapper.apply(value));

                if(isPlaceable) {
                    isPlaceable = PlacementValidators.PLACEABLE_MULTI.test(otherContext, otherBlockState);
                }

                setBlock(level, otherContext, otherContext.getClickedPos(), otherBlockState, isPlaceable);
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
