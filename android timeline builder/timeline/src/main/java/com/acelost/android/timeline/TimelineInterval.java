package com.acelost.android.timeline;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import static com.acelost.android.timeline.Preconditions.checkIsPositive;
import static com.acelost.android.timeline.Preconditions.checkNotEmpty;
import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class TimelineInterval {

    private final String name;
    private final String group;
    private final String payload;
    private final TimeUnit units;
    private final long start;
    private final long end;
    private final int count;

    @NonNull
    public static Builder builder(@NonNull final String name, @NonNull final TimeUnit units) {
        return new Builder(name, units);
    }

    public TimelineInterval(@NonNull final String name,
                            @Nullable final String group,
                            @Nullable final String payload,
                            @NonNull final TimeUnit units,
                            final long start,
                            final long end,
                            final int count) {
        this.name = checkNotEmpty(name);
        this.group = group;
        this.payload = payload;
        this.units = checkNotNull(units);
        this.start = start;
        this.end = end;
        this.count = checkIsPositive(count);
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getGroup() {
        return group;
    }

    @Nullable
    public String getPayload() {
        return payload;
    }

    @NonNull
    public TimeUnit getUnits() {
        return units;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getStartMillis() {
        return units.toMillis(start);
    }

    public long getEndMillis() {
        return units.toMillis(end);
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimelineInterval interval = (TimelineInterval) o;

        if (getStartMillis() != interval.getStartMillis()) return false;
        if (getEndMillis() != interval.getEndMillis()) return false;
        if (getCount() != interval.getCount()) return false;
        if (group != null ? !group.equals(interval.group) : interval.group != null) return false;
        if (payload != null ? !payload.equals(interval.payload) : interval.payload != null) return false;
        return name.equals(interval.name);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        result = 31 * result + (int) (getStartMillis() ^ (getStartMillis() >>> 32));
        result = 31 * result + (int) (getEndMillis() ^ (getEndMillis() >>> 32));
        result = 31 * result + count;
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "TimelineInterval{" +
                "name='" + name + '\'' +
                ", group=" + group +
                ", payload=" + payload +
                ", units=" + units +
                ", start=" + start +
                ", end=" + end +
                ", count=" + count +
                '}';
    }

    public static final class Builder {

        @NonNull
        private final String name;

        @NonNull
        private final TimeUnit units;

        @Nullable
        private String group;

        @Nullable
        private String payload;

        private int count = 1;

        private Builder(@NonNull final String name, @NonNull final TimeUnit units) {
            this.name = checkNotNull(name);
            this.units = checkNotNull(units);
        }

        @CheckResult
        @NonNull
        public Builder group(@Nullable final String group) {
            this.group = group;
            return this;
        }

        @CheckResult
        @NonNull
        public Builder payload(@Nullable final String payload) {
            this.payload = payload;
            return this;
        }

        @CheckResult
        @NonNull
        public Builder count(final int count) {
            this.count = checkIsPositive(count);
            return this;
        }

        @NonNull
        public TimelineInterval build(final long start, final long end) {
            return new TimelineInterval(name, group, payload, units, start, end, count);
        }

    }

}
