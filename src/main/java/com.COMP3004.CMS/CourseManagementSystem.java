package com.COMP3004.CMS;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import java.util.List;


@Controller
@ComponentScan("com")
public class CourseManagementSystem {

    User userLoggedIn;
    boolean logged_in = false;
    String userHash;
    UserCreateFactory factory = new User();

    static String registrationstartDate = "2021-08-31";
    static String registrationTerm1 = "2021-09-20";
    static String registrationTerm2 = "2022-01-20";
    static String registrationTerm3 = "2022-05-20";
    static String withdrawByDateTerm1 = "2021-12-10";
    static String withdrawByDateTerm2 = "2022-04-10";
    static String withdrawByDateTerm3 = "2022-08-10";

    @Autowired
    private UserDatabase repository;

    @Autowired
    private CourseDatabase Courserepository;

    @Autowired
    private DeliverableDatabase dRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    DatabaseHandler handler ;

    //just need user(name) and denied courses temporarily stored
    ArrayList<User> deniedRegistrations = new ArrayList<User>();

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("logged_in")!= null && ((boolean) session.getAttribute("logged_in"))) {
            //redirectAttributes.addFlashAttribute("User",(User)session.getAttribute("user"));
            return "forward:/dashboard";

        } else {
            return "forward:/login";
        }
    }
    @GetMapping("/login")
    public String login(@ModelAttribute User user, Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/createAccount")
    public String createAccount() {
        return "create-account";
    }



    @PostMapping("/createAccount")
    public String createAccountHandler(@RequestParam String userType) {
        if(userType.equals("professor")){
            return "professor-create";
        }
        else if(userType.equals("student")){
            return "student-create";
        }
        else {
            System.out.println(userType);
            return "error";
        }
    }

    @GetMapping("/approveUser")
    public String approveUser(Model model) {
        List<User> users = repository.findByActiveIsFalse();
        model.addAttribute("users",users);
        return "user-approve";

    }
    @PostMapping("/approveUser")
    public String approveUserHandling(@RequestParam(value = "usernameChecked", required = false) List<String> users) {
        if(users!=null) {
            for (String user : users) {
                User x = repository.findByUsername(user);
                x.setActive(true);
                repository.save(x);
            }
        }
        return "redirect:/approveUser";
    }

    @GetMapping(value ="/getStats")
    //@ResponseBody
    public String getStats(HttpServletResponse response) throws IOException {
        Stats s = new Stats(repository);
        XSSFWorkbook file = s.getStats();
        String filename = "Export.xlsx";
        String filetype = "xlsx";

        response.addHeader("Content-disposition", "attachment;filename=" + filename);
        response.setContentType(filetype);
        file.write(response.getOutputStream());
        response.flushBuffer();
        return null;
    }

    @GetMapping("/deleteUser")
    public String deleteUser(Model model) {
        List<User> users = repository.findAll();
        model.addAttribute("users",users);
        return "user-delete";
    }

    @PostMapping("/deleteUser")
    public String deleteUserHandling(@RequestParam(value = "usernameChecked", required = false) List<String> users) {
        if(users!=null) {
            for (String user : users) {
                User x = repository.findByUsername(user);
                ArrayList<String> courses = x.getCourseList();
                for (String courseID : courses) {
                    if(courseID != null) {
                        handler.deregister_student(user, courseID);
                    }
                }
                repository.deleteByUsername(user);
            }
        }
        return "redirect:/deleteUser";
    }

    @PostMapping("/createUserRequest")
    public String createUserRequest(@RequestParam String username,
                                    @RequestParam String password,
                                    @RequestParam String firstname,
                                    @RequestParam String lastname,
                                    @RequestParam(required = false) String date,
                                    @RequestParam(required = false) String gender){
        if(repository.findByUsername(username) != null && date == null){
            return "professor-create-taken";
        }
        else if(repository.findByUsername(username) != null){
            return "student-create-taken";
        }

        if(date == null){
            repository.save(factory.createUser(username, password, "Professor", repository.findTopByOrderByIdDesc().getId() +1, date, gender, firstname, lastname));
        }
        else{
            repository.save(factory.createUser(username, password, "Student", repository.findTopByOrderByIdDesc().getId() +1, date, gender, firstname, lastname));
        }
        return "create-successful";
    }

    @GetMapping("/changePassword")
    public String changePassword() {
        return "forgot-password";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String username,
                                    @RequestParam String password){
        if(repository.findByUsername(username) == null){
            return "forgot-password-error";
        }
        else{
            User x = repository.findByUsername(username);
            x.setPassword(password);
            repository.save(x);
            return "change-successful";
        }
    }

    @PostMapping("/login")
    public Object loginhandler(@ModelAttribute User user, Model model,HttpSession session) {
        model.addAttribute(user);

        // authenitcation
        User loginUser = repository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        if (loginUser != null && loginUser.getActive()) {
            session.setAttribute("username",loginUser.getUsername());
            session.setAttribute("role",loginUser.getRole());
            session.setAttribute("logged_in", true);
            return "redirect:/dashboard";
        } else {
            // figure out message to send on fail and how to send it to the html
            return "login";
        }
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        //System.out.println("We got to the dashboard Function");
        //System.out.println(Courserepository.findByCourseCode("3004B"));
        //System.out.println(repository.findByUsername("Abdul"));
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) ) {
            //System.out.println("We got to the dashboard Function Login 4");
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"),(String) session.getAttribute("role"));
            //System.out.println("We got to the dashboard Function Login 3");
            model.addAttribute("user",user);
            //System.out.println("We got to the dashboard Function Login 1");
            //System.out.println(user.getRole());
            if(user.getRole().equals("Admin")) {
                return "admin-home";
            }
            else if(user.getRole().equals("Professor")){
                System.out.println("The prof object: " + user);
                return "professor-home";
            }
            else if(user.getRole().equals("Student")){
                System.out.println("We got to the dashboard Function loggin 2");
                return "student-home";
            }
            else{
                return "error";
            }
        } else {
            // figure out message to send on fail and how to send it to the html
            System.out.println("Why are we here");
            return "login";
        }
    }



    @PostMapping("/dashboard")
    public String dashboardhandler(@ModelAttribute User user, Model model,HttpSession session) {

        user = (User) session.getAttribute("user");

        System.out.println(user.getUsername());
        System.out.println(user.getPassword());

        if (repository.findByUsernameAndPassword(user.getUsername(), user.getPassword()) != null) {
            if(repository.findByUsernameAndRole(user.getUsername(), "Admin") != null) {
                return "admin-home";
            }
            else if(repository.findByUsernameAndRole(user.getUsername(), "Professor") != null ){
                return "professor-home";
            }
            else if(repository.findByUsernameAndRole(user.getUsername(), "Student") != null){
                return "student-home";
            }
            else{
                return "error";
            }
        } else {
            // figure out message to send on fail and how to send it to the html
            return "login";
        }
    }


    @GetMapping("/submitDeliverables")
    public String submitDeliverables(@ModelAttribute("User") User user, Model model) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }

        model.addAttribute(user);
        System.out.println("The student here is: " + user.getUsername());

        return "submit-deliverable";
    }


    @GetMapping("/courseInformation")
    public String courseInformation(@ModelAttribute("User") User user, Model model) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }
        model.addAttribute(user);

        return "student-course-info";
    }

    @GetMapping("/createDeliverable")
    public String createDeliverable(Model model, HttpSession session) {

        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) ) {
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);

            if(user.getUsername() != null){
                System.out.println("REACHED DELIVERABLE CREATION PAGE");
                //Getting required info here
                List<String> assignedCourses = ((User.Professor) user).retrieveCourses();
                System.out.println("Assigned Courses: " + assignedCourses.toString());
                model.addAttribute("assignedCourses", assignedCourses);
                model.addAttribute("user",repository.findByUsernameAndRole((String) session.getAttribute("username"),(String) session.getAttribute("role")));
                return "create-deliverable";
            }
            else{
                System.out.println("Reached getUsername = null error");
                return "error";
            }
        }
        System.out.println("Reached not logged in error page");
        return "error";
    }

    @GetMapping("/deleteDeliverable")
    public String deleteDeliverable(Model model, HttpSession session) {
       if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in"))){
           User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
           model.addAttribute("user", user);
           System.out.println("REACHED DELIVERABLE DELETION PAGE");

           //Getting prof's deliverables
           List<Deliverable> profDeliverables = dRepository.findByowner(user.username);
           List<String> deliverableNames = new ArrayList<>();

           for (Deliverable profDeliverable : profDeliverables) {
               deliverableNames.add(profDeliverable.name);
           }

           System.out.println("Prof's deliverables: " + deliverableNames.toString());
           model.addAttribute("deliverableNames", deliverableNames);
           model.addAttribute("user",repository.findByUsernameAndRole((String) session.getAttribute("username"),(String) session.getAttribute("role")));

           return "delete-deliverable";
       }

       return "error";
    }


    @GetMapping("/modifyDeliverable")
    public String modifyDeliverable(Model model, HttpSession session) {
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in"))){
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"), (String) session.getAttribute("role"));
            model.addAttribute("user", user);
            System.out.println("REACHED DELIVERABLE MODIFICATION PAGE");

            List<Deliverable> profDeliverables = dRepository.findByowner(user.username);
            List<String> deliverableNames = new ArrayList<>();

            for (Deliverable profDeliverable : profDeliverables) {
                deliverableNames.add(profDeliverable.name);
            }

            System.out.println("Prof's deliverables: " + deliverableNames.toString());
            model.addAttribute("deliverableNames", deliverableNames);
            model.addAttribute("user",repository.findByUsernameAndRole((String) session.getAttribute("username"),(String) session.getAttribute("role")));

            return "modify-deliverable";
        }

        return "error";
    }


    @GetMapping("/courseDescriptionUpdate")
    public String courseDescriptionUpdate(@ModelAttribute("User") User user, Model model, HttpSession session) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }
        user = repository.findByUsernameAndRole((String) session.getAttribute("username"),(String) session.getAttribute("role"));
        System.out.println("===========================> USER IS:");
        System.out.println(user);
        model.addAttribute(user);
        User.Professor tempUser;
        if(user.getRole().equals("Professor"))
            tempUser = (User.Professor) user;
        else
            return "error";
        ArrayList<String> courses = tempUser.retrieveCourses();
        model.addAttribute("courses", courses);

        return "update-course-info";
    }

    @PostMapping("/updateCourseInfo")
    public String updateCourseInfo(@RequestParam String courseCode,
                                   @RequestParam String courseInfo){
        if(Courserepository.findByCourseCode(courseCode) == null){
            System.out.println("Course Info Update: Course submitted doesn't exist");
            return "update-course-info-error";
        }

        System.out.println("Updating course info for " + courseCode);
        handler.cou.updateRecords("UpdateCourseDetails", "Course", "Professor", courseCode, courseInfo);

        return "course-info-update-successful";
    }

    @GetMapping("/createCourse")
    public String createCourse(@ModelAttribute("User") User user, Model model) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }
        model.addAttribute(user);

        return "create-course";
    }

    @PostMapping("/createCourseRequest")
    public String createCourseRequest(@RequestParam String courseName,
                                      @RequestParam String courseCode,
                                      @RequestParam int courseLevel,
                                      @RequestParam int courseNumber,
                                      @RequestParam String courseDept,
                                      @RequestParam String courseInfo,
                                      @RequestParam int startTerm,
                                      @RequestParam int endTerm){
        System.out.println("Received new course's data, Code: " + courseCode + ", Name: " + courseName);
        if(Courserepository.findByCourseCode(courseCode) != null){
            System.out.println("Submitted course code exists");
            return "create-course-exists";
        }

        System.out.println("Saving new course " + courseName);
        if (courseLevel != 0 || courseNumber != 0 || courseDept != null) {
            String registerBy, withdrawBy;
            if(startTerm == 1)
                registerBy = registrationTerm1;
            else if(startTerm == 2)
                registerBy = registrationTerm2;
            else
                registerBy = registrationTerm3;

            if(endTerm == 1)
                withdrawBy = withdrawByDateTerm1;
            else if(endTerm == 2)
                withdrawBy = withdrawByDateTerm2;
            else
                withdrawBy = withdrawByDateTerm3;

            Course tempCourse = new Course(courseName, courseCode, courseLevel, courseNumber, courseDept);
            tempCourse.setRegisterByDate(registerBy);
            tempCourse.setWithdrawByDate(withdrawBy);
            tempCourse.setCourseInfo(courseInfo);
            Courserepository.save(tempCourse);

        }
        else
            handler.cou.updateRecords("Add", "Course", "Admin", courseCode, courseName);

        return "course-create-successful";
    }


    @GetMapping("/deleteCourse")
    public String deleteCourse(@ModelAttribute("User") User user, Model model) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }
        model.addAttribute(user);
        List<Course> courses = Courserepository.findAll();
        model.addAttribute("courses", courses);

        return "delete-course";
    }

    @PostMapping("/deleteCourseRequest")
    public String deleteCourseRequest(@RequestParam String courseCode){
        if(Courserepository.findByCourseCode(courseCode) == null){
            System.out.println("Trying to delete course that does not exist");
            return "delete-course-error";
        }

        System.out.println("Deleting " + courseCode);
        handler.cou.updateRecords("Delete", "Course", "Admin", courseCode, null);

        return "course-delete-successful";
    }


    /*
    This is a function to test Login using jmeter.
    It exists because ajax does not send json in a way that @requestBody will parse
    and jmeter cannot send Json in a way that @modelattribute can parse
     */
    @PostMapping("/loginTest")
    public String logintesthandler(@RequestBody User user, Model model) {
        model.addAttribute(user);
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        // fake authenitcation
        if (repository.findByUsernameAndPassword(user.getUsername(), user.getPassword()) != null) {
            return "dashboard";
        } else {
            // figure out message to send on fail and how to send it to the html
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logouthandler(@ModelAttribute User user, Model model,HttpSession session) {
        /*model.addAttribute("user", new User());
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());*/
        session.setAttribute("logged_in",false);
        return "login";
    }




    @GetMapping(value = "/dashboardTest")
    public String dashboardTest(Model model) {
        System.out.println("We are in the DashboardTest mapping function");
        String[] coursenames = {"Comp3000", "Comp3001", "Comp3002", "Comp3003", "Comp3004"};
        String[] courselinks = {"courses/Comp3000", "courses/Comp3001", "courses/Comp3002", "courses/Comp3003", "courses/Comp3004"};

        model.addAttribute("courses", coursenames);
        model.addAttribute("links", courselinks);
        model.addAttribute("user",factory.createUser("Sepehr","Password423","Student", 7,"null", "null","Dave","Ian"));

        return "dashboard";
    }

    @GetMapping("/Cregister")
    public String CourseRegisterPage (Model model,HttpSession session) {

        System.out.println("REACHED COURSE REGISTER PAGE");

        List<String> Depts = mongoTemplate.findDistinct("courseDept", Course.class, String.class);
        List<Integer> levels = mongoTemplate.findDistinct("courselevel", Course.class, Integer.class);

        System.out.println("Depts: " + Depts.toString());
        System.out.println("levels: " + levels.toString());

        model.addAttribute("Depts", Depts);
        model.addAttribute("levels", levels);

        model.addAttribute("user",repository.findByUsernameAndRole((String) session.getAttribute("username"),(String) session.getAttribute("role")));

        return "CourseReg";
    }

    @GetMapping("/getCourses")
    @ResponseBody
    public List<Course> FindCourses(@RequestParam Map<String,String> allParams)
    {
        System.out.println("Parameters are " + allParams.entrySet());
        System.out.println("We are in the get course function");
        List<Course> courses = new ArrayList<Course>();
        if (allParams.containsKey("CourseID")) {
            courses.add(Courserepository.findByCourseCode(allParams.get("CourseID")));
        }
        else if (allParams.containsKey("CourseLevel") && allParams.containsKey("Subject")) {
            courses.addAll(Courserepository.findByCourselevelAndCourseDept(Integer.parseInt(allParams.get("CourseLevel")),allParams.get("Subject")));
        }
        else if (allParams.containsKey("CourseNum") && allParams.containsKey("Subject")){
            courses.addAll(Courserepository.findByCoursenumberAndCourseDept(Integer.parseInt(allParams.get("CourseNum")),allParams.get("Subject")));
        }
        else if (allParams.containsKey("CourseNum")) {
            courses.addAll(Courserepository.findBycoursenumber(Integer.parseInt(allParams.get("CourseNum"))));
        }
        else if (allParams.containsKey("Subject")){
            courses.addAll(Courserepository.findByCourseDept(allParams.get("Subject")));
        }
        else if (allParams.containsKey("CourseLevel")){
            courses.addAll(Courserepository.findByCourselevel(Integer.parseInt(allParams.get("CourseLevel"))));
        }
        else {
            courses.addAll(Courserepository.findAll());
        }
        //handler.register_student("Abdul","3004B");
        //courses.add(new Course("The Test Course","2400B",2000,2400,"Testing Dept"));

        return courses;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/Courseregistration")
    @ResponseBody
    public String CourseRegistration(@RequestBody JSONObject courses, Model model, HttpSession session) {
        System.out.println("We are registering the student");
        Object [] keys = courses.keySet().toArray();
        List<String> temp = new ArrayList<String>();
        for (int i = 0; i < keys.length; ++i) {
            String courseID = (String)courses.get(keys[i]);
            temp.add(courseID);
            handler.register_student((String) session.getAttribute("username"),courseID);
        }
        return "Registered in courses: " + temp.toString();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/deliverableSubmission")
    @ResponseBody
    public void deliverableSubmit(@RequestBody JSONObject dObject, Model model, HttpSession session){
        System.out.println("Adding the new deliverable entry...");

        //Creating new deliverable
        //Getting required parameters
        String dID = UUID.randomUUID().toString().replace("-", "");
        String course =  dObject.get("course").toString();
        String name =  dObject.get("name").toString();
        String description = dObject.get("desc").toString();
        int weight = Integer.parseInt(dObject.get("weighting").toString());
        int daysDue = Integer.parseInt(dObject.get("daysDue").toString());

        System.out.println("Deliverable details: ");
        System.out.println("DID: " + dID);
        System.out.println("Course: " + course);
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Weight: " + weight);
        System.out.println("Days due: " + daysDue);

        handler.Add_deliverable(session.getAttribute("username").toString(), course, dID);


        //Retrieving newly created deliverable then passing params
        Deliverable d = dRepository.findDeliverableByDeliverableID(dID);
        d.setName(name);
        d.setDetails(description);
        d.setWeighting(weight);
        d.setDueDate(daysDue);

        //Saving instance to server
        dRepository.save(d);

        System.out.print("Deliverable Entry Added - \nName of Deliverable: ");
        System.out.println(dRepository.findDeliverableByDeliverableID(dID).name);
        System.out.println("Owner (Username): " + d.owner);

    }

    @RequestMapping(method = RequestMethod.POST, value = "/deliverableDeletion")
    @ResponseBody
    public void deliverableDelete(@RequestBody JSONObject dObject, Model model, HttpSession session){
        System.out.println("Deleting the target deliverable...");

        //Finding deliverable to delete
        String dName = dObject.get("dName").toString();
        dRepository.delete(dRepository.findByownerAndName(session.getAttribute("username").toString(),dName));

        System.out.println("Deleted.");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/deliverableModification")
    @ResponseBody
    public void deliverableModify(@RequestBody JSONObject dObject, Model model, HttpSession session){
        System.out.println("Editing the target deliverable...");

        //Finding target deliverable
        String oldName = dObject.get("oldName").toString();
        Deliverable targetD = dRepository.findByownerAndName(session.getAttribute("username").toString(), oldName);

        //Parsing post and updating deliverable
        String newName = dObject.get("newName").toString();
        String newDescription = dObject.get("newDescription").toString();
        int newWeight = Integer.parseInt(dObject.get("newWeight").toString());
        int newDueDate = Integer.parseInt(dObject.get("newDueDate").toString());

        targetD.setName(newName);
        targetD.setDetails(newDescription);
        targetD.setWeighting(newWeight);
        targetD.setDueDate(newDueDate);

        //Saving instance to server
        dRepository.save(targetD);

        System.out.println("Target update successful");
    }
}