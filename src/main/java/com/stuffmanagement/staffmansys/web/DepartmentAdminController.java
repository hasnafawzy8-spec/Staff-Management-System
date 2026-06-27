package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.department.Department;
import com.stuffmanagement.staffmansys.department.DepartmentHasEmployeesException;
import com.stuffmanagement.staffmansys.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/departments")
public class DepartmentAdminController {

    private final DepartmentService deptService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("list", deptService.all());
        return "dept_list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("dept", deptService.get(id));
        model.addAttribute("employees", deptService.allEmployeesForAdminPick());
        return "dept_form";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("dept", new Department());
        model.addAttribute("employees", deptService.allEmployeesForAdminPick());
        return "dept_form";
    }

    @PostMapping
    public String create(@ModelAttribute("dept") Department dept,
                         @RequestParam(value = "adminEmployeeId", required = false) String adminIdStr,
                         RedirectAttributes ra) {
        Long adminId = (adminIdStr == null || adminIdStr.isBlank()) ? null : Long.valueOf(adminIdStr);
        deptService.createOrUpdate(dept, adminId);
        ra.addFlashAttribute("ok", "Department created.");
        return "redirect:/admin/departments";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("dept") Department incoming,
                         @RequestParam(value = "adminEmployeeId", required = false) String adminIdStr,
                         RedirectAttributes ra) {
        Long adminId = (adminIdStr == null || adminIdStr.isBlank()) ? null : Long.valueOf(adminIdStr);
        incoming.setId(id);
        deptService.createOrUpdate(incoming, adminId);   //  will set/clear admin
        ra.addFlashAttribute("ok", "Department updated.");
        return "redirect:/admin/departments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            deptService.deleteDepartment(id);
            ra.addFlashAttribute("msg", "Department deleted.");
        } catch (DepartmentHasEmployeesException ex) {
            ra.addFlashAttribute("error",
                    "Cannot delete department: there are " + ex.getCount() +
                            " employee(s) still assigned. Reassign or remove them first.");
        }
        return "redirect:/admin/departments";
    }
}
