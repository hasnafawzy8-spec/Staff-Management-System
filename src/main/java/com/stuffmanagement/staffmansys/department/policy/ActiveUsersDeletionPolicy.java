package com.stuffmanagement.staffmansys.department.policy;

import com.stuffmanagement.staffmansys.department.Department;
import com.stuffmanagement.staffmansys.department.DepartmentHasEmployeesException;
import com.stuffmanagement.staffmansys.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Default policy: block deletion when employees are assigned. */
@Component
@RequiredArgsConstructor
public class ActiveUsersDeletionPolicy implements DepartmentDeletionPolicy {

    private final EmployeeRepository employeeRepo;

    @Override
    public void assertDeletable(Department dept) {
        long count = employeeRepo.countByDepartment_Id(dept.getId());
        if (count > 0) throw new DepartmentHasEmployeesException(count);
    }
}
