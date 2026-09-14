package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.placementpreview.api.PsudeoRegistry;
import java.util.function.BiConsumer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public final class RegisterUseOnHandlersEvent extends Event implements IModBusEvent, PsudeoRegistry.Keyed.Registrar<Item, UseOnHandler> {
    private final BiConsumer<Item, UseOnHandler> registrar;

    @ApiStatus.Internal
    public RegisterUseOnHandlersEvent(BiConsumer<Item, UseOnHandler> registrar) {
        this.registrar = registrar;
    }

    @Override
    public void register(Item key, UseOnHandler value) {
        registrar.accept(key, value);
    }
}
