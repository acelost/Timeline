package com.acelost.android.timeline.predicate;

import com.acelost.android.timeline.TimelineEvent;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventProximityPredicateTest {

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfDistanceIsNegative() {
        new EventProximityPredicate(-1, TimeUnit.NANOSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void assert_throwsExceptionIfUnitsIsNull() {
        new EventProximityPredicate(0, null);
    }

    @Test
    public void assert_trueIfEventsAreSame() {
        final TimeUnit units = TimeUnit.SECONDS;
        final long start = 0;
        final long end = 5;
        final TimelineEvent event1 = new TimelineEvent("event", units, start, end);
        final TimelineEvent event2 = new TimelineEvent("event", units, start, end);
        final EventProximityPredicate predicate = new EventProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertTrue(predicate.evaluate(event1, event2));
    }

    @Test
    public void assert_trueIfEventContainsOtherEvent() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineEvent event1 = new TimelineEvent("event", units, 0, 5);
        final TimelineEvent event2 = new TimelineEvent("event", units, 2, 3);
        final EventProximityPredicate predicate = new EventProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertTrue(predicate.evaluate(event1, event2));
        assertTrue(predicate.evaluate(event2, event1));
    }

    @Test
    public void assert_trueIfIntersects() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineEvent event1 = new TimelineEvent("event", units, 0, 5);
        final TimelineEvent event2 = new TimelineEvent("event", units, 4, 7);
        final EventProximityPredicate predicate = new EventProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertTrue(predicate.evaluate(event1, event2));
        assertTrue(predicate.evaluate(event2, event1));
    }

    @Test
    public void assert_trueIfTouches() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineEvent event1 = new TimelineEvent("event", units, 0, 5);
        final TimelineEvent event2 = new TimelineEvent("event", units, 5, 10);
        final EventProximityPredicate predicate = new EventProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertTrue(predicate.evaluate(event1, event2));
        assertTrue(predicate.evaluate(event2, event1));
    }

    @Test
    public void assert_falseIfNotTouchesAndDistanceIsZero() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineEvent event1 = new TimelineEvent("event", units, 0, 5);
        final TimelineEvent event2 = new TimelineEvent("event", units, 6, 10);
        final EventProximityPredicate predicate = new EventProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertFalse(predicate.evaluate(event1, event2));
        assertFalse(predicate.evaluate(event2, event1));
    }

    @Test
    public void assert_trueIfNotTouchesButDistanceLessThanThreshold() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineEvent event1 = new TimelineEvent("event", units, 0, 5);
        final TimelineEvent event2 = new TimelineEvent("event", units, 7, 10);
        final EventProximityPredicate predicate = new EventProximityPredicate(2, units);

        assertTrue(predicate.evaluate(event1, event2));
        assertTrue(predicate.evaluate(event2, event1));
    }

    @Test
    public void assert_falseIfNotTouchesAndDistanceGreaterThanThreshold() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineEvent event1 = new TimelineEvent("event", units, 0, 5);
        final TimelineEvent event2 = new TimelineEvent("event", units, 8, 10);
        final EventProximityPredicate predicate = new EventProximityPredicate(2, units);

        assertFalse(predicate.evaluate(event1, event2));
        assertFalse(predicate.evaluate(event2, event1));
    }

}
