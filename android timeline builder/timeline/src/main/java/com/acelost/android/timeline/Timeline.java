package com.acelost.android.timeline;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.transform.TimelineTransformBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.acelost.android.timeline.Preconditions.checkNotEmpty;
import static com.acelost.android.timeline.Preconditions.checkNotNull;

// threadsafe
public final class Timeline {

    private final String title;
    private final TimelineKind kind;
    private final List<TimelineInterval> intervals;

    public Timeline(@NonNull final String title) {
        this(title, TimelineKind.ABSOLUTE);
    }

    public Timeline(@NonNull final String title, @NonNull final TimelineKind kind) {
        this.title = checkNotEmpty(title);
        this.kind = checkNotNull(kind);
        this.intervals = new CopyOnWriteArrayList<>();
    }

    public void addInterval(@NonNull final TimelineInterval interval) {
        checkNotNull(interval);
        this.intervals.add(interval);
    }

    public void addIntervals(@NonNull final Collection<TimelineInterval> intervals) {
        checkNotNull(intervals);
        this.intervals.addAll(intervals);
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public TimelineKind getKind() {
        return kind;
    }

    @NonNull
    public Collection<TimelineInterval> getIntervals() {
        return Collections.unmodifiableCollection(intervals);
    }

    @NonNull
    public TimelineTransformBuilder transform() {
        return new TimelineTransformBuilder(this);
    }

}
