package com.acelost.android.timeline.transform;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.Timeline;
import com.acelost.android.timeline.TimelineInterval;
import com.acelost.android.timeline.predicate.BiPredicate;
import com.acelost.android.timeline.predicate.IntervalProximityPredicate;
import com.acelost.android.timeline.predicate.DurationPredicate;
import com.acelost.android.timeline.predicate.Predicate;
import com.acelost.android.timeline.predicate.Predicates;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class TimelineTransformBuilder {

    private final Timeline timeline;
    private final List<TimelineTransformer> transformers;

    public TimelineTransformBuilder(@NonNull final Timeline timeline) {
        this.timeline = checkNotNull(timeline);
        this.transformers = new ArrayList<>();
    }

    @NonNull
    public TimelineTransformBuilder compose(@NonNull final TimelineTransformer transformer) {
        transformers.add(checkNotNull(transformer));
        return this;
    }

    @NonNull
    public TimelineTransformBuilder filter(@NonNull final Predicate<TimelineInterval> predicate) {
        return compose(new FilterTransformer(checkNotNull(predicate)));
    }

    @NonNull
    public TimelineTransformBuilder filterMinDuration(final long duration, @NonNull final TimeUnit units) {
        return filter(new DurationPredicate(duration, checkNotNull(units)) {
            @Override
            protected boolean evaluate(long intervalDuration, long conditionDuration) {
                return intervalDuration >= conditionDuration;
            }
        });
    }

    @NonNull
    public TimelineTransformBuilder filterMaxDuration(final long duration, @NonNull final TimeUnit units) {
        return filter(new DurationPredicate(duration, checkNotNull(units)) {
            @Override
            protected boolean evaluate(long intervalDuration, long conditionDuration) {
                return intervalDuration <= conditionDuration;
            }
        });
    }

    @NonNull
    public TimelineTransformBuilder join(@NonNull final Predicate<String> namePredicate,
                                         @NonNull final Predicate<String> groupPredicate,
                                         @NonNull final BiPredicate<TimelineInterval, TimelineInterval> joinPredicate) {
        return compose(new JoinTransformer(namePredicate, groupPredicate, joinPredicate));
    }

    @NonNull
    public TimelineTransformBuilder join(final long distance, @NonNull final TimeUnit units) {
        return join(Predicates.<String>alwaysTrue(), Predicates.<String>alwaysTrue(), new IntervalProximityPredicate(distance, checkNotNull(units)));
    }

    @NonNull
    public TimelineTransformBuilder joinForNames(final long distance, @NonNull final TimeUnit units, @NonNull final String... names) {
        return join(Predicates.in(names), Predicates.<String>alwaysTrue(), new IntervalProximityPredicate(distance, units));
    }

    @NonNull
    public TimelineTransformBuilder joinForGroups(final long distance, @NonNull final TimeUnit units, @NonNull final String... groups) {
        return join(Predicates.<String>alwaysTrue(), Predicates.in(groups), new IntervalProximityPredicate(distance, units));
    }

    @NonNull
    public Timeline apply() {
        List<TimelineInterval> intervals = new ArrayList<>(timeline.getIntervals());
        for (TimelineTransformer transformer : transformers) {
            intervals = transformer.transform(intervals);
        }
        final Timeline transformed = new Timeline(timeline.getTitle(), timeline.getKind());
        transformed.addIntervals(intervals);
        return transformed;
    }

}
