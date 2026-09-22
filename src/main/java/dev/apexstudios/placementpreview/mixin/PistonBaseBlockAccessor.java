package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PistonBaseBlock.class)
public interface PistonBaseBlockAccessor {
    @Invoker("getNeighborSignal")
    boolean PlacementPreview$getNeighborSignal(SignalGetter level, BlockPos pos, Direction pushDirection);
}
