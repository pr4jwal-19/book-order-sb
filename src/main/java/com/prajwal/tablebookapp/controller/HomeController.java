package com.prajwal.tablebookapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {

    @RequestMapping("/welcome")
    public String welcome() {
        return "Welcome to Table Booking App!";
    }



}
