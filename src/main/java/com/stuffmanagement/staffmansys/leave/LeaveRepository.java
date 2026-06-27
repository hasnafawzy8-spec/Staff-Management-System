package com.stuffmanagement.staffmansys.leave;

import com.stuffmanagement.staffmansys.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeOrderByStartDateDesc(Employee employee);

    Optional<LeaveRequest> findByIdAndEmployeeUsername(Long id, String username);

    boolean existsByIdAndEmployeeUsernameAndStatus(Long id, String username, LeaveStatus status);
}
