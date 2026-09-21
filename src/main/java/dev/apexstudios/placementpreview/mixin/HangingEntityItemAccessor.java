package dev.apexstudios.placementpreview.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HangingEntityItem.class)
public interface HangingEntityItemAccessor {
    @Invoker("mayPlace")
    boolean PlacementPreview$mayPlace(Player player, Direction direction, ItemStack itemStack, BlockPos blockPos);
}
