package dev.apexstudios.placementpreview.api.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.item.context.BlockPlaceContext;

@FunctionalInterface
public interface PlacementHandler {
    boolean accept(LevelRenderState levelRenderState, float partialTick, BlockPlaceContext context);

    default boolean pre(LevelRenderState levelRenderState, float partialTick, ClientLevel level) {
        return true;
    }

    default void post(LevelRenderState levelRenderState, float partialTick) {

    }

    default void submit(SubmitNodeCollector nodeCollector, PoseStack poseStack, LevelRenderState levelRenderState) {

    }
}
