package dev.apexstudios.placementpreview.api.item2block;

import dev.apexstudios.placementpreview.api.PsudeoRegistry;
import java.util.function.BiConsumer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public final class RegisterItem2BlockSuppliersEvent extends Event implements IModBusEvent, PsudeoRegistry.Keyed.HolderRegistrar<Item, Item2BlockSupplier> {
    private final BiConsumer<Item, Item2BlockSupplier> registrar;

    @ApiStatus.Internal
    public RegisterItem2BlockSuppliersEvent(BiConsumer<Item, Item2BlockSupplier> registrar) {
        this.registrar = registrar;
    }

    @Override
    public void register(Item key, Item2BlockSupplier value) {
        registrar.accept(key, value);
    }
}
