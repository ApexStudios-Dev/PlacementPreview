package dev.apexstudios.placementpreview.core.apiimpl;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.apexstudios.ghostrenderer.api.BlockEntityRendererCache;
import dev.apexstudios.ghostrenderer.api.GhostBlockAndTintGetter;
import dev.apexstudios.ghostrenderer.api.GhostRenderer;
import dev.apexstudios.placementpreview.api.PlacementPreview;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.handler.PlacementHandler;
import dev.apexstudios.placementpreview.api.validator.PlacementValidators;
import java.util.Objects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

final class PlaceBlockPlacementHandler implements PlacementHandler {
    private final ContextKey<GhostBlockAndTintGetter> renderStateKey = new ContextKey<>(PlacementPreview.identifier("render_state/place_block"));
    private final BlockEntityRendererCache blockEntityRendererCache = new BlockEntityRendererCache();
    private GhostBlockAndTintGetter.@Nullable Mutable ghosts = null;

    @Override
    public boolean accept(LevelRenderState levelRenderState, float partialTick, BlockPlaceContext context) {
        if(ghosts == null) {
            return false;
        }

        var contextResult = updateContext(context);
        var updatedContext = contextResult.value();

        var updatedItem = updatedContext.getItemInHand();
        var blockStateResult = PlacementPreviewApiImpl.API.item2BlockSuppliers().get(updatedItem).apply(updatedContext, updatedItem.getItem());
        var blockState = blockStateResult.value();

        var isPlaceable = contextResult.isSuccess() && blockStateResult.isSuccess();

        if(isPlaceable) {
            isPlaceable = PlacementValidators.PLACEABLE.test(updatedContext, blockState);
        }

        setBlock(updatedContext.getLevel(), updatedContext.getClickedPos(), blockState, updatedItem, isPlaceable);
        return !ghosts.isEmpty();
    }

    @Override
    public boolean pre(LevelRenderState levelRenderState, float partialTick, ClientLevel level) {
        blockEntityRendererCache.newFrame();
        ghosts = GhostBlockAndTintGetter.create(level);
        return true;
    }

    @Override
    public void post(LevelRenderState levelRenderState, float partialTick) {
        if(ghosts == null) {
            return;
        }

        blockEntityRendererCache.extract(levelRenderState, partialTick);
        levelRenderState.setRenderData(renderStateKey, ghosts.immutable());
        ghosts = null;
    }

    @Override
    public void submit(SubmitNodeCollector nodeCollector, PoseStack poseStack, LevelRenderState levelRenderState) {
        blockEntityRendererCache.submit(nodeCollector, poseStack, levelRenderState);
        GhostRenderer.submit(nodeCollector, poseStack, levelRenderState, renderStateKey);
    }

    private PlacementResult<BlockPlaceContext> updateContext(BlockPlaceContext context) {
        if(!(context.getItemInHand().getItem() instanceof BlockItem item)) {
            return PlacementResult.success(context);
        }

        var updatedContext = item.updatePlacementContext(context);

        if(updatedContext == null) {
            return PlacementResult.failure(context);
        }

        return PlacementResult.success(updatedContext);
    }

    private void setBlock(Level level, BlockPos pos, BlockState blockState, ItemStack stack, boolean isPlaceable) {
        Objects.requireNonNull(ghosts);

        ghosts.setBlockState(pos, new GhostBlockAndTintGetter.GhostBlock(
                blockState,
                blockState.getSeed(pos),
                isPlaceable
        ));

        blockEntityRendererCache.setBlockEntity(
                pos,
                blockState,
                level,
                stack,
                isPlaceable
        );
    }

    private boolean withinPlacementRange(BlockPlaceContext context) {
        var entity = context.getPlayer();

        if(entity == null) {
            return true;
        }

        var blockInteractionRange = entity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        var entityInteractionRange = entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);

        var interactionRange = Math.max(blockInteractionRange, entityInteractionRange);
        var interactionRangeSq = Mth.square(interactionRange);

        return new AABB(context.getClickedPos()).distanceToSqr(entity.getEyePosition()) < interactionRangeSq;
    }
}
