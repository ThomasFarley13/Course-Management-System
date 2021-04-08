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

function submitDeliverable() {
    console.log("Clicked on submission...");

    let dCourse = document.getElementById("courses").value;
    console.log("Course of deliverable:");
    console.log(dCourse);

    let dName = document.getElementById("deliverable-name").value;
    console.log("Name of deliverable:");
    console.log(dName);

    let dWeighting = document.getElementById("weighting").value;
    console.log("Weight of Deliverable:");
    console.log(dWeighting);

    let daysUntilDue = document.getElementById("daysDue").value;
    console.log("Days until due:");
    console.log(daysUntilDue);

    let description = document.getElementById("deliverable-description").value;
    console.log("Description:");
    console.log(description);

    //Turning to JSON
    let deliverable = {"course": dCourse, "name": dName, "weighting": dWeighting, "daysDue": daysUntilDue, "desc": description};

    let confirmation = confirm("Create this deliverable?");
    if (confirmation){
        $.postJSON("http://localhost:8080/deliverableSubmission", deliverable, callback);
        console.log("Post request sent..")
    }

}


function callback (data,status) {
    console.log(data);
    alert(data);
}

