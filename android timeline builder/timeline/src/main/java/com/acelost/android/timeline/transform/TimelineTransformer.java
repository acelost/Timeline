package com.acelost.android.timeline.transform;

import androidx.annotation.NonNull;

import com.acelost.android.timeline.TimelineInterval;

import java.util.List;

public interface TimelineTransformer {

    @NonNull
    List<TimelineInterval> transform(@NonNull List<TimelineInterval> intervals);

}
