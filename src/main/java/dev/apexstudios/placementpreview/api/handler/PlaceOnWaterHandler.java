package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.placementpreview.api.PlacementResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;

public class PlaceOnWaterHandler extends BlockItemHandler {
    @Override
    protected PlacementResult<BlockPlaceContext> updatePlacementContext(BlockPlaceContext context) {
        var player = context.getPlayer();
        var superResult = super.updatePlacementContext(context);

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
