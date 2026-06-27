package com.stuffmanagement.staffmansys.leave;

import com.stuffmanagement.staffmansys.employee.Employee;
import com.stuffmanagement.staffmansys.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRepository repo;
    private final EmployeeRepository employeeRepo;

    /* ---------- Create & list ---------- */

    @Transactional
    public LeaveRequest submit(String username, LeaveRequest req) {
        Employee emp = employeeRepo.findByUsername(username).orElseThrow();
        if (req.getStartDate() == null || req.getEndDate() == null || req.getStartDate().isAfter(req.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        req.setEmployee(emp);
        req.setStatus(LeaveStatus.PENDING);
        req.setApprover(null);
        req.setDecisionDate(null);
        return repo.save(req);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> my(String username) {
        Employee emp = employeeRepo.findByUsername(username).orElseThrow();
        return repo.findByEmployeeOrderByStartDateDesc(emp);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> all() {
        return repo.findAll();
    }

    /* ---------- Approve/Reject by HR/SUPERUSER (unchanged) ---------- */

    @Transactional
    public void decide(Long id, String approverUsername, boolean approve) {
        var req = repo.findById(id).orElseThrow();
        var approver = employeeRepo.findByUsername(approverUsername).orElseThrow();
        req.setApprover(approver);
        req.setStatus(approve ? LeaveStatus.APPROVED : LeaveStatus.REJECTED);
        req.setDecisionDate(LocalDateTime.now());
        repo.save(req);
    }

    /* ---------- NEW: Self edit/delete while PENDING ---------- */

    @Transactional(readOnly = true)
    public LeaveRequest myRequestOrThrow(String username, Long id) {
        return repo.findByIdAndEmployeeUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
    }

    @Transactional
    public LeaveRequest updateMyPending(String username, Long id, LeaveRequest form) {
        var req = myRequestOrThrow(username, id);
        if (req.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be edited");
        }
        if (form.getStartDate() == null || form.getEndDate() == null || form.getStartDate().isAfter(form.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        // Editable fields
        req.setLeaveType(form.getLeaveType());
        req.setStartDate(form.getStartDate());
        req.setEndDate(form.getEndDate());
        req.setReason(form.getReason());

        return repo.save(req);
    }

    @Transactional
    public void deleteMyPending(String username, Long id) {
        boolean allowed = repo.existsByIdAndEmployeeUsernameAndStatus(id, username, LeaveStatus.PENDING);
        if (!allowed) throw new IllegalStateException("Only your own PENDING requests can be deleted");
        repo.deleteById(id);
    }
}
