package com.COMP3004.CMS;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;


@Controller
@ComponentScan("com")
public class CourseManagementSystem {

    User userLoggedIn;
    boolean logged_in = false;
    String userHash;

    UserCreateFactory factory = new User();

    @Autowired
    private Database repository;


    @GetMapping("/")
    public String home() {
        if (!logged_in) {
            return "forward:/login";
        } else {
            return "you are logged in";
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
    public Object loginhandler(@ModelAttribute User user, Model model, final RedirectAttributes redirectAttributes ) {
        model.addAttribute(user);
        //
        //This is a temp user instance until we get mongosessions working
        userLoggedIn = user; //
        //
        // fake authenitcation
        User loginUser = repository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        if (loginUser != null && loginUser.getActive()) {
            redirectAttributes.addFlashAttribute("User",user);
            return "redirect:/dashboard";
        } else {
            // figure out message to send on fail and how to send it to the html
            return "login";
        }
    }


    @GetMapping("/dashboard")
    public String dashboard(@ModelAttribute("User") User user, Model model) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }
        model.addAttribute(user);

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



    @PostMapping("/dashboard")
    public String dashboardhandler(@ModelAttribute User user, Model model) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }
        model.addAttribute(user);

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
    public String createDeliverable(@ModelAttribute("User") User user, Model model) {
        if(userLoggedIn != null){
            user = userLoggedIn;
        }
        model.addAttribute(user);

        if(repository.findByUsernameAndRole(user.getUsername(), "Professor") != null){
            return "create-deliverable";
        }
        else{
            return "error";
        }
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
    public String logouthandler(@ModelAttribute User user, Model model) {
        model.addAttribute("user", new User());
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
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


}