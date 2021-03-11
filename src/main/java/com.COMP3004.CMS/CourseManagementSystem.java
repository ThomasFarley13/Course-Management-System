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
    public String home() {
        if (!logged_in) {
            return "forward:/login.html";
        } else {
            return "you are logged in";
        }
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
    public @ResponseBody
    String loginhandler(@ModelAttribute User user, Model model) {

        // fake authenitcation
        if (repository.findByUsernameAndPassword(user.getUsername(), user.getPassword()) != null) {
            return "loggedin";
        } else {
            // figure out message to send on fail and how to send it to the html
            return "/login";
        }
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

        return "dashboard";
    }

    @GetMapping("/loggedin")
    public String loginForm(@ModelAttribute User user, Model model) {
        model.addAttribute("user", new User(null, null));
        return "loggedin";
    }


}