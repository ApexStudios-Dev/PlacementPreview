package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.placementpreview.api.PlacementResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;

public interface PlaceOnWaterHandler extends BlockItemHandler {
    @Override
    default PlacementResult<BlockPlaceContext> updatePlacementContext(BlockPlaceContext context) {
        var player = context.getPlayer();
        var superResult = BlockItemHandler.super.updatePlacementContext(context);

        if(player == null) {
            return superResult.asFailure();
        }

        var updatedContext = superResult.value();
        var hitResult = Item.getPlayerPOVHitResult(updatedContext.getLevel(), player, ClipContext.Fluid.SOURCE_ONLY);

        return PlacementResult.success(BlockPlaceContext.at(
                updatedContext,
                hitResult.getBlockPos().above(),
                hitResult.getDirection()
        ));
    }
}
