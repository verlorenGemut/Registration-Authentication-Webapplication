package com.task4.springbootdemo.controller;

import com.task4.springbootdemo.model.User;
import com.task4.springbootdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.*;

import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private static User currentUser = null;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping(value = "/home-page")
    public String homePage() {
        return "home-page";
    }

    @RequestMapping(value = "/login-error")
    public String loginError() {
        return "login-error";
    }


    @GetMapping("/users")
    public String findAll(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user-list";
    }

    @GetMapping("/user-create")
    public String createUserForm(User user) {
        return "user-create";
    }

    @PostMapping("/user-create")
    public String createUser(User user) {
        Date date = new Date();
        user.setRegistrationDate(date.toString());
        user.setLastLoginDate(date.toString());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        currentUser = user;
        return "redirect:/users";
    }

    @GetMapping("/login-page")
    public String logInForm(User user) {
        return "login-page";
    }

    @PostMapping("/login-page")
    public String logIn(User user) {
        List<User> users = userService.findAll();

        for (User u : users) {
            System.out.println(u.toString());
        }

        for (User currentUser : users) {
            if (currentUser.getName().equals(user.getName())
                    && bCryptPasswordEncoder.matches(user.getPassword(),currentUser.getPassword())
                    && !currentUser.isBlocked()) {
                Date date = new Date();
                if (userService.findById(currentUser.getId()).isPresent()) {
                    userService.findById(currentUser.getId()).get().setLastLoginDate(date.toString());
                    userService.saveUser(userService.findById(currentUser.getId()).get());
                }
                currentUser = user;
                return "redirect:/users";
            }
        }
        return "redirect:/login-error";
    }

    @RequestMapping("/user-delete/{id}")
    public String deleteUsers(@PathVariable("id") Long id) {
        userService.deleteById(id);
        if (currentUser.getId().equals(id)) {
            currentUser = null;
            return "redirect:/home-page";
        }
        return "redirect:/users";
    }

    @RequestMapping("/users-delete-selected/{ids}")
    public String deleteSelected(@PathVariable("ids") Long[] ids) {
        for(Long id : ids) {
            userService.deleteById(id);
        }
        return "redirect:/users";
    }

    @GetMapping("user-block/{id}")
    public String blockUser(@PathVariable("id") Long id) {
        if (userService.findById(id).isPresent()) {
            userService.findById(id).get().setBlocked(true);
            userService.saveUser(userService.findById(id).get());
        }
        if (id.equals(currentUser.getId())) {
            currentUser = null;
            return "redirect:/home-page";
        }
        return "redirect:/users";
    }

    @GetMapping("user-unblock/{id}")
    public String unblockUser(@PathVariable("id") Long id) {
        if (userService.findById(id).isPresent()) {
            userService.findById(id).get().setBlocked(false);
            userService.saveUser(userService.findById(id).get());
        }
        return "redirect:/users";
    }

}
