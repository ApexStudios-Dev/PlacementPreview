package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpeleothemBlock.class)
public interface SpeleothemBlockAccessor {
    @Invoker("calculateTipDirection")
    @Nullable Direction PlacementPreview$calculateTipDirection(LevelReader level, BlockPos pos, Direction defaultTipDirection);

    @Invoker("calculateSpeleothemThickness")
    SpeleothemThickness PlacementPreview$calculateSpeleothemThickness(LevelReader level, BlockPos pos, Direction tipDirection, boolean mergeOpposingTips);
}
