package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.provider.BlockStateProvider;
import dev.apexstudios.placementpreview.api.validator.PlacementValidators;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BlockItemHandler implements UseOnHandler {
    @Override
    public boolean accept(GhostLevel level, UseOnContext context) {
        var contextResult = updatePlacementContext(new BlockPlaceContext(context));
        var placementContext = contextResult.value();

        if(!(placementContext.getItemInHand().getItem() instanceof BlockItem item)) {
            return false;
        }

        return accept(level, placementContext, item, contextResult.isSuccess());
    }

    protected boolean accept(GhostLevel level, BlockPlaceContext context, BlockItem item, boolean initialSuccess) {
        var blockStateResult = apply(context, item);
        var blockState = blockStateResult.value();

        if(blockState.isEmpty()) {
            return false;
        }

        if(!initialSuccess) {
            blockStateResult = blockStateResult.asFailure();
        }

        setBlock(level, context, context.getClickedPos(), blockStateResult, true);
        return true;
    }

    protected PlacementResult<BlockState> apply(BlockPlaceContext context, BlockItem item) {
        return BlockStateProvider.applyDefaults(context, item.getBlock());
    }

    protected PlacementResult<BlockPlaceContext> updatePlacementContext(BlockPlaceContext context) {
        if(!(context.getItemInHand().getItem() instanceof BlockItem item)) {
            return PlacementResult.success(context);
        }

        var updatedContext = item.updatePlacementContext(context);

        if(updatedContext == null) {
            return PlacementResult.failure(context);
        }

        return PlacementResult.success(updatedContext);
    }

    protected boolean withinPlacementRange(BlockPlaceContext context) {
        var entity = context.getPlayer();

        if(entity == null) {
            return true;
        }

        var blockInteractionRange = entity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        var entityInteractionRange = entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);

        var interactionRange = Math.max(blockInteractionRange, entityInteractionRange);
        var interactionRangeSq = Mth.square(interactionRange);

        return new AABB(context.getClickedPos()).distanceToSqr(entity.getEyePosition()) < interactionRangeSq;
    }

    protected void setBlock(GhostLevel level, BlockPlaceContext context, BlockPos pos, PlacementResult<BlockState> result, boolean isValid) {
        var blockState = result.value();
        var isPlaceable = result.isSuccess() && isValid;

        if(isPlaceable) {
            isPlaceable = PlacementValidators.PLACEABLE.test(context, blockState);
        }

        level.setBlockState(pos, blockState, isPlaceable);
        level.setBlockEntity(pos, blockState, context.getItemInHand(), isPlaceable);
    }
}
