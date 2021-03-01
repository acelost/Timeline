package com.acelost.android.timeline.serialize;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acelost.android.timeline.Timeline;
import com.acelost.android.timeline.TimelineInterval;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.acelost.android.timeline.Preconditions.checkNotNull;
import static com.acelost.android.timeline.Preconditions.checkUnique;

public final class TimelineJsonSerializer {

    private final String nameKey;
    private final String groupKey;
    private final String startKey;
    private final String endKey;
    private final String countKey;
    private final String payloadKey;
    private final boolean compressIntervalIdentifiers;
    private final boolean compressIntervalTimers;

    public TimelineJsonSerializer(
            @NonNull final String nameKey,
            @NonNull final String groupKey,
            @NonNull final String startKey,
            @NonNull final String endKey,
            @NonNull final String countKey,
            @NonNull final String payloadKey,
            final boolean compressIntervalIdentifiers,
            final boolean compressIntervalTimers
    ) {
        checkUnique(nameKey, groupKey, startKey, endKey, countKey, payloadKey);
        this.nameKey = checkNotNull(nameKey);
        this.groupKey = checkNotNull(groupKey);
        this.startKey = checkNotNull(startKey);
        this.endKey = checkNotNull(endKey);
        this.countKey = checkNotNull(countKey);
        this.payloadKey = checkNotNull(payloadKey);
        this.compressIntervalIdentifiers = compressIntervalIdentifiers;
        this.compressIntervalTimers = compressIntervalTimers;
    }

    @NonNull
    public JSONObject serialize(@NonNull final Timeline timeline) {
        checkNotNull(timeline);
        final JSONObject metaJson = buildMetaJson(timeline);
        final JSONArray intervalsJson = new JSONArray();
        final JSONObject aliasesJson = new JSONObject();
        final AliasGenerator aliasGenerator = new AliasGenerator();
        final ValueEncoder valueEncoder = new ValueEncoder();
        if (compressIntervalTimers) {
            putSafe(metaJson, "valueEncodeRadix", valueEncoder.getEncodeRadix());
        }
        for (TimelineInterval interval : timeline.getIntervals()) {
            final JSONObject intervalJson = new JSONObject();
            if (compressIntervalIdentifiers) {
                final String intervalName = interval.getName();
                final String alias = aliasGenerator.getAlias(intervalName);
                putSafe(aliasesJson, alias, intervalName);
                putSafe(intervalJson, nameKey, alias);
            } else {
                putSafe(intervalJson, nameKey, interval.getName());
            }
            final String intervalGroup = interval.getGroup();
            if (intervalGroup != null) {
                if (compressIntervalIdentifiers) {
                    final String alias = aliasGenerator.getAlias(intervalGroup);
                    putSafe(aliasesJson, alias, intervalGroup);
                    putSafe(intervalJson, groupKey, alias);
                } else {
                    putSafe(intervalJson, groupKey, intervalGroup);
                }
            }
            if (compressIntervalTimers) {
                putSafe(intervalJson, startKey, valueEncoder.encode(interval.getStartMillis()));
                putSafe(intervalJson, endKey, valueEncoder.encode(interval.getEndMillis()));
            } else {
                putSafe(intervalJson, startKey, interval.getStartMillis());
                putSafe(intervalJson, endKey, interval.getEndMillis());
            }
            final String payload = interval.getPayload();
            if (payload != null) {
                putSafe(intervalJson, payloadKey, payload);
            }
            final int count = interval.getCount();
            if (count > 1) {
                putSafe(intervalJson, countKey, count);
            }
            intervalsJson.put(intervalJson);
        }
        final JSONObject timelineJson = new JSONObject();
        putSafe(timelineJson, "meta", metaJson);
        putSafe(timelineJson, "intervals", intervalsJson);
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
        putSafe(meta, "splitSameNamed", timeline.isSplitSameNamed());
        putSafe(meta, "nameKey", nameKey);
        putSafe(meta, "groupKey", groupKey);
        putSafe(meta, "startKey", startKey);
        putSafe(meta, "endKey", endKey);
        putSafe(meta, "countKey", countKey);
        putSafe(meta, "payloadKey", payloadKey);
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

    private static final class AliasGenerator {

        private static final int ALIAS_RADIX = 36;

        private final Map<String, Integer> aliases = new HashMap<>();

        private int nextAlias = 0;

        @NonNull
        String getAlias(@NonNull final String string) {
            Integer alias = aliases.get(string);
            if (alias == null) {
                alias = nextAlias++;
                aliases.put(string, alias);
            }
            return Integer.toString(alias, ALIAS_RADIX);
        }

    }

    private static final class ValueEncoder {

        private static final int ENCODE_RADIX = 36;

        @NonNull
        String encode(final long value) {
            return Long.toString(value, ENCODE_RADIX);
        }

        int getEncodeRadix() {
            return ENCODE_RADIX;
        }

    }

}
