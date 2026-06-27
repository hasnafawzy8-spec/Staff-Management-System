package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.employee.Employee;
import com.stuffmanagement.staffmansys.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final EmployeeService service;

    @GetMapping("/")
    public String home() { return "home"; }

    @GetMapping("/login")
    public String login() {
        System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("hr123"));
        return "login";
    }


}
