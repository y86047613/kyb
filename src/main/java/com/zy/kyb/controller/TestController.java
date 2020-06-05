package com.zy.kyb.controller;


import com.zy.kyb.dto.UserDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    @PreAuthorize("hasAuthority('user:test')")
    public String test(){
        return "test success";
    }

    @GetMapping("/authority")
    @PreAuthorize("hasAuthority('admin:test')")
    public String authority(){
        return "test authority success";
    }


    @GetMapping("/register")
    public String createUser(Model model){
        model.addAttribute("user",new UserDto());
        return "register";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("user",new UserDto());
        return "login";
    }



}
