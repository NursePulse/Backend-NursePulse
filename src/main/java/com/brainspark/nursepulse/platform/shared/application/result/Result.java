package com.brainspark.nursepulse.platform.shared.application.result;
import java.util.Optional;
import java.util.function.Function;
public sealed interface Result<T,E> permits Result.Success, Result.Failure {
    record Success<T, E>(T value) implements Result<T, E> {
    }

    record Failure<T, E>(E error) implements Result<T, E> {
    }

    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }

    default boolean isSuccess() {
        return this instanceof Success<T, E>;
    }

    default boolean isFailure() {
        return this instanceof Failure<T, E>;
    }

    default Optional<T> toOptional() {
        return switch (this) {
            case Success<T, E> success -> Optional.of(success.value());
            case Failure<T, E> failure -> Optional.empty();
        };
    }

    default T getOrElse(T defaultValue) {
        return switch (this) {
            case Success<T, E> success -> success.value();
            case Failure<T, E> failure -> defaultValue;
        };
    }

    default <U> Result<U, E> map(Function<T, U> mapper) {
        return switch (this) {
            case Success<T, E> success -> Result.success(mapper.apply(success.value()));
            case Failure<T, E> failure -> Result.failure(failure.error());
        };
    }
}
