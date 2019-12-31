package com.acelost.android.timeline.transformer;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineInterval;
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
        final JoinTransformer transformer = new JoinTransformer(
                Predicates.<String>alwaysTrue(),
                Predicates.<String>alwaysTrue(),
                new BiPredicate<TimelineInterval, TimelineInterval>() {
                    @Override
                    public boolean evaluate(@NonNull TimelineInterval input1, @NonNull TimelineInterval input2) {
                        return false;
                    }
                }
        );

        final List<TimelineInterval> intervals = Arrays.asList(
                TimelineInterval.builder("interval", TimeUnit.SECONDS).build(0, 1),
                TimelineInterval.builder("interval", TimeUnit.SECONDS).build(1, 2)
        );

        final List<TimelineInterval> joined = transformer.transform(intervals);

        for (TimelineInterval interval : intervals) {
            assertTrue(joined.contains(interval));
        }
    }

    @Test
    public void assert_returnsSingleItemIfJoinPredicateIsAlwaysTrue() {
        final JoinTransformer transformer = new JoinTransformer(
                Predicates.<String>alwaysTrue(),
                Predicates.<String>alwaysTrue(),
                new BiPredicate<TimelineInterval, TimelineInterval>() {
                    @Override
                    public boolean evaluate(@NonNull TimelineInterval input1, @NonNull TimelineInterval input2) {
                        return true;
                    }
                }
        );

        final long start = 0;
        final long end = 10;
        final TimeUnit units = TimeUnit.MILLISECONDS;
        final List<TimelineInterval> intervals = Arrays.asList(
                TimelineInterval.builder("interval", units).build(start, 1),
                TimelineInterval.builder("interval", units).build(3, 4),
                TimelineInterval.builder("interval", units).build(5, 7),
                TimelineInterval.builder("interval", units).build(9, end)
        );

        final List<TimelineInterval> joined = transformer.transform(intervals);

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
                Predicates.<String>alwaysTrue(),
                new BiPredicate<TimelineInterval, TimelineInterval>() {
                    @Override
                    public boolean evaluate(@NonNull TimelineInterval input1, @NonNull TimelineInterval input2) {
                        return true;
                    }
                }
        );

        final TimeUnit units = TimeUnit.MILLISECONDS;
        final List<TimelineInterval> intervals = Arrays.asList(
                TimelineInterval.builder("interval", units).build(0, 1),
                TimelineInterval.builder("interval", units).build(1, 2)
        );

        final List<TimelineInterval> joined = transformer.transform(intervals);

        for (TimelineInterval interval : intervals) {
            assertTrue(joined.contains(interval));
        }
    }

    @Test
    public void assert_returnsSameItemsIfGroupPredicateAlwaysFalse() {
        final JoinTransformer transformer = new JoinTransformer(
                Predicates.<String>alwaysTrue(),
                new Predicate<String>() {
                    @Override
                    public boolean evaluate(@NonNull String input) {
                        return false;
                    }
                },
                new BiPredicate<TimelineInterval, TimelineInterval>() {
                    @Override
                    public boolean evaluate(@NonNull TimelineInterval input1, @NonNull TimelineInterval input2) {
                        return true;
                    }
                }
        );

        final TimeUnit units = TimeUnit.MILLISECONDS;
        final List<TimelineInterval> intervals = Arrays.asList(
                TimelineInterval.builder("interval", units).group("group").build(0, 1),
                TimelineInterval.builder("interval", units).group("group").build(1, 2)
        );

        final List<TimelineInterval> joined = transformer.transform(intervals);

        for (TimelineInterval interval : intervals) {
            assertTrue(joined.contains(interval));
        }
    }

}
