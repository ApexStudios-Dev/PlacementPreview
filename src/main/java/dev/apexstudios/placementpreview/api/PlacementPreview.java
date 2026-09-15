package dev.apexstudios.placementpreview.api;

import dev.apexstudios.ghostrenderer.api.GhostProperties;
import dev.apexstudios.placementpreview.api.handler.UseOnHandler;
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

    GhostProperties GHOST_PROPERTIES = new GhostProperties() {};

    static Identifier identifier(String identifier) {
        return Identifier.fromNamespaceAndPath(ID, identifier);
    }

    static String id(String identifier) {
        return ID + Identifier.NAMESPACE_SEPARATOR + identifier;
    }

    PsudeoRegistry.Keyed.Defaulted<Block, BlockStateProvider> blockStateProviders();

    PsudeoRegistry.Keyed.NoDefault<Item, UseOnHandler> useOnHandlers();
}
