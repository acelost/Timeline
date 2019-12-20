package com.acelost.android.timeline.predicate;

import androidx.annotation.NonNull;

public interface Predicate<T> {

    boolean evaluate(@NonNull T input);

}
