package com.acelost.android.timeline.transform;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineEvent;

import java.util.List;

public interface TimelineTransformer {

    @NonNull
    List<TimelineEvent> transform(@NonNull List<TimelineEvent> events);

}
