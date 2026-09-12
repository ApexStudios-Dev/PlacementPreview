package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ScaffoldingBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ScaffoldingBlock.class)
public interface ScaffoldingBlockAccessor {
    @Invoker("isBottom")
    boolean PlacementPreview$isBottom(BlockGetter level, BlockPos pos, int distance);
}
