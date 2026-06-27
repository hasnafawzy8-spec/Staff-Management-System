package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.employee.EmployeeRepository;
import com.stuffmanagement.staffmansys.payroll.PayrollPdf;
import com.stuffmanagement.staffmansys.payroll.PayrollRecord;
import com.stuffmanagement.staffmansys.payroll.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class PayrollController {

    private final PayrollService service;
    private final EmployeeRepository employeeRepo;

    // ===================== SELF =====================

    // list my payrolls
    @GetMapping("/payroll/my")
    public String myPayrolls(@AuthenticationPrincipal User principal, Model model) {
        model.addAttribute("list", service.myPayrolls(principal.getUsername()));
        return "payroll_my";
    }

    // download my payroll as PDF (by id)
    @GetMapping("/payroll/my/{id}/pdf")
    public ResponseEntity<ByteArrayResource> myPayrollPdf(@AuthenticationPrincipal User principal,
                                                          @PathVariable Long id) {
        PayrollRecord p = service.get(id);
        var me = employeeRepo.findByUsername(principal.getUsername()).orElseThrow();
        if (!p.getEmployee().getId().equals(me.getId())) {
            return ResponseEntity.status(403).build();
        }
        byte[] pdf = PayrollPdf.render(p);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=payroll_" + p.getPeriodYear() + "_" + p.getPeriodMonth() + ".pdf")
                .body(new ByteArrayResource(pdf));
    }



    // list all payrolls  (Read)
    @GetMapping("/admin/payrolls")
    public String allPayrolls(Model model) {
        model.addAttribute("list", service.allPayrolls());
        return "payroll_all";
    }

    // quick generate endpoint (calculate & save) for a specific employee + period
    @PostMapping("/admin/payrolls/generate")
    public String generate(@RequestParam Long empId,
                           @RequestParam int month,
                           @RequestParam int year,
                           @RequestParam BigDecimal basicSalary,
                           @RequestParam(defaultValue = "0") BigDecimal overtimeRate,
                           @RequestParam(defaultValue = "0") BigDecimal allowances,
                           @RequestParam(defaultValue = "0") BigDecimal deductions,
                           RedirectAttributes ra) {
        try {
            service.generateOrUpdate(empId, month, year, basicSalary, overtimeRate, allowances, deductions);
            ra.addFlashAttribute("ok", "Payroll generated/updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error generating payroll: " + e.getMessage());
        }
        return "redirect:/admin/payrolls";
    }

    // download any payroll as PDF (finance/admin)
    @GetMapping("/admin/payrolls/{id}/pdf")
    public ResponseEntity<ByteArrayResource> adminPayrollPdf(@PathVariable Long id) {
        byte[] pdf = PayrollPdf.render(service.get(id));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payroll_" + id + ".pdf")
                .body(new ByteArrayResource(pdf));
    }

    // ===== Edit form =====
    @GetMapping("/admin/payrolls/{id}/edit")
    public String editPayroll(@PathVariable Long id, Model model) {
        model.addAttribute("record", service.get(id));
        return "payroll_edit";
    }

    // ===== Update submit =====
    @PostMapping("/admin/payrolls/{id}/edit")
    public String updatePayroll(@PathVariable Long id,
                                @ModelAttribute("record") PayrollRecord r,
                                RedirectAttributes ra,
                                Model model) {
        try {
            service.update(
                    id,
                    r.getBasicSalary(),
                    r.getOvertimeRate(),
                    r.getAllowances(),
                    r.getDeductions(),
                    r.getPeriodMonth(),
                    r.getPeriodYear()
            );
            ra.addFlashAttribute("ok", "Payroll updated successfully.");
            return "redirect:/admin/payrolls";
        } catch (Exception e) {
            // redisplay form with error
            model.addAttribute("error", e.getMessage());
            model.addAttribute("record", service.get(id));
            return "payroll_edit";
        }
    }

    // delete
    @PostMapping("/admin/payrolls/{id}/delete")
    public String deletePayroll(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.deleteById(id);
            ra.addFlashAttribute("ok", "Payroll deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error deleting payroll: " + e.getMessage());
        }
        return "redirect:/admin/payrolls";
    }
}
