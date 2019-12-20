package com.acelost.android.timeline;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Preconditions {

    @NonNull
    public static <T> T checkNotNull(final T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    @NonNull
    public static String checkNotEmpty(final String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    @NonNull
    public static String checkAtLeast(final String string, final int minLength) {
        if (string == null || string.length() < minLength) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    public static int checkIsPositive(int value) {
        if (value < 1) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static long checkIsPositive(long value) {
        if (value < 1) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static long checkNotNegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static <T> void checkUnique(@NonNull final T... values) {
        if (values.length > 2) {
            final Set<T> unique = new HashSet<>(Arrays.asList(values));
            if (unique.size() != values.length) {
                throw new IllegalArgumentException();
            }
        }
    }

}
