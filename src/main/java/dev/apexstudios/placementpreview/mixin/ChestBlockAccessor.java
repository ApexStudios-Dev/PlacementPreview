package dev.apexstudios.placementpreview.mixin;

import dev.apexstudios.placementpreview.api.PlacementPreview;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChestBlock.class)
public interface ChestBlockAccessor {
    @Invoker("candidatePartnerFacing")
    @Nullable Direction PlacementPreview$candidatePartnerFacing(Level level, BlockPos pos, Direction neighbourDirection);

    @Invoker("getChestType")
    ChestType PlacementPreview$getChestType(Level level, BlockPos pos, Direction facingDirection);
}
