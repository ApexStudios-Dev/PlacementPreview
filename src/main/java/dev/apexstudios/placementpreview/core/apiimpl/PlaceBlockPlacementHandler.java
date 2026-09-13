package dev.apexstudios.placementpreview.core.apiimpl;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.handler.PlacementHandler;
import dev.apexstudios.placementpreview.api.validator.PlacementValidators;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.AABB;

final class PlaceBlockPlacementHandler implements PlacementHandler {
    @Override
    public boolean accept(GhostLevel level, BlockPlaceContext context) {
        var contextResult = updateContext(context);
        var updatedContext = contextResult.value();
        var updatedItem = updatedContext.getItemInHand();

        if(updatedItem.isEmpty()) {
            return false;
        }

        var blockStateResult = PlacementPreviewApiImpl.API.item2BlockSuppliers().get(updatedItem).apply(updatedContext, updatedItem.getItem());
        var blockState = blockStateResult.value();

        var isPlaceable = contextResult.isSuccess() && blockStateResult.isSuccess();

        if(isPlaceable) {
            isPlaceable = PlacementValidators.PLACEABLE.test(updatedContext, blockState);
        }

        var pos = updatedContext.getClickedPos();
        level.setBlockState(pos, blockState, isPlaceable);
        level.setBlockEntity(pos, blockState, updatedItem, isPlaceable);
        return true;
    }

    private PlacementResult<BlockPlaceContext> updateContext(BlockPlaceContext context) {
        if(!(context.getItemInHand().getItem() instanceof BlockItem item)) {
            return PlacementResult.success(context);
        }

        var updatedContext = item.updatePlacementContext(context);

        if(updatedContext == null) {
            return PlacementResult.failure(context);
        }

        return PlacementResult.success(updatedContext);
    }

    private boolean withinPlacementRange(BlockPlaceContext context) {
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
}
