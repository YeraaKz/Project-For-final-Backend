package com.example.demo.controllers;

import com.example.demo.models.Role;
import com.example.demo.models.Sneaker;
import com.example.demo.models.User;
import com.example.demo.services.SneakerService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.GeneratedValue;
import java.util.List;

@Controller
public class AuthController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private SneakerService sneakerService;


    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, SneakerService sneakerService){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.sneakerService = sneakerService;
    }

    @GetMapping("/registerForm")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, BindingResult result) {
        System.out.println(user.toString());
        // check if user with the same username already exists
        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            result.rejectValue("username", null, "There is already an account registered with that username");
        }

        // create user if there are no errors
        if (result.hasErrors()) {
            return "registration";
        } else {
            user.setRole(Role.ROLE_USER); // set default role for new users
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setBalance(1000.0);
            user.setSneakers(null);
            userService.save(user);
            return "redirect:/loginForm";
        }
    }

    @GetMapping("/loginForm")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password) {

        User user = userService.findByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            System.out.println(password);
            System.out.println(user.getPassword());
            return "redirect:/loginForm";
        }

        return "redirect:/sneaker-shop/sneakers";
    }

}
