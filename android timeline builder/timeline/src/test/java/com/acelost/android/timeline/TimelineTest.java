package com.acelost.android.timeline;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TimelineTest {

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfTitleIsNull() {
        new Timeline(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assert_throwsExceptionIfTitleIsEmpty() {
        new Timeline("");
    }

    @Test
    public void assert_timelineContainsIntervals() {
        final TimelineInterval interval = TimelineInterval.builder("interval", TimeUnit.SECONDS).build(1, 5);
        final Timeline timeline = new Timeline("timeline");

        timeline.addInterval(interval);

        final Collection<TimelineInterval> intervals = timeline.getIntervals();
        assertEquals(intervals.size(), 1);
        assertEquals(intervals.iterator().next(), interval);
    }

    @Test
    public void assert_addIntervalCollectionWorksCorrectly() {
        final TimelineInterval interval1 = TimelineInterval.builder("interval1", TimeUnit.SECONDS).build(0, 0);
        final TimelineInterval interval2 = TimelineInterval.builder("interval2", TimeUnit.SECONDS).build(0, 0);
        final Timeline timeline = new Timeline("timeline");

        timeline.addIntervals(Arrays.asList(interval1, interval2));

        final Collection<TimelineInterval> intervals = timeline.getIntervals();
        assertEquals(intervals.size(), 2);
        assertTrue(intervals.contains(interval1));
        assertTrue(intervals.contains(interval2));
    }

}
