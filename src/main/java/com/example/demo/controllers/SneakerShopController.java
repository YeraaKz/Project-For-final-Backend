package com.example.demo.controllers;

import com.example.demo.models.Sneaker;
import com.example.demo.services.SneakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
public class SneakerShopController {
    @Autowired
    private SneakerService sneakerService;

    @GetMapping("/sneakers")
    public String showSneakers(Model model) {
        List<Sneaker> sneakers = sneakerService.findAll();
        model.addAttribute("sneakers", sneakers);
        return "sneakers";
    }

    @GetMapping("/sneakers/addForm")
    public String showAddSneakerForm(Model model) {
        model.addAttribute("sneaker", new Sneaker());
        return "add-sneaker";
    }
    // Не попадает в этот метод
    @PostMapping("/sneakers/add")
    public String addSneaker(@ModelAttribute("sneaker") Sneaker sneaker) {
        sneaker.setUsers(null);
        sneakerService.save(sneaker);
        return "redirect:/sneakers";
    }
}
