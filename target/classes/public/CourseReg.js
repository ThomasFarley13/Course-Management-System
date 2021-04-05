let courseSearchRes = null
let selectedCourseIDs = {}
let selectedCourseCount = 0 
function removeCourse(id) {
    //removing from screen
    console.log("inside the remove course function")
    let course = document.getElementById(id);
    course.parentNode.removeChild(course);
    selectedCourseCount-=1
    
    // removing from selected list
    delete selectedCourseIDs[id]
    console.log(selectedCourseIDs)
}


function addCourse(id) {

    // adding item to selected courses 
    let selectedcontainer = document.getElementById('selectedC')
    let mycourse = courseSearchRes[id]
    var div = document.createElement("div");
    div.id = ("Csel"+ selectedCourseCount) + id

    div.classList.add("card")

    selectedCourseIDs[div.id] = mycourse["courseCode"]

    selectedcontainer.appendChild(div);

    let output = `<div class="card-body">
    <div class="row">
        <div class = "col">
            <h6 class="card-title">${mycourse["courseCode"]}: ${mycourse["courseName"]}</h6>
        </div>
        <div class = "col-3">
            <input type="button" onclick="removeCourse('Csel${selectedCourseCount}${id}')" value="Remove"></input>
        </div>
    </div>
    <div class="row">
        <h7 class="card-subtitle text-muted">  Course Time:</h7>
    </div>

</div>`

    document.getElementById(("Csel"+ selectedCourseCount) + id).innerHTML = output;
    selectedCourseCount+=1

    // removing item from search list 
    let course = document.getElementById("CNum"+id)
    course.parentNode.removeChild(course);

    console.log(selectedCourseIDs)
}

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


function search() {
    let queryobj = {}
    queryobj["Subject"] =  $( "select#Subject" ).val();
    queryobj["CourseLevel"] = $( "select#CLevel" ).val();
    queryobj["CourseNum"] =  document.getElementById('CNumber').value
    queryobj["CourseID"] = document.getElementById('CourseID').value

    console.log(queryobj)

    url = "http://localhost:8080/getCourses?"
    Object.keys(queryobj).forEach(key => {
        if (queryobj[key] != null && queryobj[key] != "" && queryobj[key] != "any") {
            url+= `${key}=${queryobj[key]}&`
        }
      });

      if (url.charAt(url.length - 1) == "&"){
        url = url.slice(0, -1); 
      }

    jQuery.get(url, function(data, status){
      console.log("Data: " + data + "\nStatus: " + status);
      courseSearchRes = data;
      if (data == '') {
        alert("There are no courses that matched your search")
      }
      displayOptions(data)
    });

}

function displayOptions(courseArray) {
    let coursecontainer = document.getElementById('searchRes')
    let htmlout = `<div class="card-header" style="color:white; background-color:#ff0000A6">
                     Search Results
                    </div>`; 

    courseArray.forEach(addElement)
    function addElement(item, index) {
        htmlout += ` <div class="card" id= "CNum${index}">
        <div class="card-body">
            <div class="row">
                <div class = "col">
                    <h6 class="card-title">${item["courseCode"]}: ${item["courseName"]}</h6>
                </div>
                <div class = "col">
                    <h7 class="card-subtitle text-muted">  Course Time:</h7>
                </div>
                <div class = "col-3">
                    <input type="button" onclick="addCourse(${index})" value="addCourse"></input>
                </div>
            </div>

        </div>
    </div>`
    
    

    }

    coursecontainer.innerHTML = htmlout;


}

function register () {
    let confirmation = confirm("Are you sure you want to reister for these courses");
    if (confirmation) {
        $.postJSON("http://localhost:8080/Courseregistration",selectedCourseIDs,callback);
    }
}

function callback (data,status) {
    console.log(data);
    alert(data);
}