package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostHelper;
import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jspecify.annotations.Nullable;

// TODO: Come up with a better name
@SuppressWarnings("DataFlowIssue")
@FunctionalInterface
public interface UseOnHandler {
    UseOnHandler BLOCK_ITEM = new BlockItemHandler() {};
    UseOnHandler STANDING_WALL = new StandingAndWallBlockItemHandler() {};
    UseOnHandler PLACE_ON_WATER = new PlaceOnWaterHandler() {};
    UseOnHandler SPAWN_EGG = new SpawnEggHandler() {};
    UseOnHandler CUSHION = new CushionHandler() {};
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

    @Nullable PlacementResult<?> accept(GhostLevel ghosts, UseOnContext context);
}
