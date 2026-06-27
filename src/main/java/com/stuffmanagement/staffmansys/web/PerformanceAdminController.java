// src/main/java/com/stuffmanagement/staffmansys/web/PerformanceAdminController.java
package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.employee.EmployeeService;
import com.stuffmanagement.staffmansys.performance.PerformanceEvaluation;
import com.stuffmanagement.staffmansys.performance.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/performance")
public class PerformanceAdminController {

    private final PerformanceService performanceService;
    private final EmployeeService employeeService;

    @GetMapping
    public String listAll(Model model,
                          @RequestParam(value = "msg", required = false) String msg,
                          @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("evaluations", performanceService.getAll());
        if (msg != null) model.addAttribute("msg", msg);
        if (error != null) model.addAttribute("error", error);
        return "performance_all";
    }

    // ----- CREATE -----
    @GetMapping("/create/{empId}")
    public String showCreateForm(@PathVariable Long empId, Model model) {
        var emp = employeeService.getById(empId);
        model.addAttribute("employee", emp);
        model.addAttribute("evaluation", new PerformanceEvaluation());
        model.addAttribute("formTitle", "New Evaluation");
        model.addAttribute("formAction", "/admin/performance/create/" + empId);
        return "performance_form";
    }

    @PostMapping("/create/{empId}")
    public String create(@PathVariable Long empId,
                         @ModelAttribute("evaluation") PerformanceEvaluation dto,
                         @AuthenticationPrincipal User principal,
                         Model model) {
        try {
            performanceService.createEvaluation(empId, dto, principal.getUsername());
            return "redirect:/admin/performance?msg=Evaluation%20created";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("employee", employeeService.getById(empId));
            model.addAttribute("formTitle", "New Evaluation");
            model.addAttribute("formAction", "/admin/performance/create/" + empId);
            return "performance_form";
        }
    }

    // ----- EDIT -----
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var eval = performanceService.getById(id);
        model.addAttribute("employee", eval.getEmployee());
        model.addAttribute("evaluation", eval);
        model.addAttribute("formTitle", "Edit Evaluation");
        model.addAttribute("formAction", "/admin/performance/" + id + "/edit");
        return "performance_form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("evaluation") PerformanceEvaluation dto,
                         @AuthenticationPrincipal User principal,
                         Model model) {
        try {
            performanceService.update(id, dto, principal.getUsername());
            return "redirect:/admin/performance?msg=Evaluation%20updated";
        } catch (IllegalArgumentException ex) {
            var eval = performanceService.getById(id);
            model.addAttribute("employee", eval.getEmployee());
            model.addAttribute("evaluation", dto);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("formTitle", "Edit Evaluation");
            model.addAttribute("formAction", "/admin/performance/" + id + "/edit");
            return "performance_form";
        }
    }

    // ----- DELETE -----
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            performanceService.delete(id);
            return "redirect:/admin/performance?msg=Evaluation%20deleted";
        } catch (IllegalArgumentException ex) {
            return "redirect:/admin/performance?error=" + ex.getMessage().replace(" ", "%20");
        }
    }
}
