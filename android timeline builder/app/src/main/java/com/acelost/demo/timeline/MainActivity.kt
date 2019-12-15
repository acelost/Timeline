package com.acelost.demo.timeline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.acelost.android.timeline.TimelineEvent
import com.acelost.android.timeline.Timeline
import com.acelost.android.timeline.TimelineHelper
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val timeline = Timeline("t")
        timeline.addEvent(TimelineEvent("e", TimeUnit.SECONDS, 1, 5))
        val json = TimelineHelper.toJson(timeline)
        val string = TimelineHelper.toJsonString(timeline)
        TimelineHelper.share(this, timeline, "timeline_", "messss", "hinnnt")
    }
}
