package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DiodeBlock.class)
public interface DiodeBlockAccessor {
    @Invoker("shouldTurnOn")
    boolean PlacementPreview$shouldTurnOn(Level level, BlockPos pos, BlockState blockState);
}
