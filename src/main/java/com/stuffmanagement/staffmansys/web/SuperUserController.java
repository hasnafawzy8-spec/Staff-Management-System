package com.stuffmanagement.staffmansys.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/super")
public class SuperUserController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "super_dashboard";
    }
}
