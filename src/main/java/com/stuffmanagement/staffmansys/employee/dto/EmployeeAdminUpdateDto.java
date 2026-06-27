package com.stuffmanagement.staffmansys.employee.dto;

import com.stuffmanagement.staffmansys.employee.AccessType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeAdminUpdateDto {
    // username is immutable by policy – not present here

    // personal
    private String firstName;
    private String lastName;
    private String email;
    private String nic;
    private String city;
    private String postalCode;
    private String street;

    // admin-managed
    private LocalDate hireDate;
    private String position;
    private Long deptId;          // selected from dropdown
    private String status;
    private AccessType accessType;
}
