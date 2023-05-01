package com.example.demo.controllers;

import com.example.demo.models.Sneaker;
import com.example.demo.models.User;
import com.example.demo.services.SneakerService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.security.Principal;

@Controller
public class SneakerShopController {

    private final SneakerService sneakerService;

    private final UserService userService;

    @Autowired
    public SneakerShopController(SneakerService sneakerService, UserService userService) {
        this.sneakerService = sneakerService;
        this.userService = userService;
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
