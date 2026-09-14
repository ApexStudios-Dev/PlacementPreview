package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostLevel;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Spawner;
import org.jspecify.annotations.Nullable;

public interface SpawnEggHandler extends UseOnHandler {
    @Override
    default boolean accept(GhostLevel level, UseOnContext context) {
        var stack = context.getItemInHand();

        if(!(stack.getItem() instanceof SpawnEggItem)) {
            return false;
        }

        var entityType = SpawnEggItem.getType(stack);

        if(entityType == null) {
            return false;
        }

        if(updateSpawner(level, context, entityType)) {
            return true;
        }

        return spawnEntity(level, context, entityType) != null;
    }

    default boolean updateSpawner(GhostLevel level, UseOnContext context, EntityType<?> entityType) {
        var reality = level.reality();
        var pos = context.getClickedPos();

        if(!(reality.getBlockEntity(pos) instanceof Spawner)) {
            return false;
        }

        // serverLevel.isSpawnerBlockEnabled(); | uses game rules which are server only
        var blockState = reality.getBlockState(pos);
        level.setBlockState(pos, blockState, true);
        level.setBlockEntity(pos, blockState, context.getItemInHand(), true);
        Objects.requireNonNull((Spawner) level.getBlockEntity(pos)).setEntityId(entityType, reality.getRandom());
        return true;
    }

    default <TEntity extends Entity> @Nullable TEntity spawnEntity(GhostLevel level, UseOnContext context, EntityType<TEntity> entityType) {
        var reality = level.reality();
        var pos = context.getClickedPos();

        // we want to ignore as many checks that short circuit and return null
        var entity = entityType.create(reality, new EntitySpawnRequest(EntitySpawnReason.SPAWN_ITEM_USE, true));

        if(entity == null) {
            return null;
        }

        level.fixClientEntity(entity);
        var clickedFace = context.getClickedFace();
        var spawnPos = pos;

        if(!reality.getBlockState(pos).getCollisionShape(reality, pos).isEmpty()) {
            spawnPos = spawnPos.relative(clickedFace);
        }

        var movedUp = !Objects.equals(pos, spawnPos) && clickedFace == Direction.UP;
        // SpawnEggItem always passes true as the `tryMoveDown` param to `spawn`
        entity.setPos(spawnPos.getX() + .5D, spawnPos.getY() + 1D, spawnPos.getZ() + .5D);
        var yOff = EntityType.getYOffset(reality, spawnPos, movedUp, entity.getBoundingBox());

        entity.snapTo(
                spawnPos.getX() + .5D,
                spawnPos.getY() + yOff,
                spawnPos.getZ() + .5D,
                Mth.wrapDegrees(context.getRotation() + 180F),
                0F
        );

        var canSpawn = entityType.canSpawn(reality);

        if(entity instanceof Mob mob) {
            mob.yHeadRot = mob.getYRot();
            mob.yHeadRotO = mob.yRotO;
            mob.yBodyRot = mob.getYRot();
            mob.yBodyRotO = mob.yRotO;
            // TODO: If we ever get a FakeServerLevel invoke this method to apply random armor and the like to mobs
            // mob.finalizeSpawn(reality, reality.getCurrentDifficultyAt(mob.blockPosition()), EntitySpawnReason.SPAWN_ITEM_USE, null);

            if(mob.isSpawnCancelled()) {
                canSpawn = false;
            }
        }

        EntityType.createDefaultStackConfig(reality, context.getItemInHand(), context.getPlayer()).apply(entity);
        level.addEntity(entity, canSpawn);
        return entity;
    }
}
