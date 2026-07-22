package com.ppip.cutidinas.controller;

import com.ppip.cutidinas.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String dashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails, HttpSession session) {
        model.addAttribute("namaUser", userDetails.getFullName());
        session.setAttribute("currentModule", "main");
        return "index";
    }
}
