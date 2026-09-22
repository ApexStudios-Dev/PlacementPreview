package dev.apexstudios.placementpreview.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BoatItem.class)
public interface BoatItemAccessor {
    @Invoker("getBoat")
    @Nullable AbstractBoat PlacementPreview$getBoat(Level level, HitResult hitResult, ItemStack stack, Player player);
}
