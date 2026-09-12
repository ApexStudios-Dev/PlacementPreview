package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PoweredRailBlock.class)
public interface PoweredRailBlockAccessor {
    @Invoker("findPoweredRailSignal")
    boolean PlacementPreview$findPoweredRailSignal(Level level, BlockPos pos, BlockState blockState, boolean forward, int searchDepth);
}
