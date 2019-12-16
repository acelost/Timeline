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
    public void assert_timelineContainsEvents() {
        final TimelineEvent event = new TimelineEvent("event", TimeUnit.SECONDS, 1, 5);
        final Timeline timeline = new Timeline("timeline");

        timeline.addEvent(event);

        final Collection<TimelineEvent> events = timeline.getEvents();
        assertEquals(events.size(), 1);
        assertEquals(events.iterator().next(), event);
    }

    @Test
    public void assert_timelineContainsUniqueEvents() {
        final String eventName = "event";
        final long startSeconds = 1;
        final long endSeconds = 5;
        final TimelineEvent event1 = new TimelineEvent(eventName, TimeUnit.SECONDS, startSeconds, endSeconds);
        final TimelineEvent event2 = new TimelineEvent(eventName, TimeUnit.SECONDS, startSeconds, endSeconds);
        final Timeline timeline = new Timeline("timeline");

        timeline.addEvent(event1);
        timeline.addEvent(event2);

        final Collection<TimelineEvent> events = timeline.getEvents();
        assertEquals(events.size(), 1);
    }

    @Test
    public void assert_addEventCollectionWorksCorrectly() {
        final TimelineEvent event1 = new TimelineEvent("event1", TimeUnit.SECONDS, 0, 0);
        final TimelineEvent event2 = new TimelineEvent("event2", TimeUnit.SECONDS, 0, 0);
        final Timeline timeline = new Timeline("timeline");

        timeline.addEvents(Arrays.asList(event1, event2));

        final Collection<TimelineEvent> events = timeline.getEvents();
        assertEquals(events.size(), 2);
        assertTrue(events.contains(event1));
        assertTrue(events.contains(event2));
    }

}
