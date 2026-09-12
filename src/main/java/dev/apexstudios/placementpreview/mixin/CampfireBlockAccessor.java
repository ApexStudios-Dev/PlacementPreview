package dev.apexstudios.placementpreview.mixin;

import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CampfireBlock.class)
public interface CampfireBlockAccessor {
    @Invoker("isSmokeSource")
    boolean PlacementPreview$isSmokeSource(BlockState blockState);
}
