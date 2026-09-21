package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostHelper;
import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Spawner;
import org.jspecify.annotations.Nullable;

public interface SpawnEggHandler extends UseOnHandler {
    @Override
    default @Nullable PlacementResult<?> accept(GhostLevel ghosts, UseOnContext context) {
        var stack = context.getItemInHand();

        if(!(stack.getItem() instanceof SpawnEggItem)) {
            return null;
        }

        var entityType = SpawnEggItem.getType(stack);

        if(entityType == null) {
            return null;
        }

        PlacementResult<?> result = updateSpawner(ghosts, context, entityType);

        if(result == null) {
            result = spawnEntity(ghosts, context, entityType);
        }

        return result;
    }

    default @Nullable PlacementResult<EntityType<?>> updateSpawner(GhostLevel ghosts, UseOnContext context, EntityType<?> entityType) {
        var level = context.getLevel();
        var pos = context.getClickedPos();

        if(!(level.getBlockEntity(pos) instanceof Spawner)) {
            return null;
        }

        // serverLevel.isSpawnerBlockEnabled(); | uses game rules which are server only
        var blockState = level.getBlockState(pos);
        ghosts.setBlockState(pos, blockState, true);
        ghosts.setBlockEntity(pos, blockState, context.getItemInHand(), true);
        Objects.requireNonNull((Spawner) level.getBlockEntity(pos)).setEntityId(entityType, level.getRandom());
        return PlacementResult.success(entityType);
    }

    default @Nullable <TEntity extends Entity> PlacementResult<TEntity> spawnEntity(GhostLevel ghosts, UseOnContext context, EntityType<TEntity> entityType) {
        var level = context.getLevel();
        var clickedFace = context.getClickedFace();
        var pos = context.getClickedPos();
        var spawnPos = pos;

        if(!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
            spawnPos = pos.relative(clickedFace);
        }

        var entity = GhostHelper.createEntity(
                entityType,
                level,
                context.getItemInHand(),
                context.getPlayer(),
                context.getClickedPos(),
                EntitySpawnReason.SPAWN_ITEM_USE,
                true,
                !Objects.equals(pos, spawnPos) && clickedFace == Direction.UP
        );

        if(entity == null) {
            return null;
        }

        GhostHelper.snapRotation(entity, Direction.fromYRot(context.getRotation()).getOpposite().toYRot(), 0F);
        ghosts.addEntity(entity, true);
        return PlacementResult.success(entity);
    }
}
