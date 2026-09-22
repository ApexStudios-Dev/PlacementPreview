package dev.apexstudios.placementpreview.api.provider;

import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public final class RegisterBlockStateProvidersEvent extends Event implements IModBusEvent {
    private final BiConsumer<Block, BlockStateProvider> registrar;

    @ApiStatus.Internal
    public RegisterBlockStateProvidersEvent(BiConsumer<Block, BlockStateProvider> registrar) {
        this.registrar = registrar;
    }

    public void register(Block block, BlockStateProvider provider) {
        registrar.accept(block, provider);
    }

    public void register(Holder<Block> holder, BlockStateProvider provider) {
        register(holder.value(), provider);
    }

    public void register(WeatheringCopperCollection<Block> blocks, BlockStateProvider provider) {
        blocks.forEach(block -> register(block, provider));
    }

    public void register(ColorCollection<Block> blocks, BlockStateProvider provider) {
        blocks.forEach(block -> register(block, provider));
    }
}
