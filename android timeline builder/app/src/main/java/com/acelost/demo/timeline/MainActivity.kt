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
        timeline.addEvent(TimelineEvent("event1", TimeUnit.SECONDS, 1, 5))
        timeline.addEvent(TimelineEvent("event2", TimeUnit.MILLISECONDS, 2100, 8000))
        timeline.addEvent(TimelineEvent("event3", TimeUnit.SECONDS, 9, 10))
        TimelineHelper.share(this, timeline, "timeline-", null, null)
    }
}
