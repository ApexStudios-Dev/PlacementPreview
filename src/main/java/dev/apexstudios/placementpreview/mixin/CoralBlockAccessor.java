package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CoralBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CoralBlock.class)
public interface CoralBlockAccessor {
    @Invoker("scanForWater")
    boolean PlacementPreview$scanForWater(BlockGetter level, BlockPos blockPos);
}
