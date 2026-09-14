package dev.apexstudios.placementpreview.api.validator;

import dev.apexstudios.placementpreview.mixin.BlockItemAccessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public interface PlacementValidators {
    PlacementValidator SAME_BLOCK = (context, blockState) -> context.getLevel().getBlockState(context.getClickedPos()).is(blockState.getBlock());
    PlacementValidator EMPTY_BLOCK = (context, blockState) -> context.getLevel().isEmptyBlock(context.getClickedPos());
    PlacementValidator CLICKED_EMPTY_BLOCK = (context, blockState) -> context.getLevel().isEmptyBlock(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
    PlacementValidator CAN_SURVIVE = (context, blockState) -> blockState.canSurvive(context.getLevel(), context.getClickedPos());
    PlacementValidator INSIDE_WATER = (context, blockState) -> {
        var fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return fluidState.is(FluidTags.WATER) && fluidState.isFull();
    };
    PlacementValidator LADDER = (context, blockState) -> {
        if (context.replacingClickedOnBlock()) {
            return true;
        }

        var clickedFace = context.getClickedFace();
        var existingBlockState = context.getLevel().getBlockState(context.getClickedPos().relative(clickedFace.getOpposite()));
        return !existingBlockState.is(blockState.getBlock()) || existingBlockState.getValue(BlockStateProperties.HORIZONTAL_FACING) != clickedFace;
    };
    PlacementValidator WATERLOGGED = fromBoolean(BlockStateProperties.WATERLOGGED);

    PlacementValidator ITEM_ENABLED = (context, blockState) -> context.getItemInHand().isItemEnabled(context.getLevel().enabledFeatures());
    PlacementValidator BLOCK_ENABLED = (context, blockState) -> blockState.getBlock().isEnabled(context.getLevel().enabledFeatures());
    PlacementValidator IN_BOUNDS = (context, blockState) -> {
        var level = context.getLevel();
        var pos = context.getClickedPos();

        if(!level.isInWorldBounds(pos) || !level.isInValidBounds(pos)) {
            return false;
        }

        return level.getWorldBorder().isWithinBounds(pos);
    };
    PlacementValidator GAMEMASTER_ALLOWED = (context, blockState) -> {
        var player = context.getPlayer();

        if(player == null) {
            return false;
        }

        if(blockState.getBlock() instanceof GameMasterBlock) {
            return player.canUseGameMasterBlocks();
        }

        return true;
    };
    PlacementValidator REPLACEABLE = (context, blockState) -> context.getLevel().getBlockState(context.getClickedPos()).canBeReplaced(context);
    PlacementValidator CAN_PLACE_ITEM = (context, blockState) -> {
        if(!context.canPlace()) {
            return false;
        }

        if(!(context.getItemInHand().getItem() instanceof BlockItem item)) {
            return true;
        }

        return ((BlockItemAccessor) item).PlacementPreview$canPlace(context, blockState);
    };
    PlacementValidator PLACEABLE = BLOCK_ENABLED.and(IN_BOUNDS).and(GAMEMASTER_ALLOWED).and(REPLACEABLE).and(CAN_PLACE_ITEM);
    PlacementValidator PLACEABLE_MULTI = IN_BOUNDS.and(GAMEMASTER_ALLOWED).and(REPLACEABLE);

    static PlacementValidator hasProperty(Property<?> property) {
        return (context, blockState) -> blockState.hasProperty(property);
    }

    static <TValue extends Comparable<TValue>> PlacementValidator hasValue(Property<TValue> property, TValue value) {
        return (context, blockState) -> blockState.getValue(property) == value;
    }

    static PlacementValidator fromBoolean(Property<Boolean> property) {
        return (context, blockState) -> blockState.getValue(property);
    }
}
