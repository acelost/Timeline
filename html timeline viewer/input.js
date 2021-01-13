document.addEventListener('DOMContentLoaded', function () {
    let queryParams = (new URL(document.location)).searchParams;
    let base64Source = queryParams.get("source");
    let base64Timeline = queryParams.get("timeline");
    if (base64Timeline != null) {
        let sourceString = base64Source ? atob(base64Source) : "anonymous";
        let timelineString = atob(base64Timeline);
        let timelineJson = JSON.parse(timelineString);
        timelineJson["source"] = sourceString;
        handleTimeline(timelineJson);
    }
});

document.addEventListener('DOMContentLoaded', function () {
    let dropArea = document.getElementById('drop-timeline-area');

    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, preventDefaults, false)
    });
    
    ['dragenter', 'dragover'].forEach(eventName => {
        dropArea.addEventListener(eventName, highlight, false)
    });
    
    ['dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, unhighlight, false)
    });
    
    dropArea.addEventListener('drop', handleDrop, false);

    function highlight(e) {
        dropArea.classList.add('highlight')
    }
    
    function unhighlight(e) {
        dropArea.classList.remove('highlight')
    }
});

document.addEventListener('paste', function (event) {
    let paste = (event.clipboardData || window.clipboardData).getData('text');
    let source = 'paste(' + hashCode(paste) + ')';
    handleContent(source, paste);
    event.preventDefault();
});

function preventDefaults (e) {
    e.preventDefault()
    e.stopPropagation()
}

function handleDrop(e) {
    let dt = e.dataTransfer
    let files = dt.files
    
    handleFiles(files)
}

function handleFiles(files) {
    ([...files]).forEach(handleFile)
}

function handleFile(file) {
    let reader = new FileReader();
    reader.onload = function(event) {
        let content = event.target.result;
        handleContent(file.name, content);
    };
    reader.readAsText(file);
}

function handleContent(source, content) {
    try {
        let timelineJson = JSON.parse(content);
        timelineJson['source'] = source;
        notifyNewTimeline(timelineJson);
    } catch(error) {
        console.log(error);
        alert('Timeline json parsing failed! Please check your input data and try again.\n' + error);
    }
}

function parseTimeline(source, content) {
    let timelineJson = JSON.parse(content);
    timelineJson['source'] = source;
    return timelineJson;
}

function notifyNewTimeline(timelineJson) {
    let event = new CustomEvent('NewTimeline', { 'detail': timelineJson });
    document.dispatchEvent(event);
}

function hashCode(s) {
    for(var i = 0, h = 0; i < s.length; i++) {
        h = Math.imul(31, h) + s.charCodeAt(i) | 0;
    }
    return Math.abs(h);
}