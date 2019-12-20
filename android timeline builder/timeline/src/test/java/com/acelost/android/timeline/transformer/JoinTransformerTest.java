package com.acelost.android.timeline.transformer;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineEvent;
import com.acelost.android.timeline.predicate.BiPredicate;
import com.acelost.android.timeline.predicate.Predicate;
import com.acelost.android.timeline.predicate.Predicates;
import com.acelost.android.timeline.transform.JoinTransformer;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JoinTransformerTest {

    @Test
    public void assert_returnsSameItemsIfJoinPredicateIsAlwaysFalse() {
        final JoinTransformer transformer = new JoinTransformer(Predicates.<String>alwaysTrue(), new BiPredicate<TimelineEvent, TimelineEvent>() {
            @Override
            public boolean evaluate(@NonNull TimelineEvent input1, @NonNull TimelineEvent input2) {
                return false;
            }
        });

        final List<TimelineEvent> events = Arrays.asList(
                new TimelineEvent("event", TimeUnit.SECONDS, 0, 1),
                new TimelineEvent("event", TimeUnit.SECONDS, 1, 2)
        );

        final List<TimelineEvent> joined = transformer.transform(events);

        for (TimelineEvent event : events) {
            assertTrue(joined.contains(event));
        }
    }

    @Test
    public void assert_returnsSingleItemIfJoinPredicateIsAlwaysTrue() {
        final JoinTransformer transformer = new JoinTransformer(Predicates.<String>alwaysTrue(), new BiPredicate<TimelineEvent, TimelineEvent>() {
            @Override
            public boolean evaluate(@NonNull TimelineEvent input1, @NonNull TimelineEvent input2) {
                return true;
            }
        });

        final long start = 0;
        final long end = 10;
        final TimeUnit units = TimeUnit.MILLISECONDS;
        final List<TimelineEvent> events = Arrays.asList(
                new TimelineEvent("event", units, start, 1),
                new TimelineEvent("event", units, 3, 4),
                new TimelineEvent("event", units, 5, 7),
                new TimelineEvent("event", units, 9, end)
        );

        final List<TimelineEvent> joined = transformer.transform(events);

        assertEquals(joined.size(), 1);
        assertEquals(joined.get(0).getStartMillis(), start);
        assertEquals(joined.get(0).getEndMillis(), end);
    }

    @Test
    public void assert_returnsSameItemsIfNamePredicateAlwaysFalse() {
        final JoinTransformer transformer = new JoinTransformer(
                new Predicate<String>() {
                    @Override
                    public boolean evaluate(@NonNull String input) {
                        return false;
                    }
                },
                new BiPredicate<TimelineEvent, TimelineEvent>() {
                    @Override
                    public boolean evaluate(@NonNull TimelineEvent input1, @NonNull TimelineEvent input2) {
                        return true;
                    }
                }
        );

        final TimeUnit units = TimeUnit.MILLISECONDS;
        final List<TimelineEvent> events = Arrays.asList(
                new TimelineEvent("event", units, 0, 1),
                new TimelineEvent("event", units, 1, 2)
        );

        final List<TimelineEvent> joined = transformer.transform(events);

        for (TimelineEvent event : events) {
            assertTrue(joined.contains(event));
        }
    }

}
