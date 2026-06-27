package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.employee.Employee;
import com.stuffmanagement.staffmansys.employee.EmployeeService;
import com.stuffmanagement.staffmansys.employee.AccessType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/password")
public class PasswordController {

    private final EmployeeService service;

    // EMPLOYEE: reset own password
    @GetMapping("/reset")
    public String resetForm(@AuthenticationPrincipal User principal, Model model) {
        var emp = service.findByUsername(principal.getUsername());
        model.addAttribute("emp", emp);
        return "password_reset";
    }

    @PostMapping("/reset")
    public String resetSave(@AuthenticationPrincipal User principal,
                            @RequestParam String newPassword) {
        var emp = service.findByUsername(principal.getUsername());
        service.changePassword(emp, newPassword);
        return "redirect:/account/profile?pwdChanged";
    }

    // HR / SUPERUSER: reset others' password
    @GetMapping("/admin/{id}")
    public String adminResetForm(@PathVariable Long id, Model model) {
        model.addAttribute("empId", id);
        return "password_admin_reset";
    }

    @PostMapping("/admin/{id}")
    public String adminResetSave(@PathVariable Long id, @RequestParam String newPassword) {
        service.changePasswordById(id, newPassword);
        return "redirect:/admin/employees?pwdChanged";
    }
}
