package dev.apexstudios.placementpreview.core;

import dev.apexstudios.ghostrenderer.api.GhostRenderer;
import dev.apexstudios.placementpreview.api.PlacementPreview;
import dev.apexstudios.placementpreview.core.apiimpl.PlacementPreviewApiImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = PlacementPreview.ID, dist = Dist.CLIENT)
public final class PlacementPreviewMod {
    public PlacementPreviewMod(IEventBus modBus) {
        ((PlacementPreviewApiImpl) PlacementPreview.API).register(modBus);

        GhostRenderer.registerEvents(PlacementPreview.ID, (level, player, hitResult) -> {
            // TODO: REMOVE ME BEFORE PUBLISHING!!!!!!!!!!!!!!!1
            ((PlacementPreviewApiImpl) PlacementPreviewApiImpl.API).registerAll();
            var ghosted = level.ghosted();

            for(var hand : InteractionHand.values()) {
                var stack = player.getItemInHand(hand);
                var item = stack.getItem();
                var handler = PlacementPreview.API.getUseOnHandler(item);

                if(handler != null && handler.accept(level, new UseOnContext(ghosted, player, hand, stack, hitResult))) {
                    return true;
                }
            }

            return false;
        }, PlacementPreview.GHOST_PROPERTIES);
    }
}
