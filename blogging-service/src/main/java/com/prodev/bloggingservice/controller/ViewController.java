package com.prodev.bloggingservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String homePage() {
        return "pages/index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "pages/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "pages/register";
    }


}
