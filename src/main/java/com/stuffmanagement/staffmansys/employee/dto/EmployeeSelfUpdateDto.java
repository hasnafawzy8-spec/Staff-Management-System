package com.stuffmanagement.staffmansys.employee.dto;

import lombok.Data;

@Data
public class EmployeeSelfUpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String nic;
    private String city;
    private String postalCode;
    private String street;
}
