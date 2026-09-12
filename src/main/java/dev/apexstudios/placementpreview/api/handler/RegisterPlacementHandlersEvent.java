package dev.apexstudios.placementpreview.api.handler;

import dev.apexstudios.placementpreview.api.PsudeoRegistry;
import java.util.function.Consumer;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public final class RegisterPlacementHandlersEvent extends Event implements IModBusEvent, PsudeoRegistry.List.Registrar<PlacementHandler> {
    private final Consumer<PlacementHandler> registrar;

    @ApiStatus.Internal
    public RegisterPlacementHandlersEvent(Consumer<PlacementHandler> registrar) {
        this.registrar = registrar;
    }

    @Override
    public void register(PlacementHandler value) {
        registrar.accept(value);
    }
}
