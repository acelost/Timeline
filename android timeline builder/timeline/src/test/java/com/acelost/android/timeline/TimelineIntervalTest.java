package com.acelost.android.timeline;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TimelineIntervalTest {

    private static final long NANOS_IN_MILLISECOND = 1_000_000;
    private static final long MILLIS_IN_SECOND = 1_000;

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfNameIsNull() {
        new TimelineInterval(null, null, null, TimeUnit.SECONDS, 0, 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfNameIsEmpty() {
        new TimelineInterval("", null, null, TimeUnit.SECONDS, 0, 0, 1);
    }

    @Test(expected = NullPointerException.class)
    public void assert_throwsExceptionIfUnitsNotSpecified() {
        new TimelineInterval("interval", null,null, null, 0, 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfCountIsNotPositive() {
        new TimelineInterval("interval", null, null, TimeUnit.SECONDS, 0, 0, 0);
    }

    @Test
    public void assert_millisNotChanged() {
        final long startMillis = 1;
        final long endMillis = 5;
        final TimelineInterval interval = TimelineInterval.builder("interval", TimeUnit.MILLISECONDS).build(startMillis, endMillis);

        assertEquals(interval.getStartMillis(), startMillis);
        assertEquals(interval.getEndMillis(), endMillis);
    }

    @Test
    public void assert_nanosConvertsCorrectly() {
        final long startNanos = 1_000_000;
        final long endNanos = 5_000_000;
        final TimelineInterval interval = TimelineInterval.builder("interval", TimeUnit.NANOSECONDS).build(startNanos, endNanos);

        assertEquals(interval.getStartMillis(), startNanos / NANOS_IN_MILLISECOND);
        assertEquals(interval.getEndMillis(), endNanos / NANOS_IN_MILLISECOND);
    }

    @Test
    public void assert_secondsConvertsCorrectly() {
        final long startSeconds = 1;
        final long endSeconds = 5;
        final TimelineInterval interval = TimelineInterval.builder("interval", TimeUnit.SECONDS).build(startSeconds, endSeconds);

        assertEquals(interval.getStartMillis(), startSeconds * MILLIS_IN_SECOND);
        assertEquals(interval.getEndMillis(), endSeconds * MILLIS_IN_SECOND);
    }

    @Test
    public void assert_equalsIndependentOfUnits() {
        final long startSeconds = 1;
        final long endSeconds = 5;
        final long startMillis = startSeconds * MILLIS_IN_SECOND;
        final long endMillis = endSeconds * MILLIS_IN_SECOND;
        final TimelineInterval interval1 = TimelineInterval.builder("i", TimeUnit.SECONDS).build(startSeconds, endSeconds);
        final TimelineInterval interval2 = TimelineInterval.builder("i", TimeUnit.MILLISECONDS).build(startMillis, endMillis);

        assertEquals(interval1, interval2);
    }

    @Test
    public void assert_hashCodeIndependentOfUnits() {
        final long startSeconds = 1;
        final long endSeconds = 5;
        final long startMillis = startSeconds * MILLIS_IN_SECOND;
        final long endMillis = endSeconds * MILLIS_IN_SECOND;
        final TimelineInterval interval1 = TimelineInterval.builder("i", TimeUnit.SECONDS).build(startSeconds, endSeconds);
        final TimelineInterval interval2 = TimelineInterval.builder("i", TimeUnit.MILLISECONDS).build(startMillis, endMillis);

        assertEquals(interval1.hashCode(), interval2.hashCode());
    }

}
