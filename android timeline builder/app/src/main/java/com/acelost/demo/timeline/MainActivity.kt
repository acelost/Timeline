package com.acelost.demo.timeline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.acelost.android.timeline.TimelineInterval
import com.acelost.android.timeline.Timeline
import com.acelost.android.timeline.TimelineFormatter
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val timeline = Timeline("Simple Timeline")
        for (i in 0 until 40) {
            val name = "interval#${i%10}"
            timeline.addInterval(
                TimelineInterval.builder(name, TimeUnit.SECONDS)
                    .group("grp${name}")
                    //.payload("pld${name}x")
                    .build(i.toLong(), (i + 1).toLong())
            )
        }
        //TimelineFormatter.print(timeline, false)
        //TimelineFormatter.print(timeline, true)
        //TimelineHelper.share(this, transformed, "timeline-", null, null)
    }
}
