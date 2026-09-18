package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import net.minecraft.world.item.context.UseOnContext;
import org.jspecify.annotations.Nullable;

// TODO: Come up with a better name
@FunctionalInterface
public interface UseOnHandler {
    UseOnHandler BLOCK_ITEM = new BlockItemHandler() {};
    UseOnHandler STANDING_WALL = new StandingAndWallBlockItemHandler() {};
    UseOnHandler PLACE_ON_WATER = new PlaceOnWaterHandler() {};
    UseOnHandler SPAWN_EGG = new SpawnEggHandler() {};
    UseOnHandler CUSHION = new CushionHandler() {};

    @Nullable PlacementResult<?> accept(GhostLevel level, UseOnContext context);
}
