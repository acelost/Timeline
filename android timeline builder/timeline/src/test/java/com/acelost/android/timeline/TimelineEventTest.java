package com.acelost.android.timeline;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TimelineEventTest {

    private static final long NANOS_IN_MILLISECOND = 1_000_000;
    private static final long MILLIS_IN_SECOND = 1_000;

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfNameIsNull() {
        new TimelineEvent(null, TimeUnit.SECONDS, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfNameIsEmpty() {
        new TimelineEvent("", TimeUnit.SECONDS, 0, 0);
    }

    @Test(expected = NullPointerException.class)
    public void assert_throwsExceptionIfUnitsNotSpecified() {
        new TimelineEvent("event", null, 0, 0);
    }

    @Test
    public void assert_millisNotChanged() {
        final long startMillis = 1;
        final long endMillis = 5;
        final TimelineEvent event = new TimelineEvent("event", TimeUnit.MILLISECONDS, startMillis, endMillis);

        assertEquals(event.getStartMillis(), startMillis);
        assertEquals(event.getEndMillis(), endMillis);
    }

    @Test
    public void assert_nanosConvertsCorrectly() {
        final long startNanos = 1_000_000;
        final long endNanos = 5_000_000;
        final TimelineEvent event = new TimelineEvent("event", TimeUnit.NANOSECONDS, startNanos, endNanos);

        assertEquals(event.getStartMillis(), startNanos / NANOS_IN_MILLISECOND);
        assertEquals(event.getEndMillis(), endNanos / NANOS_IN_MILLISECOND);
    }

    @Test
    public void assert_secondsConvertsCorrectly() {
        final long startSeconds = 1;
        final long endSeconds = 5;
        final TimelineEvent event = new TimelineEvent("event", TimeUnit.SECONDS, startSeconds, endSeconds);

        assertEquals(event.getStartMillis(), startSeconds * MILLIS_IN_SECOND);
        assertEquals(event.getEndMillis(), endSeconds * MILLIS_IN_SECOND);
    }

    @Test
    public void assert_equalsIndependentOfUnits() {
        final long startSeconds = 1;
        final long endSeconds = 5;
        final long startMillis = startSeconds * MILLIS_IN_SECOND;
        final long endMillis = endSeconds * MILLIS_IN_SECOND;
        final TimelineEvent event1 = new TimelineEvent("e", TimeUnit.SECONDS, startSeconds, endSeconds);
        final TimelineEvent event2 = new TimelineEvent("e", TimeUnit.MILLISECONDS, startMillis, endMillis);

        assertEquals(event1, event2);
    }

}
