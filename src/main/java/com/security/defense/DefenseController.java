package com.security.defense;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefenseController {

    @Autowired
    Node node;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tStudentAlpha", node.getTStudentAlpha());
        model.addAttribute("dt", node.getDt());
        return "index";
    }
}