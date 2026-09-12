package dev.apexstudios.placementpreview.mixin;

import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FenceGateBlock.class)
public interface FenceGateBlockAccessor {
    @Invoker("isWall")
    boolean PlacementPreview$isWall(BlockState blockState);
}
