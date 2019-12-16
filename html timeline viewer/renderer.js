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

const TIMELINE_KIND_ABSOLUTE = "ABSOLUTE";
const TIMELINE_KIND_RELATIVE = "RELATIVE";

const CHART_HEADER_HEIGHT = 100;
const CHART_FOOTER_HEIGHT = 50;
const BAR_WIDTH_PX = 20;
const BAR_PADDING_PX = 4;

Highcharts.SVGRenderer.prototype.symbols.cross = function (x, y, w, h) {
    var path = [
        // Cross
        'M', x + 0.1 * w, y + 0.1 * h,
        'L', x + 0.9 * w, y + 0.9 * h,
        'M', x + 0.9 * w, y + 0.1 * h,
        'L', x + 0.1 * w, y + 0.9 * h
    ];
    return path;
};

document.addEventListener('NewTimeline', function (event) {
    let timelineJson = event.detail;
    handleTimeline(timelineJson);
});

function handleTimeline(timelineJson) {
    let meta = timelineJson['meta'];
    let filename = timelineJson.filename;
    let title = meta['title'];
    let kind = meta['kind'];
    let events = parseEvents(timelineJson['events']);
    if (kind == TIMELINE_KIND_RELATIVE) {
        convertToRelative(events);
    }
    let sequences = prepareOrderedSequences(events);
    let eventNames = [];
    let eventDatas = [];
    for (var i = 0; i < sequences.length; i++) {
        let sequence = sequences[i];
        var eventName = undefined;
        for (var event of sequence) {
            let start = event[FIELD_START];
            let end = event[FIELD_END];
            let data = {
                x: start,
                x2: end,
                y: i
            };
            if (!eventName) {
                eventName = event[FIELD_NAME];
            }
            eventDatas.push(data);
        }
        eventNames.push(eventName);
    }
    renderTimeline(filename, title, sequences.length, eventNames, eventDatas);
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

function convertToRelative(events) {
    if (events.length > 0) {
        var min = Number.MAX_SAFE_INTEGER;
        for (var event of events) {
            let less = Math.min(event[FIELD_START], event[FIELD_END]);
            if (less < min) {
                min = less;
            }
        }
        for (var event of events) {
            event[FIELD_START] -= min;
            event[FIELD_END] -= min;
        }
    }
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

function renderTimeline(filename, title, sequenceCount, eventNames, eventDatas) {
    let timelineId = filename;
    let timelineContainer = findTimelineContainer(timelineId);
    let chartHeight = CHART_HEADER_HEIGHT + CHART_FOOTER_HEIGHT + (BAR_WIDTH_PX + BAR_PADDING_PX) * sequenceCount;
    Highcharts.chart(timelineId, {
        chart: {
            type: 'xrange',
            height: chartHeight
        },
        legend: {
            itemStyle: {
                fontSize: '15px'
            }
        },
        title: {
            text: title,
            style: {
                fontWeight: 'bold',
                fontSize: '28px'
            }
        },
        tooltip: {
            formatter: formatTooltip
        },
        xAxis: {
            type: 'datetime',
            labels: {
                style: {
                    fontSize: '14px'
                }
            },
            dateTimeLabelFormats: {
                millisecond: '%M:%S.%L',
                day: '' // to prevent 1 Jan tick
            }
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                style: {
                    fontSize: '15px'
                }
            },
            categories: eventNames,
            reversed: true
        },
        series: [{
            name: filename,
            borderColor: 'gray',
            pointWidth: BAR_WIDTH_PX,
            data: eventDatas,
            turboThreshold: eventDatas.length + 1,
            dataLabels: {
                enabled: true
            }
        }],
        exporting: {
            buttons: {
                deleteButton: {
                    symbol: 'cross',
                    onclick: function () {
                        findTimilineList().removeChild(timelineContainer);
                    }
                }
            }
        }
    });
}

function findTimilineList() {
    return document.getElementById('timeline-list');
}

function findTimelineContainer(timelineId) {
    let timelineList = findTimilineList();
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