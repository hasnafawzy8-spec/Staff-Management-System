package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.department.DepartmentService;
import com.stuffmanagement.staffmansys.employee.EmployeeService;
import com.stuffmanagement.staffmansys.employee.dto.EmployeeAdminUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/employees")
public class AdminEmployeeController {

    private final EmployeeService service;
    private final DepartmentService deptService;

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var emp = service.findAll().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst().orElseThrow();

        var dto = new EmployeeAdminUpdateDto();
        dto.setFirstName(emp.getFirstName());
        dto.setLastName(emp.getLastName());
        dto.setEmail(emp.getEmail());
        dto.setNic(emp.getNic());
        dto.setCity(emp.getCity());
        dto.setPostalCode(emp.getPostalCode());
        dto.setStreet(emp.getStreet());
        dto.setHireDate(emp.getHireDate());
        dto.setPosition(emp.getPosition());
        dto.setStatus(emp.getStatus());
        dto.setAccessType(emp.getAccessType());
        if (emp.getDepartment() != null) dto.setDeptId(emp.getDepartment().getId());

        model.addAttribute("emp", emp);
        model.addAttribute("employeeAdminUpdateDto", dto);
        model.addAttribute("departments", deptService.all()); // for dropdown

        model.addAttribute("accessTypes", java.util.List.of(
                com.stuffmanagement.staffmansys.employee.AccessType.EMPLOYEE,
                com.stuffmanagement.staffmansys.employee.AccessType.HR,
                com.stuffmanagement.staffmansys.employee.AccessType.FINANCE,
                com.stuffmanagement.staffmansys.employee.AccessType.SUPERUSER
        ));

        return "admin_employee_edit";
    }


    @PostMapping("/{id}/edit")
    public String handleEdit(@PathVariable Long id,
                             @ModelAttribute("employeeAdminUpdateDto") EmployeeAdminUpdateDto dto) {
        service.updateByAdmin(id, dto);
        return "redirect:/admin/employees?updated";
    }


}
