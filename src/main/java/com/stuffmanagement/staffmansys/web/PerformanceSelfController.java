// src/main/java/com/stuffmanagement/staffmansys/web/PerformanceSelfController.java
package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.employee.EmployeeService;
import com.stuffmanagement.staffmansys.performance.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account/performance")
public class PerformanceSelfController {

    private final PerformanceService performanceService;
    private final EmployeeService employeeService;

    @GetMapping
    public String myList(@AuthenticationPrincipal User principal, Model model) {
        var emp = employeeService.findByUsername(principal.getUsername());
        model.addAttribute("evaluations", performanceService.getByEmployee(emp));
        return "performance_my"; // matches performance_my.html
    }
}
