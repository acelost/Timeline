package com.acelost.android.timeline.transformer;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineEvent;
import com.acelost.android.timeline.predicate.Predicate;
import com.acelost.android.timeline.transform.FilterTransformer;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class FilterTransformerTest {

    @Test(expected = NullPointerException.class)
    public void assert_throwsExceptionIfPredicateIsNull() {
        new FilterTransformer(null);
    }

    @Test
    public void assert_returnsSameItemsIfPredicateAlwaysTrue() {
        final FilterTransformer transformer = new FilterTransformer(new Predicate<TimelineEvent>() {
            @Override
            public boolean evaluate(@NonNull TimelineEvent input) {
                return true;
            }
        });

        final List<TimelineEvent> events = Arrays.asList(
                new TimelineEvent("event", TimeUnit.SECONDS, 0, 1),
                new TimelineEvent("event", TimeUnit.SECONDS, 1, 2)
        );

        final List<TimelineEvent> filtered = transformer.transform(events);

        for (TimelineEvent event : events) {
            assertTrue(filtered.contains(event));
        }
    }

    @Test
    public void assert_returnsEmptyListIfPredicateAlwaysFalse() {
        final FilterTransformer transformer = new FilterTransformer(new Predicate<TimelineEvent>() {
            @Override
            public boolean evaluate(@NonNull TimelineEvent input) {
                return false;
            }
        });

        final List<TimelineEvent> events = Arrays.asList(
                new TimelineEvent("event", TimeUnit.SECONDS, 0, 1),
                new TimelineEvent("event", TimeUnit.SECONDS, 1, 2)
        );

        final List<TimelineEvent> filtered = transformer.transform(events);

        assertTrue(filtered.isEmpty());
    }

}
