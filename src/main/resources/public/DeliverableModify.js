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


function modifyDeliverable(){
    console.log("Clicked on Modification");

    let oldName = document.getElementById("deliverableOldName").value;
    console.log("Old name");
    console.log(oldName);

    let newName = document.getElementById("deliverable-name").value;
    console.log("New name");
    console.log(newName);

    let newWeight = document.getElementById("weighting").value;
    console.log("New Weight");
    console.log(newWeight);

    let newDeadLine = document.getElementById("daysDue").value;
    console.log("New days until due");
    console.log(newDeadLine);

    let newDescription = document.getElementById("deliverable-description").value;
    console.log("New description");
    console.log(newDescription);

    //Turning into JSON
    let updatedDeliverable = {"oldName": oldName, "newName": newName, "newWeight" : newWeight,
    "newDueDate": newDeadLine, "newDescription": newDescription};

    let confirmation = confirm("Delete this deliverable?");
    if (confirmation){
        $.postJSON("http://localhost:8080/deliverableModification", updatedDeliverable, callback);
        console.log("Post request sent");
        }
}


function callback (data,status) {
    console.log(data);
    alert(data);
}