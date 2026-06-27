// src/main/java/com/stuffmanagement/staffmansys/performance/PerformanceService.java
package com.stuffmanagement.staffmansys.performance;

import com.stuffmanagement.staffmansys.employee.Employee;
import com.stuffmanagement.staffmansys.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository repo;
    private final EmployeeRepository employeeRepo;

    public PerformanceEvaluation createEvaluation(Long empId, PerformanceEvaluation dto, String hrUser) {
        Employee emp = employeeRepo.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + empId));

        var eval = PerformanceEvaluation.builder()
                .employee(emp)
                .attendanceScore(dto.getAttendanceScore())
                .teamworkScore(dto.getTeamworkScore())
                .taskCompletionScore(dto.getTaskCompletionScore())
                .comments(dto.getComments())
                .evaluationDate(dto.getEvaluationDate() != null ? dto.getEvaluationDate() : java.time.LocalDate.now())
                .evaluatedBy(hrUser)
                .build();

        return repo.save(eval);
    }

    // NEW
    public PerformanceEvaluation getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evaluation not found: " + id));
    }

    // NEW
    public PerformanceEvaluation update(Long id, PerformanceEvaluation dto, String hrUser) {
        var existing = getById(id);
        existing.setAttendanceScore(dto.getAttendanceScore());
        existing.setTeamworkScore(dto.getTeamworkScore());
        existing.setTaskCompletionScore(dto.getTaskCompletionScore());
        existing.setComments(dto.getComments());
        // keep original employee/date; record latest editor
        existing.setEvaluatedBy(hrUser);
        return repo.save(existing);
    }

    // NEW
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Evaluation not found: " + id);
        }
        repo.deleteById(id);
    }

    public List<PerformanceEvaluation> getByEmployee(Employee emp) {
        return repo.findByEmployeeOrderByEvaluationDateDesc(emp);
    }

    public List<PerformanceEvaluation> getAll() {
        return repo.findAll();
    }
}
