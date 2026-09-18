package dev.apexstudios.placementpreview.api.provider;

import dev.apexstudios.placementpreview.api.BlockStateHelper;
import dev.apexstudios.placementpreview.api.PlacementPreview;
import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.validator.PlacementValidator;
import java.util.Objects;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface BlockStateProvider {
    BlockStateProvider SUCCESS = (context, blockState) -> PlacementResult.success(blockState);
    BlockStateProvider FAILURE = (context, blockState) -> PlacementResult.failure(blockState);

    PlacementResult<BlockState> apply(BlockPlaceContext context, BlockState blockState);

    default BlockStateProvider asSuccess() {
        return andThen(SUCCESS);
    }

    default BlockStateProvider asFailure() {
        return andThen(FAILURE);
    }

    default BlockStateProvider andThen(BlockStateProvider after) {
        Objects.requireNonNull(after);
        return (context, blockState) -> apply(context, blockState)
                .flatMapSuccess(state -> after.apply(context, state));
    }

    default PlacementValidator asValidator() {
        return (context, blockState) -> apply(context, blockState).isSuccess();
    }

    static PlacementResult<BlockState> applyDefaults(BlockPlaceContext context, Block block) {
        var blockState = BlockStateHelper.getDefaultBlockState(context.getItemInHand(), block);
        return PlacementPreview.API.getBlockStateProvider(blockState).apply(context, blockState);
    }

    static BlockStateProvider fromVanilla(Vanilla vanilla) {
        return (context, blockState) -> {
            var result = vanilla.apply(context, blockState);
            return result == null ? PlacementResult.failure(blockState) : PlacementResult.success(result);
        };
    }

    @FunctionalInterface
    interface ForProperty<TValue extends Comparable<TValue>> {
        PlacementResult<TValue> apply(BlockPlaceContext context, BlockState blockState, TValue current);
    }

    @FunctionalInterface
    interface Vanilla {
        @Nullable BlockState apply(BlockPlaceContext context, BlockState blockState);
    }
}
