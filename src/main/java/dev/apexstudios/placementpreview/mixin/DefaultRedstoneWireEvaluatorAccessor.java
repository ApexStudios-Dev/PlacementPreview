package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.DefaultRedstoneWireEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DefaultRedstoneWireEvaluator.class)
public interface DefaultRedstoneWireEvaluatorAccessor {
    @Invoker("calculateTargetStrength")
    int PlacementPreview$calculateTargetStrength(Level level, BlockPos pos);
}
