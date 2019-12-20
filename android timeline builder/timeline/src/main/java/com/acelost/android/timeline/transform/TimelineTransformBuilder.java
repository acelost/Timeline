package com.acelost.android.timeline.transform;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.Timeline;
import com.acelost.android.timeline.TimelineEvent;
import com.acelost.android.timeline.predicate.BiPredicate;
import com.acelost.android.timeline.predicate.EventProximityPredicate;
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
    public TimelineTransformBuilder filter(@NonNull final Predicate<TimelineEvent> predicate) {
        return compose(new FilterTransformer(checkNotNull(predicate)));
    }

    @NonNull
    public TimelineTransformBuilder filterMinDuration(final long duration, @NonNull final TimeUnit units) {
        return filter(new DurationPredicate(duration, checkNotNull(units)) {
            @Override
            protected boolean evaluate(long eventDuration, long conditionDuration) {
                return eventDuration >= conditionDuration;
            }
        });
    }

    @NonNull
    public TimelineTransformBuilder filterMaxDuration(final long duration, @NonNull final TimeUnit units) {
        return filter(new DurationPredicate(duration, checkNotNull(units)) {
            @Override
            protected boolean evaluate(long eventDuration, long conditionDuration) {
                return eventDuration <= conditionDuration;
            }
        });
    }

    @NonNull
    public TimelineTransformBuilder join(@NonNull final Predicate<String> namePredicate,
                                         @NonNull final BiPredicate<TimelineEvent, TimelineEvent> joinPredicate) {
        return compose(new JoinTransformer(namePredicate, joinPredicate));
    }

    @NonNull
    public TimelineTransformBuilder join(final long distance, @NonNull final TimeUnit units, final String... names) {
        return join(Predicates.in(names), new EventProximityPredicate(distance, units));
    }

    @NonNull
    public TimelineTransformBuilder join(final long distance, @NonNull final TimeUnit units) {
        return join(Predicates.<String>alwaysTrue(), new EventProximityPredicate(distance, checkNotNull(units)));
    }

    @NonNull
    public Timeline apply() {
        List<TimelineEvent> events = new ArrayList<>(timeline.getEvents());
        for (TimelineTransformer transformer : transformers) {
            events = transformer.transform(events);
        }
        final Timeline transformed = new Timeline(timeline.getTitle(), timeline.getKind());
        transformed.addEvents(events);
        return transformed;
    }

}
