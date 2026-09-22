package dev.apexstudios.placementpreview.api;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public interface BlockStateHelper {
    static BlockState copyFrom(BlockState source, BlockState destination) {
        var result = destination;

        for(var property : source.getProperties()) {
            result = copyFrom(source, destination, property);
        }

        return result;
    }

    static <TValue extends Comparable<TValue>> BlockState copyFrom(BlockState source, BlockState destination, Property<TValue> property) {
        if(source.hasProperty(property) && destination.hasProperty(property)) {
            return destination.setValue(property, source.getValue(property));
        }

        return destination;
    }

    static BlockState getDefaultBlockState(DataComponentHolder components, Block block) {
        return components.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(block.defaultBlockState());
    }
}
