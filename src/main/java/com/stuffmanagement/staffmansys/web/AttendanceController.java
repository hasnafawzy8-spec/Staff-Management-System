package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.attendance.AttendanceRecord;
import com.stuffmanagement.staffmansys.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService service;

    // Employee: view own attendance
    @GetMapping
    public String myAttendance(@AuthenticationPrincipal User principal, Model model) {
        model.addAttribute("list", service.my(principal.getUsername()));
        return "attendance_my";
    }


    @GetMapping("/punch")
    public String punchForm() {
        return "attendance_punch";
    }

    @PostMapping("/punch")
    public String doPunch(@AuthenticationPrincipal User principal,
                          @RequestParam("action") String action) {
        boolean in = "IN".equalsIgnoreCase(action);
        service.punch(principal.getUsername(), in);
        return "redirect:/attendance?punched";
    }
}
