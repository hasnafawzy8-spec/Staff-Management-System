package com.stuffmanagement.staffmansys.department;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByDeptNameIgnoreCase(String deptName);
    }
