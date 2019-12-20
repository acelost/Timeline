const META_TITLE = 'title';
const META_KIND = 'kind';
const META_UNITS = 'units';
const META_NAME_KEY = 'nameKey';
const META_START_KEY = 'startKey';
const META_END_KEY = 'endKey';
const META_COUNT_KEY = 'countKey';

const TIMELINE_KIND_ABSOLUTE = "ABSOLUTE";
const TIMELINE_KIND_RELATIVE = "RELATIVE";

const DEFAULT_UNITS = 'ms';
const DEFAULT_NAME_KEY = 'name';
const DEFAULT_START_KEY = 'start';
const DEFAULT_END_KEY = 'end';
const DEFAULT_COUNT_KEY = 'count';

const EVENT_NAME = 'name';
const EVENT_START = 'start';
const EVENT_END = 'end';
const EVENT_COUNT = 'count';

const POINT_PAYLOAD_COUNT = 'count';

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
    let source = timelineJson['source'];
    let meta = timelineJson['meta'];
    let title = meta[META_TITLE];
    let kind = meta[META_KIND];

    let events = parseEvents(meta, timelineJson['events']);
    if (kind == TIMELINE_KIND_RELATIVE) {
        convertToRelative(events);
    }

    let sequences = prepareOrderedSequences(events);
    let categories = [];
    let points = [];
    for (var i = 0; i < sequences.length; i++) {
        let sequence = sequences[i];
        var category = undefined;
        for (var event of sequence) {
            let start = event[EVENT_START];
            let end = event[EVENT_END];
            let count = event[EVENT_COUNT];
            let data = {
                x: start,
                x2: end,
                y: i,
                count: count // POINT_PAYLOAD_COUNT
            };
            if (!category) {
                category = event[EVENT_NAME];
            }
            points.push(data);
        }
        categories.push(category);
    }
    renderTimeline(source, title, sequences.length, categories, points);
}

function parseEvents(meta, rawEvents) {
    let nameKey = meta[META_NAME_KEY] || DEFAULT_NAME_KEY;
    let startKey = meta[META_START_KEY] || DEFAULT_START_KEY;
    let endKey = meta[META_END_KEY] || DEFAULT_END_KEY;
    let countKey = meta[META_COUNT_KEY] || DEFAULT_COUNT_KEY;
    let units = meta[META_UNITS] || DEFAULT_UNITS;
    let parsedEvents = [];
    for (var i = 0; i < rawEvents.length; i++) {
        let rawEvent = rawEvents[i];
        let parsedEvent = {};
        parsedEvent[EVENT_NAME] = rawEvent[nameKey];
        parsedEvent[EVENT_START] = convertToMs(rawEvent[startKey], units);
        parsedEvent[EVENT_END] = convertToMs(rawEvent[endKey], units);
        parsedEvent[EVENT_COUNT] = rawEvent[countKey] || 1;
        parsedEvents.push(parsedEvent);
    }
    return parsedEvents;
}

function convertToMs(value, units) {
    switch(units) {
        case 's': return value * 1000;
        case 'ms': return value;
        case 'ns': return Math.floor(value / 1000000);
    }
    throw Error("Unknown units `" + units + "`.");
}

function convertToRelative(events) {
    if (events.length > 0) {
        var min = Number.MAX_SAFE_INTEGER;
        for (var event of events) {
            let less = Math.min(event[EVENT_START], event[EVENT_END]);
            if (less < min) {
                min = less;
            }
        }
        for (var event of events) {
            event[EVENT_START] -= min;
            event[EVENT_END] -= min;
        }
    }
}

function prepareOrderedSequences(events) {
    events.sort(eventComparator); // To guarantee asc order of events in sequence
    let map = new Map();
    for (var event of events) {
        let eventName = event[EVENT_NAME];
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
    if (a[EVENT_START] != b[EVENT_START]) {
        return a[EVENT_START] - b[EVENT_START];
    }
    if (a[EVENT_END] != b[EVENT_END]) {
        return a[EVENT_END] - b[EVENT_END];
    }
    return 0;
}

function sequenceComparator(a, b) {
    if (a.length == 0 || b.length == 0) {
        throw Error("Attempt to compare empty sequence.")
    }
    return eventComparator(a[0], b[0]);
}

function formatTooltip() {
    let point = this;
    let duration = point.x2 - point.x;
    let count = point['point'][POINT_PAYLOAD_COUNT];
    let category = count > 1
        ? (point.yCategory + ' (join ' + count + ')')
        : (point.yCategory);
    return '<span style="color:' + point.color + '">●</span><pre>    </pre><b>' + category + '</b> ' + duration + ' ms<br/>';
}

function renderTimeline(source, title, sequenceCount, categories, points) {
    let timelineId = source;
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
                millisecond: '%S.%L', // 1500ms -> 01.500
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
            categories: categories,
            reversed: true
        },
        series: [{
            name: source,
            borderColor: 'gray',
            pointWidth: BAR_WIDTH_PX,
            data: points,
            turboThreshold: points.length + 1,
            dataLabels: {
                enabled: true
            }
        }],
        exporting: {
            buttons: {
                deleteButton: {
                    symbol: 'cross',
                    onclick: function () {
                        findTimelineList().removeChild(timelineContainer);
                    }
                }
            }
        }
    });
}

function findTimelineList() {
    return document.getElementById('timeline-list');
}

function findTimelineContainer(timelineId) {
    let timelineList = findTimelineList();
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