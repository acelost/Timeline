package com.acelost.android.timeline.predicate;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

public class TestDurationPredicate extends DurationPredicate {

    private Long intervalDuration;
    private Long conditionDuration;

    public TestDurationPredicate(long duration, @NonNull TimeUnit units) {
        super(duration, units);
    }

    @Override
    protected boolean evaluate(long intervalDuration, long conditionDuration) {
        this.intervalDuration = intervalDuration;
        this.conditionDuration = conditionDuration;
        return false;
    }

    public long intervalDuration() {
        return intervalDuration;
    }

    public long conditionDuration() {
        return conditionDuration;
    }
}
