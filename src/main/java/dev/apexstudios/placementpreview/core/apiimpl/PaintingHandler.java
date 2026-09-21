package dev.apexstudios.placementpreview.core.apiimpl;

import com.google.common.collect.Lists;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.handler.HangingEntityHandler;
import dev.apexstudios.placementpreview.mixin.HangingEntityAccessor;
import dev.apexstudios.placementpreview.mixin.PaintingAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

final class PaintingHandler extends HangingEntityHandler<Painting> {
    private static int VARIANT_INDEX = 0;
    private static long LAST_CYCLE = -1L;

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected @Nullable PlacementResult<Painting> createEntity(Level level, BlockPos pos, Direction face, ItemStack stack) {
        var candidate = new Painting(level, pos);
        ((HangingEntityAccessor) candidate).PlacementPreview$setDirection(face);
        var accessor = (PaintingAccessor) candidate;

        var variants = Lists.newArrayList(level.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(PaintingVariantTags.PLACEABLE));
        var time = Util.getMillis();

        if(variants.isEmpty()) {
            LAST_CYCLE = time;
            VARIANT_INDEX = 0;
            return null;
        }

        variants.removeIf(variant -> {
            accessor.PlacementPreview$setVariant(variant);
            return !candidate.survives();
        });

        if(variants.isEmpty()) {
            LAST_CYCLE = time;
            VARIANT_INDEX = 0;
            return null;
        }

        var largestPossibleSize = variants.stream().mapToInt(Painting::variantArea).max().orElse(0);
        variants.removeIf(variant -> Painting.variantArea(variant) < largestPossibleSize);

        if(variants.isEmpty()) {
            LAST_CYCLE = time;
            VARIANT_INDEX = 0;
            return null;
        }

        if(variants.size() == 1) {
            LAST_CYCLE = time;
            VARIANT_INDEX = 0;
            accessor.PlacementPreview$setVariant(variants.getFirst());
            return PlacementResult.success(candidate);
        }

        if(level.tickRateManager().runsNormally() && time > LAST_CYCLE) {
            LAST_CYCLE = time + 950L;
            var index = Mth.randomBetweenInclusive(level.getRandom(), 0, variants.size() - 1);

            while (index == VARIANT_INDEX) {
                index = Mth.randomBetweenInclusive(level.getRandom(), 0, variants.size() - 1);
            }

            VARIANT_INDEX = index;
        }

        if(VARIANT_INDEX > variants.size() - 1) {
            LAST_CYCLE = time;
            VARIANT_INDEX = 0;
        }

        accessor.PlacementPreview$setVariant(variants.get(VARIANT_INDEX));
        return PlacementResult.success(candidate);
    }

    @Override
    protected Direction placeFace(UseOnContext context) {
        var face = super.placeFace(context);

        // paintings throw on non-horizontal directions
        // try to find the nearest direction facing the player
        if(!face.getAxis().isHorizontal()) {
            var ctx = new BlockPlaceContext(context);

            for(var direction : ctx.getNearestLookingDirections()) {
                if(direction.getAxis().isHorizontal()) {
                    return direction.getOpposite();
                }
            }

            return Direction.NORTH;
        }

        return face;
    }

    @Override
    protected boolean mayPlace(UseOnContext context, BlockPos pos, Direction face) {
        if(!super.mayPlace(context, pos, face)) {
            return false;
        }

        return face == context.getClickedFace();
    }
}
