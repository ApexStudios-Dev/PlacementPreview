package dev.apexstudios.placementpreview.api;

import dev.apexstudios.placementpreview.api.handler.PlacementHandler;
import dev.apexstudios.placementpreview.api.handler.UseOnHandler;
import dev.apexstudios.placementpreview.api.item2block.Item2BlockSupplier;
import dev.apexstudios.placementpreview.api.provider.BlockStateProvider;
import java.util.ServiceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface PlacementPreview {
    String ID = "placementpreview";

    PlacementPreview API = ServiceLoader.load(PlacementPreview.class, PlacementPreview.class.getClassLoader())
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load PlacementPreview Api"));

    static Identifier identifier(String identifier) {
        return Identifier.fromNamespaceAndPath(ID, identifier);
    }

    static String id(String identifier) {
        return ID + Identifier.NAMESPACE_SEPARATOR + identifier;
    }

    PsudeoRegistry.Keyed.Defaulted<Block, BlockStateProvider> blockStateProviders();

    PsudeoRegistry.Keyed.Defaulted<Item, Item2BlockSupplier> item2BlockSuppliers();

    PsudeoRegistry.List<PlacementHandler> placementHandlers();

    PsudeoRegistry.List<UseOnHandler> useOnHandlers();
}
