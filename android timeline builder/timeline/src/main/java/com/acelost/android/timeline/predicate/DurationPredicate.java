package com.acelost.android.timeline.predicate;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineInterval;

import java.util.concurrent.TimeUnit;

import static com.acelost.android.timeline.Preconditions.checkNotNegative;
import static com.acelost.android.timeline.Preconditions.checkNotNull;

public abstract class DurationPredicate implements Predicate<TimelineInterval> {

    private final long duration;
    private final TimeUnit units;

    public DurationPredicate(final long duration, @NonNull final TimeUnit units) {
        this.duration = checkNotNegative(duration);
        this.units = checkNotNull(units);
    }

    @Override
    public final boolean evaluate(@NonNull TimelineInterval input) {
        final long intervalDuration = input.getEnd() - input.getStart();
        final long conditionDuration = input.getUnits().convert(duration, units);
        return evaluate(intervalDuration, conditionDuration);
    }

    protected abstract boolean evaluate(long intervalDuration, long conditionDuration);

}
