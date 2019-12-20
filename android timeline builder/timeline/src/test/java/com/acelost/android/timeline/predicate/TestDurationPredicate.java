package com.acelost.android.timeline.predicate;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

public class TestDurationPredicate extends DurationPredicate {

    private Long eventDuration;
    private Long conditionDuration;

    public TestDurationPredicate(long duration, @NonNull TimeUnit units) {
        super(duration, units);
    }

    @Override
    protected boolean evaluate(long eventDuration, long conditionDuration) {
        this.eventDuration = eventDuration;
        this.conditionDuration = conditionDuration;
        return false;
    }

    public long eventDuration() {
        return eventDuration;
    }

    public long conditionDuration() {
        return conditionDuration;
    }
}
