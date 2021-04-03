package com.prodev.bloggingservice.controller;

import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
public class ViewController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping({ "/index", "/" })
    public String homePage() {
        return "pages/index";
    }

    @RequestMapping("/login")
    public String loginPage() {
        return "pages/login";
    }

    @RequestMapping("/register")
    public String registerPage() {
        return "pages/register";
    }

  /*  @PostMapping("/submit-registration")
    public String addUser(@ModelAttribute("userDto") @Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "pages/register";
        }
        userRepository.save(user);
        return "redirect:/";
    }*/

}
