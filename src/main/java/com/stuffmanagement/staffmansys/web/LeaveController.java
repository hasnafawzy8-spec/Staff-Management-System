package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.leave.LeaveRequest;
import com.stuffmanagement.staffmansys.leave.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/leaves")
public class LeaveController {

    private final LeaveService service;

    /* List my leaves */
    @GetMapping
    public String myLeaves(@AuthenticationPrincipal User principal, Model model) {
        model.addAttribute("list", service.my(principal.getUsername()));
        return "leave_my";
    }

    /* Create new */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("req", new LeaveRequest());
        model.addAttribute("editing", false);
        return "leave_form";
    }

    @PostMapping
    public String submit(@AuthenticationPrincipal User principal,
                         @ModelAttribute("req") LeaveRequest req) {
        service.submit(principal.getUsername(), req);
        return "redirect:/leaves?submitted";
    }

    /* ---------- NEW: Edit self PENDING ---------- */

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal User principal,
                           Model model) {
        var existing = service.myRequestOrThrow(principal.getUsername(), id);
        model.addAttribute("req", existing);
        model.addAttribute("editing", true);
        return "leave_form";
    }

    @PostMapping("/{id}/edit")
    public String handleEdit(@PathVariable Long id,
                             @AuthenticationPrincipal User principal,
                             @ModelAttribute("req") LeaveRequest form) {
        service.updateMyPending(principal.getUsername(), id, form);
        return "redirect:/leaves?updated";
    }

    /* ---------- NEW: Delete self PENDING ---------- */

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal User principal) {
        service.deleteMyPending(principal.getUsername(), id);
        return "redirect:/leaves?deleted";
    }
}
