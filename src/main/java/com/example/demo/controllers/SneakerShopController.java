package com.example.demo.controllers;

import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Sneaker;
import com.example.demo.models.User;
import com.example.demo.services.SneakerService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.security.Principal;
import java.util.NoSuchElementException;

@Controller
public class SneakerShopController {

    private final SneakerService sneakerService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SneakerShopController(SneakerService sneakerService, UserService userService, PasswordEncoder passwordEncoder) {
        this.sneakerService = sneakerService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/sneakers")
    public String showSneakers(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<Sneaker> sneakers = sneakerService.findAll();
        model.addAttribute("sneakers", sneakers);
        model.addAttribute("user", user);
        return "sneakers";
    }

    @GetMapping("/sneakers/user")
    public String userInfo(Model model, Principal principal){
        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<Sneaker> userSneakers = user.getSneakers();
        model.addAttribute("user", user);
        model.addAttribute("sneakers", userSneakers);
        return "userInfo";
    }

    @GetMapping("/sneakers/user/{id}/edit")
    public String editUserForm(@PathVariable("id") Long id, Model model){
        User userToBeUpdated = userService.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        model.addAttribute("user", userToBeUpdated);
        return "editUser";
    }

    @PostMapping("/sneakers/user/{id}")
    public String editUser(@PathVariable("id") Long id, @ModelAttribute("user") User user, @RequestParam("password")
                           String password){
        System.out.println("asda");
        User existingUser = userService.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        String encodedPassword = passwordEncoder.encode(password);
        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(encodedPassword);
        userService.save(existingUser);
        return "redirect:/sneakers/user";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/sneakers/admin/addForm")
    public String showAddSneakerForm(Model model) {
        model.addAttribute("sneaker", new Sneaker());
        return "add-sneaker";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/sneakers/admin/add")
    public String addSneaker(@ModelAttribute("sneaker") Sneaker sneaker) {
        sneaker.setUsers(null);
        sneakerService.save(sneaker);
        return "redirect:/sneakers";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/sneakers/admin/users")
    public String allUsers(Model model, Principal principal){
        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<User> users = userService.findAll();
        model.addAttribute("admin", user);
        model.addAttribute("users", users);
        return "allUsers";
    }

    @PostMapping("/sneakers/admin/users/delete/{id}")
    public String banUser(@PathVariable("id") Long id) {
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        userService.delete(user);
        return "redirect:/sneakers/admin/users";
    }

    @GetMapping("/sneakers/admin/users/{id}/recharge")
    public String rechargeUserForm(@PathVariable("id") Long id, Model model){
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        model.addAttribute("user", user);
        return "rechargeUser";
    }
    @PostMapping("/sneakers/admin/user/{id}")
    public String rechargeUserBalance(@PathVariable("id") Long id, @RequestParam("amount") int amount) {
        User user = userService.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setBalance(user.getBalance() + amount);
        userService.save(user);
        return "redirect:/sneakers/admin/users";
    }

    @PostMapping("/sneakers/{id}/buy")
    public String buySneaker(@PathVariable Long id, Principal principal) {
        Sneaker sneaker = sneakerService.findById(id);
        String username = principal.getName();
        User user = userService.findByUsername(username);
        if (sneaker.getPrice() <= user.getBalance()) {
            user.addSneaker(sneaker);
            double newBalance = user.getBalance() - sneaker.getPrice();
            user.setBalance(newBalance);
            userService.save(user);
        }
        return "redirect:/sneakers";
    }
}
