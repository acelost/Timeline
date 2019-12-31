package com.acelost.android.timeline.predicate;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineInterval;
import com.acelost.android.timeline.util.TimeUnitUtil;

import java.util.concurrent.TimeUnit;

import static com.acelost.android.timeline.Preconditions.checkNotNegative;
import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class IntervalProximityPredicate implements BiPredicate<TimelineInterval, TimelineInterval> {

    private final long distance;
    private final TimeUnit units;

    public IntervalProximityPredicate(final long distance, @NonNull final TimeUnit units) {
        this.distance = checkNotNegative(distance);
        this.units = checkNotNull(units);
    }

    @Override
    public boolean evaluate(@NonNull TimelineInterval interval1, @NonNull TimelineInterval interval2) {
        final TimeUnit i1Units = interval1.getUnits();
        final TimeUnit i2Units = interval2.getUnits();
        final TimeUnit minUnit = TimeUnitUtil.minUnit(i1Units, i2Units);
        final long i1Start = minUnit.convert(interval1.getStart(), i1Units);
        final long i1End = minUnit.convert(interval1.getEnd(), i1Units);
        final long i2Start = minUnit.convert(interval2.getStart(), i2Units);
        final long i2End = minUnit.convert(interval2.getEnd(), i2Units);
        final long joinThreshold = minUnit.convert(distance, units);
        if (i1Start < i2Start) {
            return i2Start - i1End <= joinThreshold;
        } else {
            return i1Start - i2End <= joinThreshold;
        }
    }

}
