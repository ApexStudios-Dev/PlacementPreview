package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RedstoneTorchBlock.class)
public interface RedStoneTorchBlockAccessor {
    @Invoker("hasNeighborSignal")
    boolean PlacementPreview$hasNeighborSignal(Level level, BlockPos pos, BlockState blockState);
}
