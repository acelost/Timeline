package com.acelost.android.timeline.transform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acelost.android.timeline.TimelineInterval;
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
    private final Predicate<String> groupPredicate;
    private final BiPredicate<TimelineInterval, TimelineInterval> joinPredicate;

    public JoinTransformer(@NonNull final Predicate<String> namePredicate,
                           @NonNull final Predicate<String> groupPredicate,
                           @NonNull final BiPredicate<TimelineInterval, TimelineInterval> joinPredicate) {
        this.namePredicate = checkNotNull(namePredicate);
        this.groupPredicate = checkNotNull(groupPredicate);
        this.joinPredicate = checkNotNull(joinPredicate);
    }

    @NonNull
    @Override
    public List<TimelineInterval> transform(@NonNull final List<TimelineInterval> intervals) {
        final Map<GroupKey, ArrayList<TimelineInterval>> groups = groupByPrimaryAttributes(intervals);
        final List<TimelineInterval> result = new ArrayList<>();
        for (Map.Entry<GroupKey, ArrayList<TimelineInterval>> group : groups.entrySet()) {
            final boolean canJoin = canJoin(group.getKey());
            if (canJoin) {
                final List<TimelineInterval> joined = join(group.getValue());
                result.addAll(joined);
            } else {
                result.addAll(group.getValue());
            }
        }
        return result;
    }

    @NonNull
    private Map<GroupKey, ArrayList<TimelineInterval>> groupByPrimaryAttributes(@NonNull final List<TimelineInterval> intervals) {
        final Map<GroupKey, ArrayList<TimelineInterval>> groups = new HashMap<>();
        for (TimelineInterval interval : intervals) {
            final GroupKey groupKey = getPrimaryKey(interval);
            ArrayList<TimelineInterval> group = groups.get(groupKey);
            if (group == null) {
                group = new ArrayList<>();
                groups.put(groupKey, group);
            }
            group.add(interval);
        }
        return groups;
    }

    @NonNull
    private GroupKey getPrimaryKey(@NonNull final TimelineInterval interval) {
        final String group = interval.getGroup();
        if (group != null) {
            return new GroupKey(group, GroupKey.Attribute.GROUP);
        }
        return new GroupKey(interval.getName(), GroupKey.Attribute.NAME);
    }

    private boolean canJoin(@NonNull final GroupKey key) {
        switch (key.attribute) {
            case NAME:
                return namePredicate.evaluate(key.value);
            case GROUP:
                return groupPredicate.evaluate(key.value);
        }
        return false;
    }

    private boolean shouldBeJoined(@NonNull final TimelineInterval i1, @NonNull final TimelineInterval i2) {
        return joinPredicate.evaluate(i1, i2);
    }

    @NonNull
    private List<TimelineInterval> join(@NonNull final ArrayList<TimelineInterval> intervals) {
        final List<TimelineInterval> joined = new ArrayList<>();
        do {
            joined.clear();
            for (int i = 0; i < intervals.size() - 1; i++) {
                final TimelineInterval interval1 = intervals.get(i);
                if (interval1 == null) continue;
                for (int j = i + 1; j < intervals.size(); j++) {
                    final TimelineInterval interval2 = intervals.get(j);
                    if (interval2 == null) continue;
                    if (shouldBeJoined(interval1, interval2)) {
                        joined.add(join(interval1, interval2));
                        intervals.set(i, null);
                        intervals.set(j, null);
                        i++;
                    }
                }
            }
            clearNulls(intervals);
            intervals.addAll(joined);
        } while (!joined.isEmpty());
        return intervals;
    }

    @NonNull
    private TimelineInterval join(@NonNull final TimelineInterval i1, @NonNull final TimelineInterval i2) {
        if (!i1.getName().equals(i2.getName())) {
            throw new IllegalArgumentException("Attempt to join intervals with different names.");
        }
        final TimeUnit i1Units = i1.getUnits();
        final TimeUnit i2Units = i2.getUnits();
        final TimeUnit newUnits = TimeUnitUtil.minUnit(i1Units, i2Units);
        final long i1Start = newUnits.convert(i1.getStart(), i1Units);
        final long i1End = newUnits.convert(i1.getEnd(), i1Units);
        final long i2Start = newUnits.convert(i2.getStart(), i2Units);
        final long i2End = newUnits.convert(i2.getEnd(), i2Units);
        final long newStart = Math.min(i1Start, i2Start);
        final long newEnd = Math.max(i1End, i2End);
        final int newCount = i1.getCount() + i2.getCount();
        final String newPayload = joinStrings(i1.getPayload(), i2.getPayload(), ";");
        return new TimelineInterval(i1.getName(), i1.getGroup(), newPayload, newUnits, newStart, newEnd, newCount);
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

    private static class GroupKey {

        @NonNull
        final String value;

        @NonNull
        final Attribute attribute;

        GroupKey(@NonNull final String value, @NonNull final Attribute attribute) {
            this.value = checkNotNull(value);
            this.attribute = checkNotNull(attribute);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GroupKey groupKey = (GroupKey) o;

            if (!value.equals(groupKey.value)) return false;
            return attribute == groupKey.attribute;
        }

        @Override
        public int hashCode() {
            int result = value.hashCode();
            result = 31 * result + attribute.hashCode();
            return result;
        }

        private enum Attribute {
            NAME,
            GROUP
        }
    }

}
