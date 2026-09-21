package dev.apexstudios.placementpreview.core.apiimpl;

import dev.apexstudios.ghostrenderer.api.GhostHelper;
import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.handler.UseOnHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

final class ArmorStandHandler implements UseOnHandler {
    @Override
    public @Nullable PlacementResult<ArmorStand> accept(GhostLevel ghosts, UseOnContext context) {
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
    }
}
