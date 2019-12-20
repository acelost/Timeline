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
    private final List<TimelineEvent> events;

    public Timeline(@NonNull final String title) {
        this(title, TimelineKind.ABSOLUTE);
    }

    public Timeline(@NonNull final String title, @NonNull final TimelineKind kind) {
        this.title = checkNotEmpty(title);
        this.kind = checkNotNull(kind);
        this.events = new CopyOnWriteArrayList<>();
    }

    public void addEvent(@NonNull final TimelineEvent event) {
        checkNotNull(event);
        this.events.add(event);
    }

    public void addEvents(@NonNull final Collection<TimelineEvent> events) {
        checkNotNull(events);
        this.events.addAll(events);
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
    public Collection<TimelineEvent> getEvents() {
        return Collections.unmodifiableCollection(events);
    }

    @NonNull
    public TimelineTransformBuilder transform() {
        return new TimelineTransformBuilder(this);
    }

}
