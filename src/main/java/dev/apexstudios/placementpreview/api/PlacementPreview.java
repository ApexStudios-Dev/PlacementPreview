package dev.apexstudios.placementpreview.api;

import dev.apexstudios.ghostrenderer.api.GhostProperties;
import dev.apexstudios.placementpreview.api.handler.UseOnHandler;
import dev.apexstudios.placementpreview.api.provider.BlockStateProvider;
import java.util.ServiceLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.TypedInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

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

    BlockStateProvider getBlockStateProvider(Block block);

    default BlockStateProvider getBlockStateProvider(Holder<Block> holder) {
        return getBlockStateProvider(holder.value());
    }

    default BlockStateProvider getBlockStateProvider(TypedInstance<Block> instance) {
        return getBlockStateProvider(instance.typeHolder());
    }

    @Nullable UseOnHandler getUseOnHandler(Item item);

    default @Nullable UseOnHandler getUseOnHandler(Holder<Item> holder) {
        return getUseOnHandler(holder.value());
    }

    default @Nullable UseOnHandler getUseOnHandler(TypedInstance<Item> instance) {
        return getUseOnHandler(instance.typeHolder());
    }
}
