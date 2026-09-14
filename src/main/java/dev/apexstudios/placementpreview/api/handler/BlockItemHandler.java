package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.provider.BlockStateProvider;
import dev.apexstudios.placementpreview.api.validator.PlacementValidators;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockItemHandler extends UseOnHandler {
    @Override
    default boolean accept(GhostLevel level, UseOnContext context) {
        var contextResult = updatePlacementContext(new BlockPlaceContext(context));
        var placementContext = contextResult.value();

        if(!(placementContext.getItemInHand().getItem() instanceof BlockItem item)) {
            return false;
        }

        return accept(level, placementContext, item, contextResult.isSuccess());
    }

    default boolean accept(GhostLevel level, BlockPlaceContext context, BlockItem item, boolean initialSuccess) {
        var blockStateResult = apply(context, item);
        var blockState = blockStateResult.value();

        if(blockState.isEmpty()) {
            return false;
        }

        var isPlaceable = initialSuccess;

        if(isPlaceable) {
            isPlaceable = PlacementValidators.PLACEABLE.test(context, blockState);
        }

        setBlock(level, context, context.getClickedPos(), blockState, isPlaceable);
        return true;
    }

    default PlacementResult<BlockState> apply(BlockPlaceContext context, BlockItem item) {
        return BlockStateProvider.applyDefaults(context, item.getBlock());
    }

    default PlacementResult<BlockPlaceContext> updatePlacementContext(BlockPlaceContext context) {
        if(!(context.getItemInHand().getItem() instanceof BlockItem item)) {
            return PlacementResult.success(context);
        }

        var updatedContext = item.updatePlacementContext(context);

        if(updatedContext == null) {
            return PlacementResult.failure(context);
        }

        return PlacementResult.success(updatedContext);
    }

    default void setBlock(GhostLevel level, BlockPlaceContext context, BlockPos pos, BlockState blockState, boolean isValid) {
        level.setBlockState(pos, blockState, isValid);
        level.setBlockEntity(pos, blockState, context.getItemInHand(), isValid);
    }
}
