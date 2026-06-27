package com.stuffmanagement.staffmansys.leave;

import com.stuffmanagement.staffmansys.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "emp_id", foreignKey = @ForeignKey(name="fk_leave_employee"))
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", nullable = false, length = 20)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(length = 200)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_status", nullable = false, length = 20)
    private LeaveStatus status;
    

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @ManyToOne
    @JoinColumn(name = "approver_id", foreignKey = @ForeignKey(name="fk_leave_approver"))
    private Employee approver; // HR/SUPERUSER who approved/rejected
}
