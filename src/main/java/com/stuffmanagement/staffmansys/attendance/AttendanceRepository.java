package com.stuffmanagement.staffmansys.attendance;

import com.stuffmanagement.staffmansys.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByEmployeeOrderByWorkDateDesc(Employee employee);
    List<AttendanceRecord> findByWorkDateOrderByEmployee_IdAsc(LocalDate date);

    Optional<AttendanceRecord> findFirstByEmployeeAndWorkDateAndTimeOutIsNullOrderByTimeInDesc(
            Employee employee, LocalDate workDate);
}
