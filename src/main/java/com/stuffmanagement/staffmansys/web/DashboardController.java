package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final EmployeeService employeeService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        var emp = employeeService.findByUsername(auth.getName()); // returns Employee
        model.addAttribute("emp", emp);
        return "dashboard"; // templates/dashboard.html
    }

    // 🚫 DELETE any @GetMapping("/") method from here.
}
