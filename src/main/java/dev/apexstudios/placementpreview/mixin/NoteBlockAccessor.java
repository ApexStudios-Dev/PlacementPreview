package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NoteBlock.class)
public interface NoteBlockAccessor {
    @Invoker("setInstrument")
    BlockState PlacementPreview$setInstrument(LevelReader level, BlockPos position, BlockState blockState);
}
