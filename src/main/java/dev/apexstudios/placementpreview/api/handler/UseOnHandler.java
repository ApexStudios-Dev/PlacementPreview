package dev.apexstudios.placementpreview.api.handler;

import com.google.common.collect.Lists;
import dev.apexstudios.ghostrenderer.api.GhostHelper;
import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.mixin.HangingEntityAccessor;
import dev.apexstudios.placementpreview.mixin.PaintingAccessor;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.CushionItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

// TODO: Come up with a better name
@SuppressWarnings("DataFlowIssue")
@FunctionalInterface
public interface UseOnHandler {
    UseOnHandler BLOCK_ITEM = new BlockItemHandler() {};
    UseOnHandler STANDING_WALL = new StandingAndWallBlockItemHandler() {};
    UseOnHandler PLACE_ON_WATER = new PlaceOnWaterHandler() {};
    UseOnHandler SPAWN_EGG = new SpawnEggHandler() {};
    UseOnHandler CUSHION = (ghosts, context) -> {
        var stack = context.getItemInHand();

        if(!(stack.getItem() instanceof CushionItem)) {
            return null;
        }

        var level = context.getLevel();
        var recalculated = CushionItem.recalculateContextForSpecialCollisionShapes(context);
        var placeContext = new BlockPlaceContext(recalculated);
        var pos = placeContext.getClickedPos();

        var entity = GhostHelper.createEntity(
                EntityTypes.CUSHION,
                level,
                EntityType.createDefaultStackConfig(level, stack, context.getPlayer()),
                pos,
                EntitySpawnReason.SPAWN_ITEM_USE,
                true,
                true
        );

        if(entity == null) {
            return null;
        }

        var valid = true;
        var entityPos = Vec3.atCenterOfWithY(pos, recalculated.getClickLocation().y());
        GhostHelper.snapRotation(entity, Direction.fromYRot(placeContext.getRotation()).toYRot(), 0F);

        if(recalculated.getClickedFace() != Direction.UP) {
            valid = false;
        } else {
            var spawnAABB = EntityTypes.CUSHION.getSpawnAABB(entityPos);

            if(!Cushion.canBePlacedAt(level, spawnAABB)) {
                valid = false;
            } else if(!level.getEntitiesOfClass(Cushion.class, spawnAABB).isEmpty()) {
                valid = false;
            } else if(level.findBlocksIn(entity.getBoundingBox().nextDeflated()).filterState(blockState -> blockState.is(BlockTags.FIRE)).anyMatched()) {
                valid = false;
            }
        }

        ghosts.addEntity(entity, valid);
        return PlacementResult.of(entity, valid);
    };
    UseOnHandler MINECART = (ghosts, context) -> {
        var stack = context.getItemInHand();

        if(!(stack.getItem() instanceof MinecartItem item)) {
            return null;
        }

        var level = context.getLevel();
        var pos = context.getClickedPos();
        var blockState = level.getBlockState(pos);
        var valid = BaseRailBlock.isRail(blockState);
        var shape = valid ? ((BaseRailBlock) blockState.getBlock()).getRailDirection(blockState, level, pos, null) : RailShape.NORTH_SOUTH;
        var offset = shape.isSlope() ? .5D : 0D;
        var minecart = AbstractMinecart.createMinecart(
                level,
                pos.getX() + .5D,
                pos.getY() + .0625D + offset,
                pos.getZ() + .5D,
                item.type,
                EntitySpawnReason.DISPENSER,
                stack,
                context.getPlayer()
        );

        if(minecart == null) {
            return null;
        }


        if(AbstractMinecart.useExperimentalMovement(level)) {
            var box = minecart.getBoundingBox();

            for(var entity : level.getEntities(null, box)) {
                if(entity instanceof AbstractMinecart) {
                    valid = false;
                    break;
                }
            }
        }

        GhostHelper.snapRotation(minecart);
        ghosts.addEntity(minecart, valid);
        return PlacementResult.of(minecart, valid);
    };
    UseOnHandler END_CRYSTAL = (ghosts, context) -> {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var blockState = level.getBlockState(pos);
        var valid = blockState.is(Blocks.OBSIDIAN) || blockState.is(Blocks.BEDROCK);
        var abovePos = pos.above();

        if(valid) {
            valid = level.isEmptyBlock(abovePos);
        }

        var x = abovePos.getX();
        var y = abovePos.getY();
        var z = abovePos.getZ();

        if(valid) {
            valid = level.getEntities(null, new AABB(x, y, z, x + 1D, y + 1D, z + 1D)).isEmpty();
        }

        var entity = new EndCrystal(level, x + .5D, y, z + .5D);
        entity.time = Objects.requireNonNull(context.getPlayer()).tickCount;
        GhostHelper.snapRotation(entity, x + .5D, y, z + .5D, 0F, 0F);
        entity.setShowBottom(false);
        ghosts.addEntity(entity, valid);
        return PlacementResult.of(entity, valid);
    };
    UseOnHandler ARMOR_STAND = (ghosts, context) -> {
        var level = context.getLevel();
        var placeContext = new BlockPlaceContext(context);
        var blockPos = placeContext.getClickedPos();

        var entity = GhostHelper.createEntity(
                EntityTypes.ARMOR_STAND,
                level,
                EntityType.createDefaultStackConfig(level, context.getItemInHand(), context.getPlayer()),
                blockPos,
                EntitySpawnReason.SPAWN_ITEM_USE,
                true,
                true
        );

        if(entity == null) {
            return null;
        }

        GhostHelper.snapRotation(entity, Mth.floor((Mth.wrapDegrees(context.getRotation() - 180F) + 22.5F) / 45F) * 45F, 0F);
        var box = EntityTypes.ARMOR_STAND.getDimensions().makeBoundingBox(Vec3.atBottomCenterOf(blockPos));
        var noCollision = level.noCollision(null, box) && level.getEntities(null, box).isEmpty();
        ghosts.addEntity(entity, noCollision);
        return PlacementResult.of(entity, noCollision);
    };
    UseOnHandler ITEM_FRAME = new HangingEntityHandler() {
        @Override
        protected @Nullable PlacementResult<ItemFrame> createEntity(Level level, BlockPos pos, Direction face, ItemStack stack) {
            return PlacementResult.success(stack.is(Items.ITEM_FRAME) ? new ItemFrame(level, pos, face) : new GlowItemFrame(level, pos, face));
        }
    };
    UseOnHandler PAINTING = new HangingEntityHandler() {
        int variantIndex = 0;
        long lastCycle = -1L;

        @Override
        protected @Nullable PlacementResult<? extends HangingEntity> createEntity(Level level, BlockPos pos, Direction face, ItemStack stack) {
            var candidate = new Painting(level, pos);
            ((HangingEntityAccessor) candidate).PlacementPreview$setDirection(face);
            var accessor = (PaintingAccessor) candidate;

            var variants = Lists.newArrayList(level.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(PaintingVariantTags.PLACEABLE));
            var time = Util.getMillis();

            if(variants.isEmpty()) {
                lastCycle = time;
                variantIndex = 0;
                return null;
            }

            variants.removeIf(variant -> {
                accessor.PlacementPreview$setVariant(variant);
                return !candidate.survives();
            });

            if(variants.isEmpty()) {
                lastCycle = time;
                variantIndex = 0;
                return null;
            }

            var largestPossibleSize = variants.stream().mapToInt(Painting::variantArea).max().orElse(0);
            variants.removeIf(variant -> Painting.variantArea(variant) < largestPossibleSize);

            if(variants.isEmpty()) {
                lastCycle = time;
                variantIndex = 0;
                return null;
            }

            if(variants.size() == 1) {
                lastCycle = time;
                variantIndex = 0;
                accessor.PlacementPreview$setVariant(variants.getFirst());
                return PlacementResult.success(candidate);
            }

            if(level.tickRateManager().runsNormally() && time > lastCycle) {
                lastCycle = time + 950L;
                var index = Mth.randomBetweenInclusive(level.getRandom(), 0, variants.size() - 1);

                while (index == variantIndex) {
                    index = Mth.randomBetweenInclusive(level.getRandom(), 0, variants.size() - 1);
                }

                variantIndex = index;
            }

            if(variantIndex > variants.size() - 1) {
                lastCycle = time;
                variantIndex = 0;
            }

            accessor.PlacementPreview$setVariant(variants.get(variantIndex));
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
    };

    @Nullable PlacementResult<?> accept(GhostLevel ghosts, UseOnContext context);
}
