package dev.apexstudios.placementpreview.api.item2block;

import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.provider.BlockStateProvider;
import dev.apexstudios.placementpreview.api.validator.PlacementValidator;
import dev.apexstudios.placementpreview.mixin.StandingAndWallBlockItemAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface Item2BlockSupplier {
    Item2BlockSupplier BLOCK_ITEM = (context, item) -> BlockStateProvider.applyDefaults(context, item instanceof BlockItem blockItem ? blockItem.getBlock() : Blocks.AIR);

    PlacementResult<BlockState> apply(BlockPlaceContext context, Item item);

    static Item2BlockSupplier standingAndWall(Direction attachmentDirection, Block standingBlock, Block wallBlock, PlacementValidator validator) {
        var attachmentDirectionOpposite = attachmentDirection.getOpposite();

        return (context, item) -> {
            for(var direction : context.getNearestLookingDirections()) {
                if(direction == attachmentDirectionOpposite) {
                    continue;
                }

                var block = direction == attachmentDirection ? standingBlock : wallBlock;
                var result = BlockStateProvider.applyDefaults(context, block)
                        .filter(blockState -> validator.test(context, blockState));

                if(result.isSuccess()) {
                    return result;
                }
            }

            return PlacementResult.failure(standingBlock.defaultBlockState());
        };
    }

    static Item2BlockSupplier standingAndWall(StandingAndWallBlockItem item) {
        var accessor = (StandingAndWallBlockItemAccessor) item;
        return standingAndWall(item.attachmentDirection, item.getBlock(), item.wallBlock, (context, blockState) -> accessor.PlacementPreview$canPlace(context.getLevel(), blockState, context.getClickedPos()));
    }
}
