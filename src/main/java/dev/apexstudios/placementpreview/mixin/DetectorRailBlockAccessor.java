package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.function.Predicate;

@Mixin(DetectorRailBlock.class)
public interface DetectorRailBlockAccessor {
    @Invoker("getInteractingMinecartOfType")
    <TMinecart extends AbstractMinecart> List<TMinecart> PlacementPreview$getInteractingMinecartOfType(Level level, BlockPos pos, Class<TMinecart> type, Predicate<Entity> entitySelector);
}
