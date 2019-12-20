package com.acelost.android.timeline.transform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acelost.android.timeline.TimelineEvent;
import com.acelost.android.timeline.predicate.BiPredicate;
import com.acelost.android.timeline.predicate.Predicate;
import com.acelost.android.timeline.util.TimeUnitUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class JoinTransformer implements TimelineTransformer {

    private final Predicate<String> namePredicate;
    private final BiPredicate<TimelineEvent, TimelineEvent> joinPredicate;

    public JoinTransformer(@NonNull final Predicate<String> namePredicate,
                           @NonNull final BiPredicate<TimelineEvent, TimelineEvent> joinPredicate) {
        this.namePredicate = checkNotNull(namePredicate);
        this.joinPredicate = checkNotNull(joinPredicate);
    }

    @NonNull
    @Override
    public List<TimelineEvent> transform(@NonNull final List<TimelineEvent> events) {
        final Map<String, ArrayList<TimelineEvent>> groups = groupByName(events);
        final List<TimelineEvent> result = new ArrayList<>();
        for (Map.Entry<String, ArrayList<TimelineEvent>> group : groups.entrySet()) {
            if (namePredicate.evaluate(group.getKey())) {
                final List<TimelineEvent> joined = join(group.getValue());
                result.addAll(joined);
            } else {
                result.addAll(group.getValue());
            }
        }
        return result;
    }

    @NonNull
    private Map<String, ArrayList<TimelineEvent>> groupByName(@NonNull final List<TimelineEvent> events) {
        final Map<String, ArrayList<TimelineEvent>> groups = new HashMap<>();
        for (TimelineEvent event : events) {
            final String name = event.getName();
            ArrayList<TimelineEvent> group = groups.get(name);
            if (group == null) {
                group = new ArrayList<>();
                groups.put(name, group);
            }
            group.add(event);
        }
        return groups;
    }

    private boolean shouldBeJoined(@NonNull final TimelineEvent e1, @NonNull final TimelineEvent e2) {
        return joinPredicate.evaluate(e1, e2);
    }

    @NonNull
    private List<TimelineEvent> join(@NonNull final ArrayList<TimelineEvent> events) {
        final List<TimelineEvent> joined = new ArrayList<>();
        do {
            joined.clear();
            for (int i = 0; i < events.size() - 1; i++) {
                final TimelineEvent event1 = events.get(i);
                if (event1 == null) continue;
                for (int j = i + 1; j < events.size(); j++) {
                    final TimelineEvent event2 = events.get(j);
                    if (event2 == null) continue;
                    if (shouldBeJoined(event1, event2)) {
                        joined.add(join(event1, event2));
                        events.set(i, null);
                        events.set(j, null);
                        i++;
                    }
                }
            }
            clearNulls(events);
            events.addAll(joined);
        } while (!joined.isEmpty());
        return events;
    }

    @NonNull
    private TimelineEvent join(@NonNull final TimelineEvent e1, @NonNull final TimelineEvent e2) {
        if (!e1.getName().equals(e2.getName())) {
            throw new IllegalArgumentException("Attempt to join events with different names.");
        }
        final TimeUnit e1Units = e1.getUnits();
        final TimeUnit e2Units = e2.getUnits();
        final TimeUnit newUnits = TimeUnitUtil.minUnit(e1Units, e2Units);
        final long e1Start = newUnits.convert(e1.getStart(), e1Units);
        final long e1End = newUnits.convert(e1.getEnd(), e1Units);
        final long e2Start = newUnits.convert(e2.getStart(), e2Units);
        final long e2End = newUnits.convert(e2.getEnd(), e2Units);
        final long newStart = Math.min(e1Start, e2Start);
        final long newEnd = Math.max(e1End, e2End);
        final int newCount = e1.getCount() + e2.getCount();
        final String newPayload = joinStrings(e1.getPayload(), e2.getPayload(), ";");
        return new TimelineEvent(e1.getName(), newPayload, newUnits, newStart, newEnd, newCount);
    }

    private void clearNulls(@NonNull final ArrayList<?> list) {
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
            }
        }
    }

    @Nullable
    private String joinStrings(@Nullable final String s1,
                               @Nullable final String s2,
                               @NonNull final String separator) {
        if (s1 == null) {
            return s2;
        } else if (s2 == null) {
            return s1;
        }
        return s1 + separator + s2;
    }

}
