package com.acelost.android.timeline;

import androidx.annotation.NonNull;
import com.acelost.android.timeline.serialize.TimelineJsonSerializer;
import org.json.JSONObject;
import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class TimelineFormatter {

    @NonNull
    public static JSONObject toJson(@NonNull final Timeline timeline, final boolean compress) {
        checkNotNull(timeline);
        final TimelineJsonSerializer serializer = compress
                ? new TimelineJsonSerializer("n", "g", "s", "e", "c", "p", true, true)
                : new TimelineJsonSerializer("name", "group", "start", "end", "count", "payload", false, false);
        return serializer.serialize(timeline);
    }

    @NonNull
    public static String toJsonString(@NonNull final Timeline timeline, final boolean compress) {
        checkNotNull(timeline);
        return toJson(timeline, compress).toString();
    }

}
