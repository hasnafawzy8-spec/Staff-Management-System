package com.stuffmanagement.staffmansys.department.policy;

import com.stuffmanagement.staffmansys.department.Department;

/** Strategy for deciding if a department can be deleted. */
public interface DepartmentDeletionPolicy {
    /** Throws a RuntimeException if the department cannot be deleted. */
    void assertDeletable(Department dept);
}
