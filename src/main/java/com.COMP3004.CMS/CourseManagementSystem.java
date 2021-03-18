package com.COMP3004.CMS;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class CourseManagementSystem {

    boolean logged_in = false;
    String userHash;


    @Autowired
    private Database repository;

    @GetMapping("/")
    public String home(@ModelAttribute User user, Model model) {
        if (!logged_in) {
            return "forward:/login";
        } else {
            return "you are logged in";
        }
    }
    @GetMapping("/login")
    public String login(@ModelAttribute User user, Model model) {
        model.addAttribute("user", new User(null, null,null));
        return "loggedin";
    }

    /*@RequestMapping(value = "/login", method = RequestMethod.POST)
    public  ModelAndView login_handler(@RequestBody LoginForm loginForm, ModelMap model) {

        //your code

        // fake authenitcation
        if (loginForm.getUsername().equals("Seppy") && loginForm.getPassword().equals( "IH83004")) {
            System.out.println("Recived Valid Usernaame and password");

            return new ModelAndView("redirect:/hello", model);
        }
        else {
            // figure out message to send on fail and how to send it to the html

        }
        return null;

    }*/

    @PostMapping("/login")
    public String loginhandler(@ModelAttribute User user, Model model) {
        model.addAttribute(user);
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        // fake authenitcation
        if (repository.findByUsernameAndPassword(user.getUsername(), user.getPassword()) != null) {
            System.out.println("Hello2");
            return "dashboard";
        } else {
            // figure out message to send on fail and how to send it to the html
            return "loggedin";
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
            System.out.println("Hello2");
            return "dashboard";
        } else {
            // figure out message to send on fail and how to send it to the html
            return "loggedin";
        }
    }

    @PostMapping("/logout")
    public String logouthandler(@ModelAttribute User user, Model model) {
        model.addAttribute("user", new User(null, null,null));
        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        return "loggedin";
    }



    @GetMapping(value = "/hello")
    public ModelAndView hello(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        System.out.println("We are in the Hello mapping function");
        System.out.println(new ModelAndView("hello").toString());
        return new ModelAndView("hello");
    }

    @GetMapping(value = "/dashboardTest")
    public String dashboardTest(Model model) {
        System.out.println("We are in the DashboardTest mapping function");
        String[] coursenames = {"Comp3000", "Comp3001", "Comp3002", "Comp3003", "Comp3004"};
        String[] courselinks = {"courses/Comp3000", "courses/Comp3001", "courses/Comp3002", "courses/Comp3003", "courses/Comp3004"};

        model.addAttribute("courses", coursenames);
        model.addAttribute("links", courselinks);
        model.addAttribute("user",new User("Sepehr","Password423","Student"));

        return "dashboard";
    }


}