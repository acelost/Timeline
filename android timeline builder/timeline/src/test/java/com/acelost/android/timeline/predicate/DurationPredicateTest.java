package com.acelost.android.timeline.predicate;

import com.acelost.android.timeline.TimelineInterval;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class DurationPredicateTest {

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfDurationIsNegative() {
        new DurationPredicate(-1, TimeUnit.SECONDS) {
            @Override
            protected boolean evaluate(long intervalDuration, long conditionDuration) {
                return false;
            }
        };
    }

    @Test(expected = NullPointerException.class)
    public void assert_throwsExceptionIfUnitsIsNull() {
        new DurationPredicate(0, null) {
            @Override
            protected boolean evaluate(long intervalDuration, long conditionDuration) {
                return false;
            }
        };
    }

    @Test
    public void assert_intervalDurationValue() {
        final TimeUnit units = TimeUnit.SECONDS;
        final long start = 5;
        final long threshold = 2;
        final TimelineInterval interval1 = TimelineInterval.builder("interval", units).build(start, start + threshold - 1);
        final TimelineInterval interval2 = TimelineInterval.builder("interval", units).build(start, start + threshold);
        final TimelineInterval interval3 = TimelineInterval.builder("interval", units).build(start, start + threshold + 1);
        final TestDurationPredicate predicate = new TestDurationPredicate(2, units);

        predicate.evaluate(interval1);
        assertTrue(predicate.intervalDuration() < predicate.conditionDuration());

        predicate.evaluate(interval2);
        assertTrue(predicate.intervalDuration() == predicate.conditionDuration());

        predicate.evaluate(interval3);
        assertTrue(predicate.intervalDuration() > predicate.conditionDuration());
    }

}
