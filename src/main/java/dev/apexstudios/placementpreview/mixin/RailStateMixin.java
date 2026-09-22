package dev.apexstudios.placementpreview.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.apexstudios.placementpreview.extensions.RailStateExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RailState.class)
public abstract class RailStateMixin implements RailStateExtension {
    @Unique
    private boolean PlacementPreview$allowsSetBlock = true;

    @WrapOperation(
            method = {
                    "Lnet/minecraft/world/level/block/RailState;connectTo(Lnet/minecraft/world/level/block/RailState;)V",
                    "Lnet/minecraft/world/level/block/RailState;place(ZZLnet/minecraft/world/level/block/state/properties/RailShape;)Lnet/minecraft/world/level/block/RailState;"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            )
    )
    private boolean PlacementPreview$setBlock(Level level, BlockPos pos, BlockState blockState, @Block.UpdateFlags int updateFlags, Operation<Boolean> original) {
        return PlacementPreview$allowsSetBlock && original.call(level, pos, blockState, updateFlags);
    }

    @ModifyReturnValue(
            method = "Lnet/minecraft/world/level/block/RailState;getRail(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/RailState;",
            at = @At(
                    value = "RETURN",
                    target = "Lnet/minecraft/world/level/block/RailState;getRail(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/RailState;"
            )
    )
    private @Nullable RailState PlacementPreview$copyAllowsSetBlock(@Nullable RailState railState) {
        if(railState != null) {
            ((RailStateExtension) railState).PlacementPreview$allowsSetBlock(PlacementPreview$allowsSetBlock);
        }

        return railState;
    }

    @Override
    public void PlacementPreview$allowsSetBlock(boolean allowsSetBlock) {
        PlacementPreview$allowsSetBlock = allowsSetBlock;
    }
}
