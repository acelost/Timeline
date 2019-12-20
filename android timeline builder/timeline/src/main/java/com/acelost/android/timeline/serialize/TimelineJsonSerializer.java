package com.acelost.android.timeline.serialize;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acelost.android.timeline.Timeline;
import com.acelost.android.timeline.TimelineEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.acelost.android.timeline.Preconditions.checkNotNull;
import static com.acelost.android.timeline.Preconditions.checkUnique;

public final class TimelineJsonSerializer {

    private static final int ALIAS_RADIX = 36;
    private static final int VALUE_RADIX = 36;

    private final String nameKey;
    private final String startKey;
    private final String endKey;
    private final String countKey;
    private final String payloadKey;
    private final boolean compressEventNames;
    private final boolean compressEventTimers;

    public TimelineJsonSerializer(
            @NonNull final String nameKey,
            @NonNull final String startKey,
            @NonNull final String endKey,
            @NonNull final String countKey,
            @NonNull final String payloadKey,
            final boolean compressEventNames,
            final boolean compressEventTimers
    ) {
        checkUnique(nameKey, startKey, endKey, countKey, payloadKey);
        this.nameKey = checkNotNull(nameKey);
        this.startKey = checkNotNull(startKey);
        this.endKey = checkNotNull(endKey);
        this.countKey = checkNotNull(countKey);
        this.payloadKey = checkNotNull(payloadKey);
        this.compressEventNames = compressEventNames;
        this.compressEventTimers = compressEventTimers;
    }

    @NonNull
    public JSONObject serialize(@NonNull final Timeline timeline) {
        checkNotNull(timeline);
        final JSONObject metaJson = buildMetaJson(timeline);
        final JSONArray eventsJson = new JSONArray();
        final JSONObject aliasesJson = new JSONObject();
        final Map<String, Integer> aliases = new HashMap<>();
        int alias = 0;
        for (TimelineEvent event : timeline.getEvents()) {
            final JSONObject eventJson = new JSONObject();
            if (compressEventNames) {
                final String eventName = event.getName();
                Integer eventAlias = aliases.get(eventName);
                if (eventAlias == null) {
                    eventAlias = alias++;
                    aliases.put(eventName, eventAlias);
                }
                final String aliasString = Integer.toString(eventAlias, ALIAS_RADIX);
                putSafe(aliasesJson, aliasString, eventName);
                putSafe(eventJson, nameKey, aliasString);
            } else {
                putSafe(eventJson, nameKey, event.getName());
            }
            if (compressEventTimers) {
                putSafe(eventJson, startKey, Long.toString(event.getStartMillis(), VALUE_RADIX));
                putSafe(eventJson, endKey, Long.toString(event.getEndMillis(), VALUE_RADIX));
            } else {
                putSafe(eventJson, startKey, event.getStartMillis());
                putSafe(eventJson, endKey, event.getEndMillis());
            }
            final String payload = event.getPayload();
            if (payload != null) {
                putSafe(eventJson, payloadKey, payload);
            }
            final int count = event.getCount();
            if (count > 1) {
                putSafe(eventJson, countKey, count);
            }
            eventsJson.put(eventJson);
        }
        final JSONObject timelineJson = new JSONObject();
        putSafe(timelineJson, "meta", metaJson);
        putSafe(timelineJson, "events", eventsJson);
        if (aliasesJson.length() > 0) {
            putSafe(timelineJson, "aliases", aliasesJson);
        }
        return timelineJson;
    }

    @NonNull
    private JSONObject buildMetaJson(@NonNull final Timeline timeline) {
        final JSONObject meta = new JSONObject();
        putSafe(meta, "title", timeline.getTitle());
        putSafe(meta, "kind", timeline.getKind().name());
        putSafe(meta, "units", "ms");
        putSafe(meta, "nameKey", nameKey);
        putSafe(meta, "startKey", startKey);
        putSafe(meta, "endKey", endKey);
        putSafe(meta, "countKey", countKey);
        putSafe(meta, "payloadKey", payloadKey);
        if (compressEventTimers) {
            putSafe(meta, "valueEncodeRadix", VALUE_RADIX);
        }
        return meta;
    }

    @NonNull
    private static JSONObject putSafe(@NonNull final JSONObject object,
                                      @NonNull final String key,
                                      @Nullable final Object value) {
        try {
            return object.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

}
