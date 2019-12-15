package com.acelost.android.timeline;

import androidx.annotation.NonNull;

class Preconditions {

    @NonNull
    static <T> T checkNotNull(final T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    @NonNull
    static String checkNotEmpty(final String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    @NonNull
    static String checkAtLeast(final String string, final int minLength) {
        if (string == null || string.length() < minLength) {
            throw new IllegalArgumentException();
        }
        return string;
    }

}
