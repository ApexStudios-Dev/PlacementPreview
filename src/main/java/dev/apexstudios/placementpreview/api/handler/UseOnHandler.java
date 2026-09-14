package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import net.minecraft.world.item.context.UseOnContext;

// TODO: Come up with a better name
@FunctionalInterface
public interface UseOnHandler {
    UseOnHandler BLOCK_ITEM = new BlockItemHandler() {};
    UseOnHandler STANDING_WALL = new StandingAndWallBlockItemHandler() {};
    UseOnHandler PLACE_ON_WATER = new PlaceOnWaterHandler() {};
    UseOnHandler SPAWN_EGG = new SpawnEggHandler() {};

    boolean accept(GhostLevel level, UseOnContext context);
}
