package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WallBlock.class)
public interface WallBlockAccessor {
    @Invoker("connectsTo")
    boolean PlacementPreview$connectsTo(BlockState blockState, boolean faceSolid, Direction direction);

    @Invoker("updateShape")
    BlockState PlacementPreview$updateShape(LevelReader level, BlockState blockState, BlockPos topPos, BlockState topNeighbour, boolean north, boolean east, boolean south, boolean west);
}
