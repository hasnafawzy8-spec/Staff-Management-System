package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.employee.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class EmployeeController {
    private final EmployeeService service;

    // EMPLOYEE: view their own profile
    @GetMapping("/account/profile")
    public String myProfile(@AuthenticationPrincipal User principal, Model model) {
        var emp = service.findByUsername(principal.getUsername());
        model.addAttribute("emp", emp);
        return "profile";
    }

    // HR: list + add employees
    @GetMapping("/admin/employees")
    public String list(Model model) {
        model.addAttribute("list", service.findAll());
        return "employees_list";
    }

    @GetMapping("/admin/employees/new")
    public String newEmp(Model model) {
        model.addAttribute("employee", Employee.builder().accessType(AccessType.EMPLOYEE).build());
        return "employee_form";
    }

    @PostMapping("/admin/employees")
    public String save(@AuthenticationPrincipal User principal,
                       @ModelAttribute Employee employee) {
        var creator = service.findByUsername(principal.getUsername());

        // HR cannot create SUPERUSER
        if (creator.getAccessType() == AccessType.HR && employee.getAccessType() == AccessType.SUPERUSER) {
            throw new RuntimeException("HR cannot create SUPERUSER accounts");
        }

        // HR cannot create other HRs unless allowed
        if (creator.getAccessType() == AccessType.HR && employee.getAccessType() == AccessType.HR) {
            throw new RuntimeException("HR cannot create HR accounts");
        }

        service.register(employee);
        return "redirect:/admin/employees?created";
    }

}
