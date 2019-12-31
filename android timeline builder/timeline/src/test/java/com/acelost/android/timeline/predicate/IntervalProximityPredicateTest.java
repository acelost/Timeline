package com.acelost.android.timeline.predicate;

import com.acelost.android.timeline.TimelineInterval;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntervalProximityPredicateTest {

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfDistanceIsNegative() {
        new IntervalProximityPredicate(-1, TimeUnit.NANOSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void assert_throwsExceptionIfUnitsIsNull() {
        new IntervalProximityPredicate(0, null);
    }

    @Test
    public void assert_trueIfIntervalsAreSame() {
        final TimeUnit units = TimeUnit.SECONDS;
        final long start = 0;
        final long end = 5;
        final TimelineInterval interval1 = TimelineInterval.builder("interval", units).build(start, end);
        final TimelineInterval interval2 = TimelineInterval.builder("interval", units).build(start, end);
        final IntervalProximityPredicate predicate = new IntervalProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertTrue(predicate.evaluate(interval1, interval2));
    }

    @Test
    public void assert_trueIfIntervalContainsOtherInterval() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineInterval interval1 = TimelineInterval.builder("interval", units).build(0, 5);
        final TimelineInterval interval2 = TimelineInterval.builder("interval", units).build(2, 3);
        final IntervalProximityPredicate predicate = new IntervalProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertTrue(predicate.evaluate(interval1, interval2));
        assertTrue(predicate.evaluate(interval2, interval1));
    }

    @Test
    public void assert_trueIfIntersects() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineInterval interval1 = TimelineInterval.builder("interval", units).build(0, 5);
        final TimelineInterval interval2 = TimelineInterval.builder("interval", units).build(4, 7);
        final IntervalProximityPredicate predicate = new IntervalProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertTrue(predicate.evaluate(interval1, interval2));
        assertTrue(predicate.evaluate(interval2, interval1));
    }

    @Test
    public void assert_trueIfTouches() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineInterval interval1 = TimelineInterval.builder("interval", units).build(0, 5);
        final TimelineInterval interval2 = TimelineInterval.builder("interval", units).build(5, 10);
        final IntervalProximityPredicate predicate = new IntervalProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertTrue(predicate.evaluate(interval1, interval2));
        assertTrue(predicate.evaluate(interval2, interval1));
    }

    @Test
    public void assert_falseIfNotTouchesAndDistanceIsZero() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineInterval interval1 = TimelineInterval.builder("interval", units).build(0, 5);
        final TimelineInterval interval2 = TimelineInterval.builder("interval", units).build(6, 10);
        final IntervalProximityPredicate predicate = new IntervalProximityPredicate(0, TimeUnit.NANOSECONDS);

        assertFalse(predicate.evaluate(interval1, interval2));
        assertFalse(predicate.evaluate(interval2, interval1));
    }

    @Test
    public void assert_trueIfNotTouchesButDistanceLessThanThreshold() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineInterval interval1 = TimelineInterval.builder("interval", units).build(0, 5);
        final TimelineInterval interval2 = TimelineInterval.builder("interval", units).build(7, 10);
        final IntervalProximityPredicate predicate = new IntervalProximityPredicate(2, units);

        assertTrue(predicate.evaluate(interval1, interval2));
        assertTrue(predicate.evaluate(interval2, interval1));
    }

    @Test
    public void assert_falseIfNotTouchesAndDistanceGreaterThanThreshold() {
        final TimeUnit units = TimeUnit.SECONDS;
        final TimelineInterval interval1 = TimelineInterval.builder("interval", units).build(0, 5);
        final TimelineInterval interval2 = TimelineInterval.builder("interval", units).build(8, 10);
        final IntervalProximityPredicate predicate = new IntervalProximityPredicate(2, units);

        assertFalse(predicate.evaluate(interval1, interval2));
        assertFalse(predicate.evaluate(interval2, interval1));
    }

}
