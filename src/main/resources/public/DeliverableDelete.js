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

function submitDeliverable(){
    console.log("Click on deletion...");
    let dName = document.getElementById("deliverables").value;
    let targetName = {"dName" : dName};

    let confirmation = confirm("Delete this deliverable?");
    if (confirmation){
    $.postJSON("http://localhost:8080/deliverableDeletion", targetName, callback);
    console.log("Post request sent");
    }
}

function callback (data,status) {
    console.log(data);
    alert(data);
}