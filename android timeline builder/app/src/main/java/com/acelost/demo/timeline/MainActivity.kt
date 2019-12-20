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
        val timeline = Timeline("Simple Timeline")
        for (i in 0 until 20) {
            val name = "event#${i%10}"
            timeline.addEvent(TimelineEvent(name, "pld${name}x", TimeUnit.SECONDS, i.toLong(), (i + 1).toLong()))
        }
        TimelineHelper.print(timeline, false)
        TimelineHelper.print(timeline, true)
        //TimelineHelper.share(this, transformed, "timeline-", null, null)
    }
}
