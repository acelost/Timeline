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
    parseTimelineFile(file);
}

function parseTimelineFile(file) {
    let reader = new FileReader();
    reader.onload = function(event) {
        let content = event.target.result;
        let timelineJson = JSON.parse(content);
        timelineJson['filename'] = file.name;
        notifyNewTimeline(timelineJson);
    };
    reader.readAsText(file);
}

function notifyNewTimeline(timelineJson) {
    let event = new CustomEvent('NewTimeline', { 'detail': timelineJson });
    document.dispatchEvent(event);
}