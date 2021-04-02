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


    @Autowired
    private UserDatabase repository;

    @Autowired
    private CourseDatabase Courserepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    DatabaseHandler handler ;


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
        System.out.println("We got to the dashboard Function");
        System.out.println(Courserepository.findByCourseCode("3004B"));
        System.out.println(repository.findByUsername("Abdul"));
        if (session.getAttribute("logged_in") != null && ((boolean) session.getAttribute("logged_in")) ) {
            System.out.println("We got to the dashboard Function Login 4");
            User user = repository.findByUsernameAndRole((String) session.getAttribute("username"),(String) session.getAttribute("role"));
            System.out.println("We got to the dashboard Function Login 3");
            model.addAttribute("user",user);
            System.out.println("We got to the dashboard Function Login 1");
            System.out.println(user.getRole());
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
                //Get required info here?
                return "create-deliverable";
            }
            else{
                return "error";
            }
        }
        return "error";
    }

    @GetMapping("/deleteDeliverable")
    public String deleteDeliverable(@ModelAttribute("User") User user, Model model) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }
        model.addAttribute(user);

        if(repository.findByUsernameAndRole(user.getUsername(), "Professor") != null){
            return "delete-deliverable";
        }
        else{
            return "error";
        }
    }

    @GetMapping("/modifyDeliverable")
    public String modifyDeliverable(@ModelAttribute("User") User user, Model model) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }
        model.addAttribute(user);

        if(repository.findByUsernameAndRole(user.getUsername(), "Professor") != null){
            return "modify-deliverable";
        }
        else{
            return "error";
        }
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
                                    @RequestParam String courseCode){
        System.out.println("Received new course's data, Code: " + courseCode + ", Name: " + courseName);
        if(Courserepository.findByCourseCode(courseCode) != null){
            System.out.println("Submitted course code exists");
            return "create-course-exists";
        }

        System.out.println("Saving new course " + courseName);
        Courserepository.save(new Course(courseName, courseCode));
        return "course-create-successful";
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


}