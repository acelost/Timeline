package com.acelost.android.timeline.transform;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineInterval;
import com.acelost.android.timeline.predicate.Predicate;

import java.util.ArrayList;
import java.util.List;

import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class FilterTransformer implements TimelineTransformer {

    private final Predicate<TimelineInterval> predicate;

    public FilterTransformer(@NonNull final Predicate<TimelineInterval> predicate) {
        this.predicate = checkNotNull(predicate);
    }

    @NonNull
    @Override
    public List<TimelineInterval> transform(@NonNull final List<TimelineInterval> intervals) {
        final List<TimelineInterval> filtered = new ArrayList<>();
        for (TimelineInterval interval : intervals) {
            if (predicate.evaluate(interval)) {
                filtered.add(interval);
            }
        }
        return filtered;
    }

}
