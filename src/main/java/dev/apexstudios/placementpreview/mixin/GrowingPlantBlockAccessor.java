package dev.apexstudios.placementpreview.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GrowingPlantBlock.class)
public interface GrowingPlantBlockAccessor {
    @Invoker("getHeadBlock")
    GrowingPlantHeadBlock PlacementPreview$getHeadBlock();

    @Invoker("getBodyBlock")
    Block PlacementPreview$getBodyBlock();
}
