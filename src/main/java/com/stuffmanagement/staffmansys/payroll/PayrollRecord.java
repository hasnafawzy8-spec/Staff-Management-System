package com.stuffmanagement.staffmansys.payroll;

import com.stuffmanagement.staffmansys.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_records",
        uniqueConstraints = @UniqueConstraint(name="uk_emp_period", columnNames = {"emp_id","period_month","period_year"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="emp_id", foreignKey = @ForeignKey(name="fk_payroll_employee"))
    private Employee employee;

    @Column(name="period_month", nullable=false)
    private int periodMonth; // 1..12
    @Column(name="period_year", nullable=false)
    private int periodYear;

    // Inputs / configuration for the period
    @Column(name="basic_salary", nullable=false, precision=12, scale=2)
    private BigDecimal basicSalary;         // monthly base
    @Column(name="overtime_rate", precision=12, scale=2)
    private BigDecimal overtimeRate;        // per hour
    @Column(precision=12, scale=2)
    private BigDecimal allowances;          // total add-ons (transport, etc.)
    @Column(precision=12, scale=2)
    private BigDecimal deductions;          // manual deductions

    // Calculated metrics
    @Column(name="attendance_hours", precision=10, scale=2)
    private BigDecimal attendanceHours;
    @Column(name="leave_hours", precision=10, scale=2)
    private BigDecimal leaveHours;
    @Column(name="net_hours", precision=10, scale=2)
    private BigDecimal netHours;
    @Column(name="overtime_hours", precision=10, scale=2)
    private BigDecimal overtimeHours;

    // Totals
    @Column(name="gross_salary", precision=12, scale=2)
    private BigDecimal grossSalary;
    @Column(name="net_salary", precision=12, scale=2)
    private BigDecimal netSalary;

    private LocalDateTime generatedAt;
}
