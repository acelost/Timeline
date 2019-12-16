# Timeline

[![Bintray][bintraybadge-svg]][bintray-android-builder]

`Timeline` is a tool for building and viewing time range charts.

# Show me!

`Timeline` uses simple json format for describing time range charts:

```json
// timeline-example.json
{
  "meta": {
    "title": "My first timeline"
  },
  "events": [
    {
      "name": "Event1",
      "startMs": 1000,
      "endMs": 2000
    },
    {
      "name": "Event2",
      "startMs": 500,
      "endMs": 2500
    },
    {
      "name": "Event3",
      "startMs": 1000,
      "endMs": 1500
    },
    {
      "name": "Event1",
      "startMs": 2100,
      "endMs": 2400
    }
  ]
}
```

Resulting timeline looks so:

<img src="example.png" width="760">

## How to use

1. Download <a src="https://minhaskamal.github.io/DownGit/#/home?url=https://github.com/acelost/Timeline/tree/master/html%20timeline%20viewer">timeline html viewer</a>
2. Open `timeline.html` file in your browser
3. Put file with timeline on page

## Builder for Android

Use simple `Timeline` builder in your android application:

```java
public Timeline buildTimeline() {
    Timeline timeline = new Timeline("my first timeline");
    timeline.addEvent(new TimelineEvent("e1", TimeUnit.SECONDS, 0, 1));
    timeline.addEvent(new TimelineEvent("e2", TimeUnit.SECONDS, 1, 4));
    return timeline;
}
```

You can format `Timeline` as json or string:

```java
public void printTimeline(Timeline timeline) {
    JSONObject json = TimelineHelper.toJson(timeline);
    Log.i("MyTimeline", json.toString());
}
```

Also you can share `Timeline` with share intent:

```java
public void shareTimeline(Timeline timeline) {
    TimelineHelper.share(
            context, 
            timeline, 
            "timeline_", // file prefix
            "Look at my first timeline!", // message for recipient
            "Choose recipient for your timeline." // title for android system chooser
    );
}
```

For integrating `android timeline builder` just add this dependency to your gradle script:

```groovy
implementation 'com.acelost.timeline:timeline-builder:0.0.3'
```

## License

    Copyright 2019 The Spectrum Author

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
[bintray-android-builder]: https://bintray.com/acelost/Timeline/timeline-builder
[bintraybadge-svg]: https://img.shields.io/bintray/v/acelost/Timeline/timeline-builder.svg
