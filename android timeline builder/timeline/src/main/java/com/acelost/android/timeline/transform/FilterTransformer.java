package com.acelost.android.timeline.transform;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.Preconditions;
import com.acelost.android.timeline.TimelineEvent;
import com.acelost.android.timeline.predicate.Predicate;

import java.util.ArrayList;
import java.util.List;

public final class FilterTransformer implements TimelineTransformer {

    private final Predicate<TimelineEvent> predicate;

    public FilterTransformer(@NonNull final Predicate<TimelineEvent> predicate) {
        this.predicate = Preconditions.checkNotNull(predicate);
    }

    @NonNull
    @Override
    public List<TimelineEvent> transform(@NonNull final List<TimelineEvent> events) {
        final List<TimelineEvent> filtered = new ArrayList<>();
        for (TimelineEvent event : events) {
            if (predicate.evaluate(event)) {
                filtered.add(event);
            }
        }
        return filtered;
    }

}
