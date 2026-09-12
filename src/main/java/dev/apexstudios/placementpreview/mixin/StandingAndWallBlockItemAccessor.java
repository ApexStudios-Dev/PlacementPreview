package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StandingAndWallBlockItem.class)
public interface StandingAndWallBlockItemAccessor {
    @Invoker("canPlace")
    boolean PlacementPreview$canPlace(LevelReader level, BlockState blockState, BlockPos pos);
}
