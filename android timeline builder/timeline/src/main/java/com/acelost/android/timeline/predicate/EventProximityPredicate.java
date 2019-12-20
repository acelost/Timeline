package com.acelost.android.timeline.predicate;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineEvent;
import com.acelost.android.timeline.util.TimeUnitUtil;

import java.util.concurrent.TimeUnit;

import static com.acelost.android.timeline.Preconditions.checkNotNegative;
import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class EventProximityPredicate implements BiPredicate<TimelineEvent, TimelineEvent> {

    private final long distance;
    private final TimeUnit units;

    public EventProximityPredicate(final long distance, @NonNull final TimeUnit units) {
        this.distance = checkNotNegative(distance);
        this.units = checkNotNull(units);
    }

    @Override
    public boolean evaluate(@NonNull TimelineEvent event1, @NonNull TimelineEvent event2) {
        final TimeUnit e1Units = event1.getUnits();
        final TimeUnit e2Units = event2.getUnits();
        final TimeUnit minUnit = TimeUnitUtil.minUnit(e1Units, e2Units);
        final long e1Start = minUnit.convert(event1.getStart(), e1Units);
        final long e1End = minUnit.convert(event1.getEnd(), e1Units);
        final long e2Start = minUnit.convert(event2.getStart(), e2Units);
        final long e2End = minUnit.convert(event2.getEnd(), e2Units);
        final long joinThreshold = minUnit.convert(distance, units);
        if (e1Start < e2Start) {
            return e2Start - e1End <= joinThreshold;
        } else {
            return e1Start - e2End <= joinThreshold;
        }
    }

}
