package com.acelost.android.timeline.predicate;

import com.acelost.android.timeline.TimelineEvent;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class DurationPredicateTest {

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfDurationIsNegative() {
        new DurationPredicate(-1, TimeUnit.SECONDS) {
            @Override
            protected boolean evaluate(long eventDuration, long conditionDuration) {
                return false;
            }
        };
    }

    @Test(expected = NullPointerException.class)
    public void assert_throwsExceptionIfUnitsIsNull() {
        new DurationPredicate(0, null) {
            @Override
            protected boolean evaluate(long eventDuration, long conditionDuration) {
                return false;
            }
        };
    }

    @Test
    public void assert_eventDurationValue() {
        final TimeUnit units = TimeUnit.SECONDS;
        final long start = 5;
        final long threshold = 2;
        final TimelineEvent event1 = new TimelineEvent("event", units, start, start + threshold - 1);
        final TimelineEvent event2 = new TimelineEvent("event", units, start, start + threshold);
        final TimelineEvent event3 = new TimelineEvent("event", units, start, start + threshold + 1);
        final TestDurationPredicate predicate = new TestDurationPredicate(2, units);

        predicate.evaluate(event1);
        assertTrue(predicate.eventDuration() < predicate.conditionDuration());

        predicate.evaluate(event2);
        assertTrue(predicate.eventDuration() == predicate.conditionDuration());

        predicate.evaluate(event3);
        assertTrue(predicate.eventDuration() > predicate.conditionDuration());
    }

}
