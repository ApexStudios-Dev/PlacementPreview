package dev.apexstudios.placementpreview.core.apiimpl;

import dev.apexstudios.ghostrenderer.api.GhostHelper;
import dev.apexstudios.ghostrenderer.api.GhostLevel;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.handler.UseOnHandler;
import java.util.Objects;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

final class EndCrystalHandler implements UseOnHandler {
    @Override
    public PlacementResult<EndCrystal> accept(GhostLevel ghosts, UseOnContext context) {
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
    }
}
