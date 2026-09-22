package dev.apexstudios.placementpreview.core;

import dev.apexstudios.ghostrenderer.api.GhostRenderer;
import dev.apexstudios.placementpreview.api.PlacementPreview;
import dev.apexstudios.placementpreview.core.apiimpl.PlacementPreviewApiImpl;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@Mod(value = PlacementPreview.ID, dist = Dist.CLIENT)
public final class PlacementPreviewMod {
    public PlacementPreviewMod(IEventBus modBus) {
        ((PlacementPreviewApiImpl) PlacementPreview.API).register(modBus);

        var validPlacement = new AtomicBoolean(true);

        modBus.addListener(RegisterGuiLayersEvent.class, event -> event.registerAboveAll(PlacementPreview.identifier("gui"), (graphics, delta) -> {
            if(validPlacement.get()) {
                return;
            }

            var x = (graphics.guiWidth() - 16) / 2 + 10;
            var y = (graphics.guiHeight() - 16) / 2 + 10;

            // TODO: Swap out crosshair fully
            graphics.fakeItem(Items.BARRIER.getDefaultInstance(), x, y);
            // graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("player_list/remove_player"), x, y, 16, 16);
            // graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("world_list/error_highlighted"), x, y, 16, 16);
        }));

        modBus.addListener(EventPriority.HIGH, RegisterRenderStateModifiersEvent.class, event -> {
            event.registerEntityModifier(AbstractBoatRenderer.class, (boat, renderState) -> {
                if(GhostRenderer.isGhostRender(renderState)) {
                    renderState.isUnderWater = true;
                }
            });
        });

        GhostRenderer.registerEvents(PlacementPreview.ID, (level, player, hitResult) -> {
            // TODO: REMOVE ME BEFORE PUBLISHING!!!!!!!!!!!!!!!1
            ((PlacementPreviewApiImpl) PlacementPreviewApiImpl.API).registerAll();
            var ghosted = level.ghosted();
            validPlacement.set(true);

            for(var hand : InteractionHand.values()) {
                var stack = player.getItemInHand(hand);
                var handler = PlacementPreview.API.getUseOnHandler(stack);

                if(handler == null) {
                    continue;
                }

                var result = handler.accept(level, new UseOnContext(ghosted, player, hand, stack, hitResult));

                if(result != null) {
                    if(result.isFailure()) {
                        validPlacement.set(false);
                    }

                    return true;
                }
            }

            return false;
        }, PlacementPreview.GHOST_PROPERTIES);
    }
}
