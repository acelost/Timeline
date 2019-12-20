package com.acelost.android.timeline.predicate;

import androidx.annotation.NonNull;

import java.util.Collection;

import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class Predicates {

    private static final Predicate ALWAYS_TRUE_PREDICATE = new Predicate() {
        @Override
        public boolean evaluate(@NonNull Object input) {
            return true;
        }
    };

    @NonNull
    public static <T> Predicate<T> alwaysTrue() {
        //noinspection unchecked
        return ALWAYS_TRUE_PREDICATE;
    }

    @NonNull
    public static <T> Predicate<T> in(@NonNull final T[] array) {
        checkNotNull(array);
        return new Predicate<T>() {
            @Override
            public boolean evaluate(@NonNull T input) {
                for (T item : array) {
                    if (input.equals(item)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @NonNull
    public static <T> Predicate<T> in(@NonNull final Collection<T> collection) {
        checkNotNull(collection);
        return new Predicate<T>() {
            @Override
            public boolean evaluate(@NonNull T input) {
                return collection.contains(input);
            }
        };
    }

}
