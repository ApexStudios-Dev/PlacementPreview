package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import net.minecraft.world.item.context.BlockPlaceContext;

@FunctionalInterface
public interface PlacementHandler {
    boolean accept(GhostLevel level, BlockPlaceContext context);
}
