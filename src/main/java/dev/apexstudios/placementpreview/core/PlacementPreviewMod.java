package dev.apexstudios.placementpreview.core;

import dev.apexstudios.ghostrenderer.api.GhostRenderer;
import dev.apexstudios.placementpreview.api.PlacementPreview;
import dev.apexstudios.placementpreview.core.apiimpl.PlacementPreviewApiImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
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
            var reality = level.reality();
            var processed = false;

            for(var hand : InteractionHand.values()) {
                if(processed) {
                    break;
                }

                var useOnContext = new UseOnContext(reality, player, hand, player.getItemInHand(hand), hitResult);

                for(var handler : PlacementPreview.API.useOnHandlers()) {
                    if(handler.accept(level, useOnContext)) {
                        processed = true;
                        break;
                    }
                }

                if(processed) {
                    break;
                }

                var placeContext = new BlockPlaceContext(useOnContext);

                for(var handler : PlacementPreview.API.placementHandlers()) {
                    if(handler.accept(level, placeContext)) {
                        processed = true;
                        break;
                    }
                }
            }

            return processed;
        });
    }
}
