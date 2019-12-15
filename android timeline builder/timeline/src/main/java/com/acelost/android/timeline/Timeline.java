package com.acelost.android.timeline;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.acelost.android.timeline.Preconditions.checkNotEmpty;
import static com.acelost.android.timeline.Preconditions.checkNotNull;

// threadsafe
public class Timeline {

    private final String title;
    private final Set<TimelineEvent> events;

    public Timeline(@NonNull final String title) {
        this.title = checkNotEmpty(title);
        this.events = new CopyOnWriteArraySet<>();
    }

    public void addEvent(@NonNull final TimelineEvent event) {
        checkNotNull(event);
        events.add(event);
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public Collection<TimelineEvent> getEvents() {
        return Collections.unmodifiableCollection(events);
    }

}
