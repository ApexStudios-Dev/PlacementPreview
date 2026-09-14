package dev.apexstudios.placementpreview.core.apiimpl;

import dev.apexstudios.placementpreview.api.PsudeoRegistry;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.IModBusEvent;
import org.jspecify.annotations.Nullable;

abstract class PsudeoRegistryImpl implements PsudeoRegistry {
    protected PsudeoRegistryImpl() {}

    public abstract void register();

    static final class List<TValue, TEvent extends Event & IModBusEvent & PsudeoRegistry.List.Registrar<TValue>> extends PsudeoRegistryImpl implements PsudeoRegistry.List<TValue> {
        private final java.util.List<TValue> registry = new ArrayList<>();
        private final java.util.List<TValue> view = Collections.unmodifiableList(registry);
        private final Class<TValue> type;
        private final Function<Consumer<TValue>, TEvent> eventFactory;

        List(Class<TValue> type, Function<Consumer<TValue>, TEvent> eventFactory) {
            this.type = type;
            this.eventFactory = eventFactory;
        }

        @Override
        public void register() {
            registry.clear();
            ModLoader.postEvent(eventFactory.apply(registry::add));
        }

        @Override
        public int size() {
            return registry.size();
        }

        @Override
        public boolean contains(TValue value) {
            return registry.contains(value);
        }

        @Override
        public Iterator<TValue> iterator() {
            return registry.iterator();
        }

        @SuppressWarnings("unchecked")
        @Override
        public TValue[] toArray() {
            return toArray((TValue[]) Array.newInstance(type, size()));
        }

        @Override
        public TValue[] toArray(TValue[] array) {
            return registry.toArray(array);
        }

        @Override
        public TValue get(int index) {
            return registry.get(index);
        }

        @Override
        public int indexOf(TValue value) {
            return registry.indexOf(value);
        }

        @Override
        public int lasIndexOf(TValue value) {
            return registry.lastIndexOf(value);
        }
    }

    static abstract class Keyed<TKey, TValue, TEvent extends Event & IModBusEvent & PsudeoRegistry.Keyed.Registrar<TKey, TValue>> extends PsudeoRegistryImpl implements PsudeoRegistry.Keyed<TKey, TValue> {
        private final Map<TKey, TValue> registry = new IdentityHashMap<>();
        private final Map<TKey, TValue> view = Collections.unmodifiableMap(registry);
        private final Function<BiConsumer<TKey, TValue>, TEvent> eventFactory;
        private final Function<TKey, Holder.Reference<TKey>> holderGetter;
        private final String registryName;

        protected Keyed(Function<BiConsumer<TKey, TValue>, TEvent> eventFactory, Function<TKey, Holder.Reference<TKey>> holderGetter, String registryName) {
            this.eventFactory = eventFactory;
            this.holderGetter = holderGetter;
            this.registryName = registryName;
        }

        protected @Nullable TValue getNullable(TKey key) {
            return registry.get(key);
        }

        @Override
        public void register() {
            registry.clear();

            ModLoader.postEvent(eventFactory.apply((key, value) -> {
                if(registry.putIfAbsent(key, value) != null) {
                    throw new IllegalStateException("Duplicate " + registryName + " registration: " + holderGetter.apply(key).key());
                }
            }));
        }

        @Override
        public int size() {
            return registry.size();
        }

        @Override
        public boolean containsKey(TKey key) {
            return registry.containsKey(key);
        }

        @Override
        public boolean containsValue(TValue value) {
            return registry.containsValue(value);
        }

        @Override
        public Set<TKey> keySet() {
            return view.keySet();
        }

        @Override
        public Collection<TValue> values() {
            return view.values();
        }

        @Override
        public Set<Map.Entry<TKey, TValue>> entrySet() {
            return view.entrySet();
        }

        static final class NoDefault<TKey, TValue, TEvent extends Event & IModBusEvent & PsudeoRegistry.Keyed.Registrar<TKey, TValue>> extends PsudeoRegistryImpl.Keyed<TKey, TValue, TEvent> implements PsudeoRegistry.Keyed.NoDefault<TKey, TValue> {
            NoDefault(Function<BiConsumer<TKey, TValue>, TEvent> eventFactory, Function<TKey, Holder.Reference<TKey>> holderGetter, String registryName) {
                super(eventFactory, holderGetter, registryName);
            }

            @Override
            public @Nullable TValue get(TKey key) {
                return getNullable(key);
            }
        }

        static final class Defaulted<TKey, TValue, TEvent extends Event & IModBusEvent & PsudeoRegistry.Keyed.Registrar<TKey, TValue>> extends PsudeoRegistryImpl.Keyed<TKey, TValue, TEvent> implements PsudeoRegistry.Keyed.Defaulted<TKey, TValue> {
            private final TValue defaultValue;

            Defaulted(Function<BiConsumer<TKey, TValue>, TEvent> eventFactory, Function<TKey, Holder.Reference<TKey>> holderGetter, String registryName, TValue defaultValue) {
                super(eventFactory, holderGetter, registryName);

                this.defaultValue = defaultValue;
            }

            @Override
            public TValue get(TKey key) {
                return Objects.requireNonNullElse(getNullable(key), defaultValue);
            }

            @Override
            public TValue getDefault() {
                return defaultValue;
            }
        }
    }
}
