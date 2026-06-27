package com.stuffmanagement.staffmansys.web;

import com.stuffmanagement.staffmansys.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/attendance")
public class AttendanceAdminController {

    private final AttendanceService service;

    // HR & SUPERUSER: view all (optionally filter by date)
    @GetMapping
    public String all(@RequestParam(required = false)
                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                      Model model) {
        model.addAttribute("list", date == null ? service.all() : service.byDate(date));
        model.addAttribute("selectedDate", date);
        return "attendance_all";
    }
}
