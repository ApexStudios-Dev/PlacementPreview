package dev.apexstudios.placementpreview.api;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface PlacementResult<TValue> {
    TValue value();

    boolean isSuccess();

    default boolean isFailure() {
        return !isSuccess();
    }

    default PlacementResult<TValue> asSuccess() {
        return flatMapFailure(PlacementResult::success);
    }

    default PlacementResult<TValue> asFailure() {
        return flatMapSuccess(PlacementResult::failure);
    }

    default void execute(Consumer<? super TValue> successAction, Consumer<? super TValue> failureAction) {
        Objects.requireNonNull(successAction);
        Objects.requireNonNull(failureAction);

        if(isSuccess()) {
            successAction.accept(value());
        } else {
            failureAction.accept(value());
        }
    }

    default void execute(Consumer<? super TValue> action) {
        execute(action, action);
    }

    default void ifSuccess(Consumer<? super TValue> action) {
        Objects.requireNonNull(action);

        if(isSuccess()) {
            action.accept(value());
        }
    }

    default void ifFailed(Consumer<? super TValue> action) {
        Objects.requireNonNull(action);

        if(isFailure()) {
            action.accept(value());
        }
    }

    default PlacementResult<TValue> filter(Predicate<? super TValue> filter) {
        Objects.requireNonNull(filter);

        if(isFailure()) {
            return this;
        }

        var value = value();
        return filter.test(value) ? this : failure(value);
    }

    default <TResult> PlacementResult<TResult> map(Function<? super TValue, ? extends TResult> successMapper, Function<? super TValue, ? extends TResult> failureMapper) {
        Objects.requireNonNull(successMapper);
        Objects.requireNonNull(failureMapper);

        if(isSuccess()) {
            return success(successMapper.apply(value()));
        } else {
            return failure(failureMapper.apply(value()));
        }
    }

    default <TResult> PlacementResult<TResult> map(Function<? super TValue, ? extends TResult> mapper) {
        return map(mapper, mapper);
    }

    default PlacementResult<TValue> mapSuccess(Function<? super TValue, ? extends TValue> mapper) {
        Objects.requireNonNull(mapper);
        return isSuccess() ? success(mapper.apply(value())) : this;
    }

    default PlacementResult<TValue> mapFailure(Function<? super TValue, ? extends TValue> mapper) {
        Objects.requireNonNull(mapper);
        return isFailure() ? failure(mapper.apply(value())) : this;
    }

    @SuppressWarnings("unchecked")
    default <TResult> PlacementResult<TResult> flatMap(Function<? super TValue, ? extends PlacementResult<? extends TResult>> sucessMapper, Function<? super TValue, ? extends PlacementResult<? extends TResult>> failureMapper) {
        Objects.requireNonNull(sucessMapper);
        Objects.requireNonNull(failureMapper);

        if(isSuccess()) {
            return (PlacementResult<TResult>) sucessMapper.apply(value());
        } else {
            return (PlacementResult<TResult>) failureMapper.apply(value());
        }
    }

    default <TResult> PlacementResult<TResult> flatMap(Function<? super TValue, ? extends PlacementResult<? extends TResult>> mapper) {
        return flatMap(mapper, mapper);
    }

    @SuppressWarnings("unchecked")
    default PlacementResult<TValue> flatMapSuccess(Function<? super TValue, ? extends PlacementResult<? extends TValue>> mapper) {
        Objects.requireNonNull(mapper);
        return isSuccess() ? (PlacementResult<TValue>) mapper.apply(value()) : this;
    }

    @SuppressWarnings("unchecked")
    default PlacementResult<TValue> flatMapFailure(Function<? super TValue, ? extends PlacementResult<? extends TValue>> mapper) {
        Objects.requireNonNull(mapper);
        return isFailure() ? (PlacementResult<TValue>) mapper.apply(value()) : this;
    }

    default PlacementResult<TValue> success() {
        return isFailure() ? success(value()) : this;
    }

    default PlacementResult<TValue> fail() {
        return isSuccess() ? failure(value()) : this;
    }

    default Optional<TValue> optional() {
        return isSuccess() ? Optional.of(value()) : Optional.empty();
    }

    default Stream<TValue> stream() {
        return isSuccess() ? Stream.of(value()) : Stream.empty();
    }

    static <TValue> PlacementResult<TValue> of(TValue value, boolean success) {
        return new PlacementResult<>() {
            @Override
            public TValue value() {
                return value;
            }

            @Override
            public boolean isSuccess() {
                return success;
            }
        };
    }

    static <TValue> PlacementResult<TValue> success(TValue value) {
        return of(value, true);
    }

    static <TValue> PlacementResult<TValue> failure(TValue value) {
        return of(value, false);
    }
}
