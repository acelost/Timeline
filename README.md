# Timeline

[![Bintray][bintraybadge-svg]][bintray-android-builder]

`Timeline` is a tool for building and viewing time range charts.

# Input and output

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
      "start": 1000,
      "end": 2000
    },
    {
      "name": "Event2",
      "start": 500,
      "end": 2500
    },
    {
      "name": "Event3",
      "start": 1000,
      "end": 1500
    },
    {
      "name": "Event1",
      "start": 2100,
      "end": 2400
    }
  ]
}
```

Resulting timeline looks so:

<img src="timeline-example.png" width="1000">

## How to use

1. Download [timeline html viewer](https://minhaskamal.github.io/DownGit/#/home?url=https://github.com/acelost/Timeline/tree/master/html%20timeline%20viewer)
2. Open `timeline.html` file in your browser
3. Drop file with timeline on page or just copy timeline json and paste it (Ctrl+V)

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
implementation 'com.acelost.timeline:timeline-builder:0.0.5'
```

## Android Transform API

Transform API allows to prepare your timeline for rendering.

1. You can filter events in timeline:
```java
public Timeline prepare(Timeline timeline) {
    return timeline.transform()
            .filter(new MyPredicate())
            //.filterMinDuration(1, TimeUnit.SECOND)
            //.filterMaxDuration(10, TimeUnit.SECOND
            .apply();
}
```

2. You can join events with same name:
```java
public Timeline prepare(Timeline timeline) {
    return timeline.transform()
            .join(new MyNamePredicate(), new MyJoinPredicate())
            //.join(10, TimeUnit.MILLISECONDS)
            //.join(10, TimeUnit.MILLISECONDS, "Event7")
            .apply();
}
```

3. You can implement custom transformer:
```java
public Timeline prepare(Timeline timeline) {
    return timeline.transform()
            .compose(new MyTransformer())
            .apply();
}
```

## Input json format

```
{
  "meta": {
      "title": <String>, // [Required] Chart title
      "kind": <'ABSOLUTE', 'RELATIVE'>, // [Optional] Chart kind ('ABSOLUTE' by default)
      "units": <'s', 'ms', 'ns'>, // [Optional] Units for event values ('ms' by default)
      "nameKey": <String>, // [Optional] Event name mapping key ('name' by default)
      "startKey": <String>, // [Optional] Event start time mapping key ('start' by default)
      "endKey": <String> // [Optional] Event end time mapping key ('end' by default)
      "countKey": <String> // [Optional] Event count mapping key ('count' by default)
  },
  "events": [
      {
          "<your name key>": <String>, // [Required] Event name
          "<your start key>": <Long>, // [Required] Event start time
          "<your end key>": <Long> // [Required] Event end time
          "<your count key>": <Int> // [Optional] Event count
      }, ...
  ]
}
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
