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
import org.jspecify.annotations.Nullable;

public interface BlockItemHandler extends UseOnHandler {
    @Override
    default @Nullable PlacementResult<BlockState> accept(GhostLevel ghosts, UseOnContext context) {
        var contextResult = updatePlacementContext(new BlockPlaceContext(context));
        var placementContext = contextResult.value();

        if(!(placementContext.getItemInHand().getItem() instanceof BlockItem item)) {
            return null;
        }

        return accept(ghosts, placementContext, item, contextResult.isSuccess());
    }

    default @Nullable PlacementResult<BlockState> accept(GhostLevel ghosts, BlockPlaceContext context, BlockItem item, boolean initialSuccess) {
        var blockStateResult = apply(context, item);
        var blockState = blockStateResult.value();

        if(blockState.isEmpty()) {
            return null;
        }

        if(!initialSuccess || !PlacementValidators.PLACEABLE.test(context, blockState)) {
            blockStateResult = blockStateResult.asFailure();
        }

        setBlock(ghosts, context, context.getClickedPos(), blockStateResult);
        return blockStateResult;
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

    default void setBlock(GhostLevel ghosts, BlockPlaceContext context, BlockPos pos, PlacementResult<BlockState> blockStateResult) {
        var blockState = blockStateResult.value();
        var isValid = blockStateResult.isSuccess();

        ghosts.setBlockState(pos, blockState, isValid);
        ghosts.setBlockEntity(pos, blockState, context.getItemInHand(), isValid);
    }
}
