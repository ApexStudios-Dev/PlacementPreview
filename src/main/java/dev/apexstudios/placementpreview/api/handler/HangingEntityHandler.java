package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.ghostrenderer.api.GhostHelper;
import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.mixin.HangingEntityItemAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class HangingEntityHandler<TEntity extends HangingEntity> implements UseOnHandler {
    @Override
    public @Nullable PlacementResult<TEntity> accept(GhostLevel ghosts, UseOnContext context) {
        var stack = context.getItemInHand();

        if(!(stack.getItem() instanceof HangingEntityItem)) {
            return null;
        }

        var level = context.getLevel();
        var face = placeFace(context);
        var pos = placePos(context, face);
        var player = context.getPlayer();
        var result = createEntity(level, pos, face, stack);

        if(result == null) {
            return null;
        }

        if(result.isSuccess() && !mayPlace(context, pos, face)) {
            result = result.asFailure();
        }

        var entity = result.value();
        GhostHelper.setupEntity(entity);
        GhostHelper.snapRotation(entity, pos);
        EntityType.createDefaultStackConfig(level, stack, player).apply(entity);

        if(result.isSuccess() && !entity.survives()) {
            result = result.asFailure();
        }

        entity.setPos(new Vec3(pos));
        entity.setOldPosAndRot();
        ghosts.addEntity(entity, result.isSuccess());
        return result;
    }

    protected Direction placeFace(UseOnContext context) {
        return context.getClickedFace();
    }

    protected BlockPos placePos(UseOnContext context, Direction face) {
        return context.getClickedPos().relative(face);
    }

    protected boolean mayPlace(UseOnContext context, BlockPos pos, Direction face) {
        var player = context.getPlayer();
        var stack = context.getItemInHand();
        return player != null && ((HangingEntityItemAccessor) stack.getItem()).PlacementPreview$mayPlace(player, face, stack, pos);
    }

    protected abstract @Nullable PlacementResult<TEntity> createEntity(Level level, BlockPos pos, Direction face, ItemStack stack);
}
