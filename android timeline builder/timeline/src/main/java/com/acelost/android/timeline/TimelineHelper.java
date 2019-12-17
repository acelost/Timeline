package com.acelost.android.timeline;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.acelost.android.timeline.Preconditions.checkAtLeast;
import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class TimelineHelper {

    private static final String KEY_EVENT_NAME = "name";
    private static final String KEY_EVENT_START = "startMs";
    private static final String KEY_EVENT_END = "endMs";

    @NonNull
    public static JSONObject toJson(@NonNull final Timeline timeline) {
        checkNotNull(timeline);
        final JSONObject metaJson = new JSONObject();
        putSafe(metaJson, "title", timeline.getTitle());
        putSafe(metaJson, "kind", timeline.getKind().name());
        putSafe(metaJson, "units", "ms");
        putSafe(metaJson, "nameKey", KEY_EVENT_NAME);
        putSafe(metaJson, "startKey", KEY_EVENT_START);
        putSafe(metaJson, "endKey", KEY_EVENT_END);
        final JSONArray eventsJson = new JSONArray();
        for (TimelineEvent event : timeline.getEvents()) {
            final JSONObject eventJson = new JSONObject();
            putSafe(eventJson, KEY_EVENT_NAME, event.getName());
            putSafe(eventJson, KEY_EVENT_START, event.getStartMillis());
            putSafe(eventJson, KEY_EVENT_END, event.getEndMillis());
            eventsJson.put(eventJson);
        }
        final JSONObject timelineJson = new JSONObject();
        putSafe(timelineJson, "meta", metaJson);
        putSafe(timelineJson, "events", eventsJson);
        return timelineJson;
    }

    @NonNull
    public static String toJsonString(@NonNull final Timeline timeline) {
        checkNotNull(timeline);
        return toJson(timeline).toString();
    }

    public static void print(@NonNull final Timeline timeline) {
        checkNotNull(timeline);
        Log.i("TimelineLog", toJsonString(timeline));
    }

    @NonNull
    public static File toTempFile(@NonNull final Timeline timeline,
                                  @NonNull final String prefix,
                                  @Nullable final String suffix,
                                  @NonNull final File directory) throws IOException {
        checkNotNull(timeline);
        checkAtLeast(prefix, 3);
        final String timelineString = toJsonString(timeline);
        final File tempFile = File.createTempFile(prefix, suffix, directory);
        final OutputStream outputStream = new FileOutputStream(tempFile, false);
        try (final Writer writer = new OutputStreamWriter(outputStream)) {
            writer.write(timelineString);
        }
        return tempFile;
    }

    @NonNull
    public static Intent toShareIntent(@NonNull final Context context,
                                       @NonNull final Timeline timeline,
                                       @NonNull final String filePrefix,
                                       @Nullable final String messageText,
                                       @Nullable final String chooserTitle) throws IOException {
        checkNotNull(context);
        checkNotNull(timeline);
        final File directory = checkNotNull(context.getExternalCacheDir());
        final File file = toTempFile(timeline, filePrefix, ".json", directory);
        final Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        final Intent dataIntent = new Intent(Intent.ACTION_SEND);
        dataIntent.setType("text/*");
        dataIntent.putExtra(Intent.EXTRA_STREAM, uri);
        if (!TextUtils.isEmpty(messageText)) {
            dataIntent.putExtra(Intent.EXTRA_TEXT, messageText);
        }
        return Intent.createChooser(dataIntent, chooserTitle);
    }

    public static boolean share(@NonNull final Context context,
                         @NonNull final Timeline timeline,
                         @NonNull final String filePrefix,
                         @Nullable final String messageText,
                         @Nullable final String chooserTitle) {
        try {
            final Intent shareIntent = toShareIntent(context, timeline, filePrefix, messageText, chooserTitle);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(shareIntent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
