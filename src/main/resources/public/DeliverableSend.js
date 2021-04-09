$.postJSON = function(url, data, callback) {
    return jQuery.ajax({
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    },
    'type': 'POST',
    'url': url,
    'data': JSON.stringify(data),
    'async': false,    //Cross-domain requests and dataType: "jsonp" requests do not support synchronous operation
    'cache': false,    //This will force requested pages not to be cached by the browser
    'processData':false,
    'dataType': 'json',
    'success': callback
    });
};


function sendDeliverable(){
    console.log("Sending Deliverable");

    let targetCourse = document.getElementById("courseCode").value;
    console.log("Target Course: ");
    console.log(targetCourse);

    let deliverableName = document.getElementById("deliverables").value;
    console.log("Assignment name:");
    console.log(deliverableName);

    let submissionLink = document.getElementById("submit-box").value;
    console.log("Sub link:");
    console.log(submissionLink);

     //Turning to JSON
    let studentDeliverable = {"targetCourse": targetCourse, "dName": deliverableName, "subLink": submissionLink};

    let confirmation = confirm("Submit this deliverable?");
    if(confirmation){
    $.postJSON("http://localhost:8080//deliverableStudentSubmission", studentDeliverable, callback);
    console.log("Post request sent");
    }

}


function callback (data,status) {
    console.log(data);
    alert(data);
}