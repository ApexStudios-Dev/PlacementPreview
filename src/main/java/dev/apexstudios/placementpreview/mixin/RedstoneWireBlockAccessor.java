package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedstoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RedstoneWireBlock.class)
public interface RedstoneWireBlockAccessor {
    @Invoker("getConnectionState")
    BlockState PlacementPreview$getConnectionState(BlockGetter level, BlockState blockState, BlockPos pos);
}
