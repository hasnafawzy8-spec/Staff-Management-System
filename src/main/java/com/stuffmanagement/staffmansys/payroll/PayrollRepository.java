package com.stuffmanagement.staffmansys.payroll;

import com.stuffmanagement.staffmansys.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<PayrollRecord, Long> {
    List<PayrollRecord> findByEmployeeOrderByPeriodYearDescPeriodMonthDesc(Employee employee);
    Optional<PayrollRecord> findByEmployeeAndPeriodMonthAndPeriodYear(Employee e, int m, int y);
}
