package com.COMP3004.CMS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class CourseManagementSystem {

    @Autowired
    private Database repository;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute User user, Model model) {
        model.addAttribute("user", new User(null, null));
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute User user, Model model) {
        model.addAttribute("user", user);
        if(repository.findByUsernameAndPassword(user.getUsername(), user.getPassword()) != null){
            return "home";
        }
        return "/login";
    }

}