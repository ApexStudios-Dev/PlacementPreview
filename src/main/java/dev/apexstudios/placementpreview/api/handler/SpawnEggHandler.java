package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Spawner;
import org.jspecify.annotations.Nullable;

public interface SpawnEggHandler extends UseOnHandler {
    @Override
    default @Nullable PlacementResult<?> accept(GhostLevel level, UseOnContext context) {
        var stack = context.getItemInHand();

        if(!(stack.getItem() instanceof SpawnEggItem)) {
            return null;
        }

        var entityType = SpawnEggItem.getType(stack);

        if(entityType == null) {
            return null;
        }

        var result = updateSpawner(level, context, entityType);

        if(result == null) {
            result = spawnEntity(level, context, entityType);
        }

        return result;
    }

    default @Nullable PlacementResult<?> updateSpawner(GhostLevel level, UseOnContext context, EntityType<?> entityType) {
        var reality = level.reality();
        var pos = context.getClickedPos();

        if(!(reality.getBlockEntity(pos) instanceof Spawner)) {
            return null;
        }

        // serverLevel.isSpawnerBlockEnabled(); | uses game rules which are server only
        var blockState = reality.getBlockState(pos);
        level.setBlockState(pos, blockState, true);
        level.setBlockEntity(pos, blockState, context.getItemInHand(), true);
        Objects.requireNonNull((Spawner) level.getBlockEntity(pos)).setEntityId(entityType, reality.getRandom());
        return PlacementResult.success(entityType);
    }

    default @Nullable <TEntity extends Entity> PlacementResult<?> spawnEntity(GhostLevel level, UseOnContext context, EntityType<TEntity> entityType) {
        var reality = level.reality();
        var clickedFace = context.getClickedFace();
        var pos = context.getClickedPos();
        var spawnPos = pos;

        if(!reality.getBlockState(pos).getCollisionShape(reality, pos).isEmpty()) {
            spawnPos = pos.relative(clickedFace);
        }

        var entity = level.createEntity(
                entityType,
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

        entity.setYRot(Direction.fromYRot(context.getRotation()).getOpposite().toYRot());
        entity.setYBodyRot(entity.getYRot());
        entity.setOldRot();

        if(entity instanceof Mob mob) {
            mob.yHeadRot = mob.getYRot();
            mob.yHeadRotO = mob.yRotO;
            mob.yBodyRotO = mob.yRotO;
        }

        level.addEntity(entity, true);
        return PlacementResult.success(entity);
    }
}
