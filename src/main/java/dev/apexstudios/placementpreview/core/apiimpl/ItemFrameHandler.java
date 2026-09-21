package dev.apexstudios.placementpreview.core.apiimpl;

import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.handler.HangingEntityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

final class ItemFrameHandler extends HangingEntityHandler<ItemFrame> {
    @Override
    protected PlacementResult<ItemFrame> createEntity(Level level, BlockPos pos, Direction face, ItemStack stack) {
        return PlacementResult.success(stack.is(Items.ITEM_FRAME) ? new ItemFrame(level, pos, face) : new GlowItemFrame(level, pos, face));
    }
}
