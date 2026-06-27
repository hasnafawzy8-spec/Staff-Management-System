package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.leave.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/leaves")
public class LeaveAdminController {

    private final LeaveService service;

    @GetMapping
    public String all(Model model) {
        model.addAttribute("list", service.all());
        return "leave_all";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, @AuthenticationPrincipal User principal) {
        service.decide(id, principal.getUsername(), true);
        return "redirect:/admin/leaves?approved";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, @AuthenticationPrincipal User principal) {
        service.decide(id, principal.getUsername(), false);
        return "redirect:/admin/leaves?rejected";
    }
}
