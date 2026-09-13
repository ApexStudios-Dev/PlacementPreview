package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import net.minecraft.world.item.context.UseOnContext;

// TODO: Come up with a better name
@FunctionalInterface
public interface UseOnHandler {
    boolean accept(GhostLevel level, UseOnContext context);
}
