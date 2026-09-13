package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.placementpreview.api.PsudeoRegistry;
import java.util.function.Consumer;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public final class RegisterUseOnHandlersEvent extends Event implements IModBusEvent, PsudeoRegistry.List.Registrar<UseOnHandler> {
    private final Consumer<UseOnHandler> registrar;

    @ApiStatus.Internal
    public RegisterUseOnHandlersEvent(Consumer<UseOnHandler> registrar) {
        this.registrar = registrar;
    }

    @Override
    public void register(UseOnHandler value) {
        registrar.accept(value);
    }
}
