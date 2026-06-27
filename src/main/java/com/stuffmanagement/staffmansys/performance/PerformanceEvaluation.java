// src/main/java/com/stuffmanagement/staffmansys/performance/PerformanceEvaluation.java
package com.stuffmanagement.staffmansys.performance;

import com.stuffmanagement.staffmansys.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "performance_evaluations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PerformanceEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "fk_perf_employee"))
    private Employee employee;

    @Column(nullable = false)
    private LocalDate evaluationDate;

    // --- 3 KPIs ---
    @Column(nullable = false)
    private int attendanceScore;   // 1–5

    @Column(nullable = false)
    private int teamworkScore;     // 1–5

    @Column(nullable = false)
    private int taskCompletionScore; // 1–5

    private String comments;

    @Column(nullable = false)
    private String evaluatedBy; // HR username
}
