package com.stuffmanagement.staffmansys.department;

import com.stuffmanagement.staffmansys.department.factory.DepartmentFactory;
import com.stuffmanagement.staffmansys.department.policy.DepartmentDeletionPolicy;
import com.stuffmanagement.staffmansys.employee.Employee;
import com.stuffmanagement.staffmansys.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository repo;
    private final EmployeeRepository employeeRepo;

    // NEW: strategy injected (ActiveUsersDeletionPolicy by default via @Component)
    private final DepartmentDeletionPolicy deletionPolicy;

    public List<Department> all() { return repo.findAll(); }
    public Department get(Long id) { return repo.findById(id).orElseThrow(); }

    public List<Employee> allEmployeesForAdminPick() {
        return employeeRepo.findAll();
    }

    @Transactional
    public Department createOrUpdate(Department incoming, Long adminEmployeeId) {
        Employee admin = (adminEmployeeId != null) ? employeeRepo.findById(adminEmployeeId).orElse(null) : null;

        // Create
        if (incoming.getId() == null) {
            Department d = DepartmentFactory.create(incoming.getDeptName(), incoming.getDescription(), admin);
            return repo.save(d);
        }

        // Update
        Department existing = repo.findById(incoming.getId()).orElseThrow();
        DepartmentFactory.apply(existing, incoming.getDeptName(), incoming.getDescription(), admin);
        return repo.save(existing);
    }

    public boolean deleteIfEmpty(Long id) {
        long count = employeeRepo.countByDepartment_Id(id);
        if (count > 0) return false;
        repo.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public long countEmployeesInDepartment(Long deptId) {
        return employeeRepo.countByDepartment_Id(deptId);
    }

    @Transactional
    public void deleteDepartment(Long deptId) {
        Department d = repo.findById(deptId).orElseThrow();

        //  use strategy to enforce the rule
        deletionPolicy.assertDeletable(d);

        try {
            repo.deleteById(deptId);
        } catch (DataIntegrityViolationException ex) {
            // DB-level safety net in case of FK RESTRICT, unknown exact count here
            throw new DepartmentHasEmployeesException(1);
        }
    }
}
