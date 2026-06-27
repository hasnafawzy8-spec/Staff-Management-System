package com.stuffmanagement.staffmansys.department;

import com.stuffmanagement.staffmansys.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_dept_name", columnNames = "dept_name")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long id;

    @Column(name = "dept_name", nullable = false, length = 120)
    private String deptName;

    @Column(length = 255)
    private String description;


    @ManyToOne
    @JoinColumn(name = "admin_id")
    private com.stuffmanagement.staffmansys.employee.Employee admin;
}
