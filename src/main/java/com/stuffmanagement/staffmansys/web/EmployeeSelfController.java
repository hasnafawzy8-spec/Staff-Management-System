package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.employee.EmployeeService;
import com.stuffmanagement.staffmansys.employee.dto.EmployeeSelfUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class EmployeeSelfController {

    private final EmployeeService service;

    @InitBinder("employeeSelfUpdateDto")
    void disallowImmutable(WebDataBinder binder) {
        binder.setDisallowedFields("username", "position", "hireDate", "deptId", "accessType");
    }

    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal User principal, Model model) {
        var emp = service.findByUsername(principal.getUsername());
        var dto = new EmployeeSelfUpdateDto();
        dto.setFirstName(emp.getFirstName());
        dto.setLastName(emp.getLastName());
        dto.setEmail(emp.getEmail());
        dto.setNic(emp.getNic());
        dto.setCity(emp.getCity());
        dto.setPostalCode(emp.getPostalCode());
        dto.setStreet(emp.getStreet());

        model.addAttribute("emp", emp);
        model.addAttribute("employeeSelfUpdateDto", dto);
        return "account_edit";
    }

    @PostMapping("/edit")
    public String handleEdit(@AuthenticationPrincipal User principal,
                             @ModelAttribute("employeeSelfUpdateDto") EmployeeSelfUpdateDto dto) {
        service.updateSelf(principal.getUsername(), dto);
        return "redirect:/account/profile?updated";
    }
}
