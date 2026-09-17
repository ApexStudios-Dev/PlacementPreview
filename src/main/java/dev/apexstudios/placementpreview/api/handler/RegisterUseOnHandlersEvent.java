package dev.apexstudios.placementpreview.api.handler;

import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public final class RegisterUseOnHandlersEvent extends Event implements IModBusEvent {
    private final BiConsumer<Item, UseOnHandler> registrar;

    @ApiStatus.Internal
    public RegisterUseOnHandlersEvent(BiConsumer<Item, UseOnHandler> registrar) {
        this.registrar = registrar;
    }

    public void register(Item item, UseOnHandler handler) {
        registrar.accept(item, handler);
    }

    public void register(Holder<Item> holder, UseOnHandler handler) {
        register(holder.value(), handler);
    }

    public void register(WeatheringCopperCollection<Item> items, UseOnHandler handler) {
        items.forEach(item -> register(item, handler));
    }

    public void register(ColorCollection<Item> items, UseOnHandler handler) {
        items.forEach(item -> register(item, handler));
    }
}
