package dev.apexstudios.placementpreview.api.provider;

import dev.apexstudios.placementpreview.api.PsudeoRegistry;
import java.util.function.BiConsumer;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public final class RegisterBlockStateProvidersEvent extends Event implements IModBusEvent, PsudeoRegistry.Keyed.HolderRegistrar<Block, BlockStateProvider> {
    private final BiConsumer<Block, BlockStateProvider> registrar;

    @ApiStatus.Internal
    public RegisterBlockStateProvidersEvent(BiConsumer<Block, BlockStateProvider> registrar) {
        this.registrar = registrar;
    }

    @Override
    public void register(Block key, BlockStateProvider value) {
        registrar.accept(key, value);
    }
}
