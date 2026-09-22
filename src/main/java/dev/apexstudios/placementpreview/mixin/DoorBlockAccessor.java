package dev.apexstudios.placementpreview.mixin;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DoorBlock.class)
public interface DoorBlockAccessor {
    @Invoker("getHinge")
    DoorHingeSide PlacementPreview$getHinge(BlockPlaceContext context);
}
