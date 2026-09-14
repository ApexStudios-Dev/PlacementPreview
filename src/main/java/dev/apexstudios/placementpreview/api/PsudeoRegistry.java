package dev.apexstudios.placementpreview.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.core.Holder;
import net.minecraft.core.TypedInstance;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import org.jspecify.annotations.Nullable;

public interface PsudeoRegistry {
    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    interface List<TValue> extends PsudeoRegistry, Iterable<TValue> {
        boolean contains(TValue value);

        Iterator<TValue> iterator();

        TValue[] toArray();

        TValue[] toArray(TValue[] array);

        default TValue[] toArray(IntFunction<TValue[]> generator) {
            return toArray(generator.apply(0));
        }

        default Stream<TValue> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        default Stream<TValue> parallelStream() {
            return StreamSupport.stream(spliterator(), true);
        }

        TValue get(int index);

        int indexOf(TValue value);

        int lasIndexOf(TValue value);

        interface Registrar<TValue> {
            void register(TValue value);
        }

        interface HolderRegistrar<TValue> extends Registrar<TValue> {
            default void register(ColorCollection<TValue> values) {
                values.forEach(this::register);
            }

            default void register(WeatheringCopperCollection<TValue> values) {
                values.forEach(this::register);
            }

            default void register(Holder<TValue> holder) {
                register(holder.value());
            }
        }
    }

    interface Keyed<TKey, TValue> extends PsudeoRegistry {
        boolean containsKey(TKey key);

        boolean containsValue(TValue value);

        Set<TKey> keySet();

        Collection<TValue> values();

        Set<Map.Entry<TKey, TValue>> entrySet();

        default void forEach(BiConsumer<? super TKey, ? super TValue> action) {
            Objects.requireNonNull(action);
            entrySet().forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
        }

        interface NoDefault<TKey, TValue> extends Keyed<TKey, TValue> {
            @Nullable TValue get(TKey key);

            default @Nullable TValue get(Holder<TKey> holder) {
                return get(holder.value());
            }

            default @Nullable TValue get(TypedInstance<TKey> instance) {
                return get(instance.typeHolder());
            }

            default TValue getOrDefault(TKey key, TValue value) {
                return Objects.requireNonNullElse(get(key), value);
            }

            default TValue getOrDefault(Holder<TKey> holder, TValue value) {
                return getOrDefault(holder.value(), value);
            }

            default TValue getOrDefault(TypedInstance<TKey> instance, TValue value) {
                return getOrDefault(instance.typeHolder(), value);
            }
        }

        interface Defaulted<TKey, TValue> extends Keyed<TKey, TValue> {
            TValue get(TKey key);

            default TValue get(Holder<TKey> holder) {
                return get(holder.value());
            }

            default TValue get(TypedInstance<TKey> instance) {
                return get(instance.typeHolder());
            }

            TValue getDefault();
        }

        interface Registrar<TKey, TValue> {
            void register(TKey key, TValue value);
        }

        interface HolderRegistrar<TKey, TValue> extends Registrar<TKey, TValue> {
            default void register(ColorCollection<TKey> keys, TValue value) {
                keys.forEach(key -> register(key, value));
            }

            default void register(WeatheringCopperCollection<TKey> keys, TValue value) {
                keys.forEach(key -> register(key, value));
            }

            default void register(Holder<TKey> holder, TValue value) {
                register(holder.value(), value);
            }
        }
    }
}
