// src/main/java/com/stuffmanagement/staffmansys/performance/PerformanceRepository.java
package com.stuffmanagement.staffmansys.performance;

import com.stuffmanagement.staffmansys.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PerformanceRepository extends JpaRepository<PerformanceEvaluation, Long> {
    List<PerformanceEvaluation> findByEmployeeOrderByEvaluationDateDesc(Employee employee);
}
