package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.item.CushionItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface CushionHandler extends UseOnHandler {
    @Override
    default @Nullable PlacementResult<?> accept(GhostLevel ghosts, UseOnContext context) {
        var stack = context.getItemInHand();

        if(!(stack.getItem() instanceof CushionItem)) {
            return null;
        }

        var level = context.getLevel();
        var recalculated = CushionItem.recalculateContextForSpecialCollisionShapes(context);
        var placeContext = new BlockPlaceContext(recalculated);
        var pos = placeContext.getClickedPos();

        var entity = ghosts.createEntity(
                EntityTypes.CUSHION,
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
        entity.snapTo(entityPos, Direction.fromYRot(placeContext.getRotation()).toYRot(), 0F);

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
    }
}
