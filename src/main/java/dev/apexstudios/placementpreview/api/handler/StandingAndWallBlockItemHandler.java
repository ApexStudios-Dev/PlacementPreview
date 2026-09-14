package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.provider.BlockStateProvider;
import dev.apexstudios.placementpreview.mixin.StandingAndWallBlockItemAccessor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public interface StandingAndWallBlockItemHandler extends BlockItemHandler {
    @Override
    default PlacementResult<BlockState> apply(BlockPlaceContext context, BlockItem item) {
        var standingAndWallItem = (StandingAndWallBlockItem) item;
        var attachmentDirectionOpposite = standingAndWallItem.attachmentDirection.getOpposite();
        var standingBlock = standingAndWallItem.getBlock();

        for(var direction : context.getNearestLookingDirections()) {
            if(direction != attachmentDirectionOpposite) {
                var block = direction == standingAndWallItem.attachmentDirection ? standingBlock : standingAndWallItem.wallBlock;
                var result = BlockStateProvider.applyDefaults(context, block)
                        .filter(blockState -> canPlace(context, blockState, standingAndWallItem));

                if(result.isSuccess()) {
                    return result;
                }
            }
        }

        return PlacementResult.failure(standingBlock.defaultBlockState());
    }

    default boolean canPlace(BlockPlaceContext context, BlockState blockState, StandingAndWallBlockItem item) {
        return ((StandingAndWallBlockItemAccessor) item).PlacementPreview$canPlace(context.getLevel(), blockState, context.getClickedPos());
    }
}
