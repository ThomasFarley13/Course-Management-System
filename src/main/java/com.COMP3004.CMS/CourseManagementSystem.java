package com.COMP3004.CMS;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


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

    @PostMapping("/createUserRequest")
    public String createUserRequest(@RequestParam String username,
                                    @RequestParam String password,
                                    @RequestParam String firstname,
                                    @RequestParam String lastname,
                                    @RequestParam(required = false) String date ){
        if(repository.findByUsername(username) != null && date == null){
            return "professor-create-taken";
        }
        else if(repository.findByUsername(username) != null){
            return "student-create-taken";
        }

        if(date == null){
            repository.save(factory.createUser(username, password, "Professor", repository.findTopByOrderByIdDesc().getId() +1, date, firstname, lastname));
        }
        else{
            repository.save(factory.createUser(username, password, "Student", repository.findTopByOrderByIdDesc().getId() +1, date, firstname, lastname));
        }
        return "create-successful";
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
        model.addAttribute("user",factory.createUser("Sepehr","Password423","Student", 7,"null","Dave","Ian"));

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
        courses.add(new Course("The Test Course","2400B",2000,2400,"Testing Dept"));

        return courses;
    }


}