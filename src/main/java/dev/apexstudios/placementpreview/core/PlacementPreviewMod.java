package dev.apexstudios.placementpreview.core;

import dev.apexstudios.placementpreview.api.PlacementPreview;
import dev.apexstudios.placementpreview.core.apiimpl.PlacementPreviewApiImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugEntryNoop;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PlacementPreview.ID, dist = Dist.CLIENT)
public final class PlacementPreviewMod {
    private final Identifier alwaysVisible = PlacementPreview.identifier("always_visible");

    public PlacementPreviewMod(IEventBus modBus) {
        ((PlacementPreviewApiImpl) PlacementPreview.API).register(modBus);

        modBus.addListener(RegisterDebugEntriesEvent.class, event -> event.register(alwaysVisible, new DebugEntryNoop()));

        NeoForge.EVENT_BUS.addListener(ExtractLevelRenderStateEvent.class, event -> {
            var level = event.getLevel();
            var partialTick = event.getDeltaTracker().getGameTimeDeltaPartialTick(false);
            var client = Minecraft.getInstance();
            var player = client.player;

            if(player == null || player.isSpectator()) {
                return;
            }

            if(!(client.hitResult instanceof BlockHitResult hitResult)) {
                return;
            }

            if(hitResult.getType() == HitResult.Type.MISS) {
                if(!client.debugEntries.isCurrentlyEnabled(alwaysVisible)) {
                    return;
                }

                // vanillas hit result flickers between top and bottom of air blocks
                // as you move your view around, this simple hack "fixes" that
                // simply replace the direction with what ever is closest to the players view
                // if we are looking up or down
                if(hitResult.getDirection().getAxis().isVertical()) {
                    hitResult = hitResult.withDirection(player.getNearestViewDirection());
                }
            }

            // TODO: REMOVE ME BEFORE PUBLISHING!!!!!!!!!!!!!!!1
            ((PlacementPreviewApiImpl) PlacementPreviewApiImpl.API).registerAll();
            var levelRenderState = event.getRenderState();
            var mainContext = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, player.getMainHandItem(), hitResult);
            var offhandContext = new BlockPlaceContext(player, InteractionHand.OFF_HAND, player.getOffhandItem(), hitResult);

            for(var handler : PlacementPreview.API.placementHandlers()) {
                if(!handler.pre(levelRenderState, partialTick, level)) {
                    continue;
                }

                var handled = handler.accept(levelRenderState, partialTick, mainContext);

                if(!handled) {
                    handled = handler.accept(levelRenderState, partialTick, offhandContext);
                }

                if(handled) {
                    handler.post(levelRenderState, partialTick);
                    break;
                }
            }
        });

        NeoForge.EVENT_BUS.addListener(SubmitCustomGeometryEvent.class, event -> {
            var nodeCollector = event.getSubmitNodeCollector();
            var poseStack = event.getPoseStack();
            var levelRenderState = event.getLevelRenderState();

            PlacementPreview.API.placementHandlers().forEach(handler -> handler.submit(nodeCollector, poseStack, levelRenderState));
        });
    }
}
