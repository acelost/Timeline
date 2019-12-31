package com.acelost.android.timeline.transformer;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineInterval;
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
        final FilterTransformer transformer = new FilterTransformer(new Predicate<TimelineInterval>() {
            @Override
            public boolean evaluate(@NonNull TimelineInterval input) {
                return true;
            }
        });

        final List<TimelineInterval> intervals = Arrays.asList(
                TimelineInterval.builder("interval", TimeUnit.SECONDS).build(0, 1),
                TimelineInterval.builder("interval", TimeUnit.SECONDS).build(1, 2)
        );

        final List<TimelineInterval> filtered = transformer.transform(intervals);

        for (TimelineInterval interval : intervals) {
            assertTrue(filtered.contains(interval));
        }
    }

    @Test
    public void assert_returnsEmptyListIfPredicateAlwaysFalse() {
        final FilterTransformer transformer = new FilterTransformer(new Predicate<TimelineInterval>() {
            @Override
            public boolean evaluate(@NonNull TimelineInterval input) {
                return false;
            }
        });

        final List<TimelineInterval> intervals = Arrays.asList(
                TimelineInterval.builder("interval", TimeUnit.SECONDS).build(0, 1),
                TimelineInterval.builder("interval", TimeUnit.SECONDS).build(1, 2)
        );

        final List<TimelineInterval> filtered = transformer.transform(intervals);

        assertTrue(filtered.isEmpty());
    }

}
