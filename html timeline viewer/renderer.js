let parsers = [
    {
        units: "ms",
        nameField: "name",
        startField: "startMs",
        endField: "endMs"
    },
    {
        units: "ns",
        nameField: "name",
        startField: "startNs",
        endField: "endNs"
    }
];

const FIELD_NAME = 'name';
const FIELD_START = 'start';
const FIELD_END = 'end';

document.addEventListener('NewTimeline', function (event) {
    let timelineJson = event.detail;
    handleTimeline(timelineJson);
});

function handleTimeline(timelineJson) {
    let meta = timelineJson['meta'];
    let filename = timelineJson.filename;
    let title = meta['title'];
    let events = parseEvents(timelineJson['events']);
    let sequences = prepareOrderedSequences(events);
    let eventNames = [];
    let eventDatas = [];
    for (var i = 0; i < sequences.length; i++) {
        let sequence = sequences[i];
        for (var event of sequence) {
            let name = event[FIELD_NAME];
            let start = event[FIELD_START];
            let end = event[FIELD_END];
            let data = {
                x: start,
                x2: end,
                y: i
            };
            eventNames.push(name);
            eventDatas.push(data);
        }
    }
    renderTimeline(filename, title, eventNames, eventDatas);
}

function parseEvents(rawEvents) {
    let parsedEvents = [];
    for (var i = 0; i < rawEvents.length; i++) {
        let rawEvent = rawEvents[i];
        let event = {};
        event[FIELD_NAME] = parseEventName(rawEvent);
        event[FIELD_START] = parseEventStart(rawEvent);
        event[FIELD_END] = parseEventEnd(rawEvent);
        parsedEvents.push(event);
    }
    return parsedEvents;
}

function parseEventName(event) {
    for (var i = 0; i < parsers.length; i++) {
        let parser = parsers[i];
        let nameField = parser['nameField'];
        if (nameField in event) {
            return event[nameField];
        }
    }
    throw Error("Не найден парсер для названия события: " + JSON.stringify(event));
}

function parseEventStart(event) {
    for (var i = 0; i < parsers.length; i++) {
        let parser = parsers[i];
        let startField = parser['startField'];
        if (startField in event) {
            return convertToMs(event[startField], parser['units']);
        }
    }
    throw Error("Не найден парсер для начала события: " + JSON.stringify(event));
}

function parseEventEnd(event) {
    for (var i = 0; i < parsers.length; i++) {
        let parser = parsers[i];
        let endField = parser['endField'];
        if (endField in event) {
            return convertToMs(event[endField], parser['units']);
        }
    }
    throw Error("Не найден парсер для окончания события: " + JSON.stringify(event));
}

function convertToMs(value, units) {
    switch(units) {
        case 'ms': return value;
        case 'ns': return Math.floor(value / 1000000);
    }
    throw Error("В парсере не указаны единицы измерения.")
}

function prepareOrderedSequences(events) {
    events.sort(eventComparator); // To guaratee asc order of events in sequence
    let map = new Map();
    for (var event of events) {
        let eventName = event[FIELD_NAME];
        var sequence = map.get(eventName);
        if (!sequence) {
            sequence = [];
            map.set(eventName, sequence);
        }
        sequence.push(event);
    }
    return [...map.values()].sort(sequenceComparator);
}

function eventComparator(a, b) {
    if (a[FIELD_START] != b[FIELD_START]) {
        return a[FIELD_START] - b[FIELD_START];
    }
    if (a[FIELD_END] != b[FIELD_END]) {
        return a[FIELD_END] - b[FIELD_END];
    }
    return 0;
}

function sequenceComparator(a, b) {
    if (a.length == 0 || b.length == 0) {
        throw Error("Обнаружена попытка сравнить пустую последовательность.")
    }
    return eventComparator(a[0], b[0]);
}

function formatTooltip() {
    let point = this;
    let duration = point.x2 - point.x;
    return '<span style="color:' + point.color + '">●</span><pre>    </pre><b>' + point.yCategory + '</b> ' + duration + ' ms<br/>';
}

function renderTimeline(filename, title, eventNames, eventDatas) {
    let timelineId = filename;
    let timelineContainer = findTimelineContainer(timelineId);
    Highcharts.chart(timelineId, {
        chart: {
            type: 'xrange'
        },
        xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: {
                millisecond: '%M:%S.%L'
            }
        },
        title: {
            text: title
        },
        tooltip: {
            formatter: formatTooltip
        },
        yAxis: {
            title: {
                text: ''
            },
            categories: eventNames,
            reversed: true
        },
        series: [{
            name: filename,
            borderColor: 'gray',
            pointWidth: 20,
            data: eventDatas,
            dataLabels: {
                enabled: true
            }
        }]
    
    });
}

function findTimelineContainer(timelineId) {
    let timelineList = document.getElementById('timeline-list');
    var reusedContainer = undefined;
    let nodes = timelineList.childNodes;
    for (var i = 0; i < nodes.length; i++) {
        let node = nodes[i];
        if (node.id == timelineId) {
            reusedContainer = node;
            break;
        }
    }
    if (!reusedContainer) {
        let newContainer = document.createElement('div');
        newContainer.id = timelineId;
        newContainer.classList.add('timeline-container');
        timelineList.appendChild(newContainer);
        return newContainer;
    }
    return reusedContainer;
}