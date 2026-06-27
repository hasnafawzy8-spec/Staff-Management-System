package com.stuffmanagement.staffmansys.employee;

import com.stuffmanagement.staffmansys.department.Department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees", uniqueConstraints = {
        @UniqueConstraint(name="uk_employee_username", columnNames = "username"),
        @UniqueConstraint(name="uk_employee_email", columnNames = "email"),
        @UniqueConstraint(name="uk_employee_nic", columnNames = "nic")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long id;

    // Auth
    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false, length = 20)
    private AccessType accessType;

    // Profile
    @Column(name = "first_name", length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "nic", length = 25)
    private String nic;

    @Column(length = 80)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 180)
    private String street;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(length = 80)
    private String position;

    @Column(length = 20)
    private String status; // ACTIVE/INACTIVE

    /** Department relation replaces old Long deptId */
    @ManyToOne
    @JoinColumn(name = "dept_id", foreignKey = @ForeignKey(name = "fk_employee_department"))
    private Department department;
}
