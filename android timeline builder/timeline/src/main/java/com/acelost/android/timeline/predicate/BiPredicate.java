package com.acelost.android.timeline.predicate;

import androidx.annotation.NonNull;

public interface BiPredicate<U, V> {

    boolean evaluate(@NonNull U input1, @NonNull V input2);

}
