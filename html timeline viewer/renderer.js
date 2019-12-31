const META_TITLE = 'title';
const META_KIND = 'kind';
const META_UNITS = 'units';
const META_VALUE_ENCODE_RADIX = 'valueEncodeRadix';
const META_NAME_KEY = 'nameKey';
const META_GROUP_KEY = 'groupKey';
const META_START_KEY = 'startKey';
const META_END_KEY = 'endKey';
const META_COUNT_KEY = 'countKey';
const META_PAYLOAD_KEY = 'payloadKey';

const TIMELINE_KIND_ABSOLUTE = "ABSOLUTE";
const TIMELINE_KIND_RELATIVE = "RELATIVE";

const DEFAULT_UNITS = 'ms';
const DEFAULT_NAME_KEY = 'name';
const DEFAULT_GROUP_KEY = 'group';
const DEFAULT_START_KEY = 'start';
const DEFAULT_END_KEY = 'end';
const DEFAULT_COUNT_KEY = 'count';
const DEFAULT_PAYLOAD_KEY = 'payload';

const INTERVAL_NAME = 'name';
const INTERVAL_GROUP = 'group';
const INTERVAL_START = 'start';
const INTERVAL_END = 'end';
const INTERVAL_COUNT = 'count';
const INTERVAL_PAYLOAD = 'payload';

const SEQUENCE_NAME = 'name';
const SEQUENCE_GROUPED_BY = 'groupedBy';
const SEQUENCE_INTERVALS = 'intervals';

const SEQUENCE_GROUP_TYPE_NAME = 'name';
const SEQUENCE_GROUP_TYPE_GROUP = 'group';

const POINT_NAME = 'name';
const POINT_COUNT = 'count';
const POINT_PAYLOAD = 'payload';

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
    let aliases = timelineJson['aliases'];
    let meta = timelineJson['meta'];
    let title = meta[META_TITLE];
    let kind = meta[META_KIND];

    let intervals = parseIntervals(meta, aliases, timelineJson['intervals']);
    if (kind == TIMELINE_KIND_RELATIVE) {
        convertToRelative(intervals);
    }

    let sequences = prepareOrderedSequences(intervals);
    let categories = [];
    let points = [];
    for (var i = 0; i < sequences.length; i++) {
        let sequence = sequences[i];
        let sequenceName = sequence[SEQUENCE_NAME];
        let sequenceGroupedBy = sequence[SEQUENCE_GROUPED_BY];
        let category = formatCategory(sequenceName, sequenceGroupedBy);
        categories.push(category);
        let sequenceIntervals = sequence[SEQUENCE_INTERVALS];
        for (var interval of sequenceIntervals) {
            let start = interval[INTERVAL_START];
            let end = interval[INTERVAL_END];
            let count = interval[INTERVAL_COUNT];
            let payload = interval[INTERVAL_PAYLOAD];
            let data = {
                x: start,
                x2: end,
                y: i,
                name: interval[INTERVAL_NAME], // POINT_NAME
                count: count, // POINT_COUNT
                payload: payload // POINT_PAYLOAD
            };
            points.push(data);
        }
    }
    renderTimeline(source, title, sequences.length, categories, points);
}

function parseIntervals(meta, aliases, rawIntervals) {
    let units = parseUnits(meta);
    let parsedIntervals = [];
    for (var i = 0; i < rawIntervals.length; i++) {
        let rawInterval = rawIntervals[i];
        let parsedInterval = {};
        parsedInterval[INTERVAL_NAME] = parseIntervalName(meta, aliases, rawInterval);
        parsedInterval[INTERVAL_GROUP] = parseIntervalGroup(meta, aliases, rawInterval);
        parsedInterval[INTERVAL_START] = convertToMs(parseIntervalStart(meta, rawInterval), units);
        parsedInterval[INTERVAL_END] = convertToMs(parseIntervalEnd(meta, rawInterval), units);
        parsedInterval[INTERVAL_COUNT] = parseCount(meta, rawInterval);
        parsedInterval[INTERVAL_PAYLOAD] = parsePayload(meta, rawInterval);
        parsedIntervals.push(parsedInterval);
    }
    return parsedIntervals;
}

function parseUnits(meta) {
    return meta[META_UNITS] || DEFAULT_UNITS;
}

function parseCount(meta, rawInterval) {
    let countKey = meta[META_COUNT_KEY] || DEFAULT_COUNT_KEY;
    return rawInterval[countKey] || 1;
}

function parseAliased(key, aliases, rawInterval) {
    if (aliases) {
        let alias = rawInterval[key];
        return aliases[alias];
    }
    return rawInterval[key];
}

function parseIntervalName(meta, aliases, rawInterval) {
    let nameKey = meta[META_NAME_KEY] || DEFAULT_NAME_KEY;
    return parseAliased(nameKey, aliases, rawInterval);
}

function parseIntervalGroup(meta, aliases, rawInterval) {
    let groupKey = meta[META_GROUP_KEY] || DEFAULT_GROUP_KEY;
    return parseAliased(groupKey, aliases, rawInterval);
}

function parseIntervalStart(meta, rawInterval) {
    let startKey = meta[META_START_KEY] || DEFAULT_START_KEY;
    return parseIntervalTimer(startKey, meta, rawInterval);
}

function parseIntervalEnd(meta, rawInterval) {
    let endKey = meta[META_END_KEY] || DEFAULT_END_KEY;
    return parseIntervalTimer(endKey, meta, rawInterval);
}

function parseIntervalTimer(key, meta, rawInterval) {
    let encodeRadix = meta[META_VALUE_ENCODE_RADIX];
    if (encodeRadix) {
        let encodedValue = rawInterval[key];
        return parseInt(encodedValue, encodeRadix);
    }
    return rawInterval[key];
}

function parsePayload(meta, rawInterval) {
    let payloadKey = meta[META_PAYLOAD_KEY] || DEFAULT_PAYLOAD_KEY;
    return rawInterval[payloadKey];
}

function convertToMs(value, units) {
    switch(units) {
        case 's': return value * 1000;
        case 'ms': return value;
        case 'ns': return Math.floor(value / 1000000);
    }
    throw Error("Unknown units `" + units + "`.");
}

function convertToRelative(intervals) {
    if (intervals.length > 0) {
        var min = Number.MAX_SAFE_INTEGER;
        for (var interval of intervals) {
            let less = Math.min(interval[INTERVAL_START], interval[INTERVAL_END]);
            if (less < min) {
                min = less;
            }
        }
        for (var interval of intervals) {
            interval[INTERVAL_START] -= min;
            interval[INTERVAL_END] -= min;
        }
    }
}

function prepareOrderedSequences(intervals) {
    intervals.sort(intervalComparator); // To guarantee asc order of intervals in sequence
    let groupedSequenceMap = new Map();
    let namedSequenceMap = new Map();
    for (var interval of intervals) {
        var sequenceMap = undefined;
        var sequenceKey = undefined;
        let intervalGroup, intervalName;
        if (intervalGroup = interval[INTERVAL_GROUP]) {
            sequenceMap = groupedSequenceMap;
            sequenceKey = intervalGroup;
        } else if (intervalName = interval[INTERVAL_NAME]) {
            sequenceMap = namedSequenceMap;
            sequenceKey = intervalName;
        }
        var sequence = sequenceMap.get(sequenceKey);
        if (!sequence) {
            sequence = [];
            sequenceMap.set(sequenceKey, sequence);
        }
        sequence.push(interval);
    }
    let sequences = [];
    sequences.push(...toSequences(groupedSequenceMap, SEQUENCE_GROUP_TYPE_GROUP));
    sequences.push(...toSequences(namedSequenceMap, SEQUENCE_GROUP_TYPE_NAME));
    return sequences.sort(sequenceComparator);
}

function toSequences(sequenceMap, groupedBy) {
    let sequences = [];
    for (var [key, intervals] of sequenceMap) {
        let sequence = {};
        sequence[SEQUENCE_NAME] = key;
        sequence[SEQUENCE_GROUPED_BY] = groupedBy;
        sequence[SEQUENCE_INTERVALS] = intervals;
        sequences.push(sequence);
    }
    return sequences;
}

function intervalComparator(a, b) {
    if (a[INTERVAL_START] != b[INTERVAL_START]) {
        return a[INTERVAL_START] - b[INTERVAL_START];
    }
    if (a[INTERVAL_END] != b[INTERVAL_END]) {
        return a[INTERVAL_END] - b[INTERVAL_END];
    }
    return 0;
}

function sequenceComparator(a, b) {
    let aIntervals = a[SEQUENCE_INTERVALS];
    let bIntervals = b[SEQUENCE_INTERVALS];
    if (aIntervals.length == 0 || bIntervals.length == 0) {
        throw Error("Attempt to compare empty sequence.")
    }
    return intervalComparator(aIntervals[0], bIntervals[0]);
}

function formatCategory(name, groupedBy) {
    switch (groupedBy) {
        case SEQUENCE_GROUP_TYPE_NAME:
            return name;
        case SEQUENCE_GROUP_TYPE_GROUP:
            return '<span class="category-type-label">group</span>' + name;
    }
    return name;
}

function formatDataLabels() {
    let point = this;
    let name = point['point'][POINT_NAME];
    let barWidth = point['point']['shapeArgs']['width'];
    let labelWidth = measureText(name, "bold 11px verdana");
    return barWidth >= labelWidth ? name : '';
}

function formatTooltip() {
    let point = this;
    let duration = point.x2 - point.x;
    let count = point['point'][POINT_COUNT];
    let name = point['point'][POINT_NAME];
    let nameString = count > 1
        ? (name + ' (join ' + count + ')')
        : (name);
    let mainInfo = '<span style="color:' + point.color + '">●</span><pre>    </pre><b>' + nameString + '</b> ' + duration + ' ms<br/>';
    let payload = point['point'][POINT_PAYLOAD];
    if (payload) {
        return mainInfo + '<span style="color:' + '#fff' + '">●</span><pre>    </pre><b>payload: </b>' + payload + '<br/>';
    } else {
        return mainInfo;
    }
}

function renderTimeline(source, title, sequenceCount, categories, points) {
    let timelineId = source;
    let timelineContainer = findTimelineContainer(timelineId);
    let chartHeight = CHART_HEADER_HEIGHT + CHART_FOOTER_HEIGHT + (BAR_WIDTH_PX + BAR_PADDING_PX) * sequenceCount;
    Highcharts.chart(timelineId, {
        chart: {
            type: 'xrange',
            height: chartHeight,
            zoomType: 'x'
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
        plotOptions: {
            series: {
                dataLabels: {
                    enabled: true,
                    inside: true
                }
            }
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
                },
                useHTML: true
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
            dataLabels: [{
                align: 'center',
                crop: true,
                formatter: formatDataLabels
            }]
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

function measureText(text, font) {
    var myCanvas = measureText.canvas || (measureText.canvas = document.createElement("canvas"));
    var context = myCanvas.getContext("2d");
    context.font = font;
    
    var metrics = context.measureText(text);
    return metrics.width;
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