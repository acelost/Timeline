package com.acelost.android.timeline.predicate;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.Preconditions;
import com.acelost.android.timeline.TimelineEvent;

import java.util.concurrent.TimeUnit;

import static com.acelost.android.timeline.Preconditions.checkNotNegative;

public abstract class DurationPredicate implements Predicate<TimelineEvent> {

    private final long duration;
    private final TimeUnit units;

    public DurationPredicate(final long duration, @NonNull final TimeUnit units) {
        this.duration = checkNotNegative(duration);
        this.units = Preconditions.checkNotNull(units);
    }

    @Override
    public final boolean evaluate(@NonNull TimelineEvent input) {
        final long eventDuration = input.getEnd() - input.getStart();
        final long conditionDuration = input.getUnits().convert(duration, units);
        return evaluate(eventDuration, conditionDuration);
    }

    protected abstract boolean evaluate(long eventDuration, long conditionDuration);

}
