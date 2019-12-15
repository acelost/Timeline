package com.acelost.android.timeline;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import static com.acelost.android.timeline.Preconditions.checkNotEmpty;
import static com.acelost.android.timeline.Preconditions.checkNotNull;

public class TimelineEvent {

    private final String name;
    private final TimeUnit units;
    private final long start;
    private final long end;

    public TimelineEvent(@NonNull final String name,
                         @NonNull final TimeUnit units,
                         final long start,
                         final long end) {
        this.name = checkNotEmpty(name);
        this.units = checkNotNull(units);
        this.start = start;
        this.end = end;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public TimeUnit getUnits() {
        return units;
    }

    public long getStartMillis() {
        return units.toMillis(start);
    }

    public long getEndMillis() {
        return units.toMillis(end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimelineEvent event = (TimelineEvent) o;

        if (getStartMillis() != event.getStartMillis()) return false;
        if (getEndMillis() != event.getEndMillis()) return false;
        return name.equals(event.name);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) (getStartMillis() ^ (getStartMillis() >>> 32));
        result = 31 * result + (int) (getEndMillis() ^ (getEndMillis() >>> 32));
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "TimelineEvent{" +
                "name='" + name + '\'' +
                ", units=" + units +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

}
