package dev.apexstudios.placementpreview.api.validator;

import dev.apexstudios.placementpreview.api.PlacementResult;
import dev.apexstudios.placementpreview.api.provider.BlockStateProvider;
import java.util.Objects;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface PlacementValidator {
    boolean test(BlockPlaceContext context, BlockState blockState);

    default PlacementValidator and(PlacementValidator after) {
        Objects.requireNonNull(after);
        return (context, blockState) -> test(context, blockState) && after.test(context, blockState);
    }

    default PlacementValidator or(PlacementValidator after) {
        Objects.requireNonNull(after);
        return (context, blockState) -> test(context, blockState) || after.test(context, blockState);
    }

    default PlacementValidator negate() {
        return (context, blockState) -> !test(context, blockState);
    }

    default BlockStateProvider asProvider() {
        return (context, blockState) -> PlacementResult.of(blockState, test(context, blockState)) ;
    }
}
