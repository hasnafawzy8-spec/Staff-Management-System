package com.stuffmanagement.staffmansys.employee;

import com.stuffmanagement.staffmansys.department.DepartmentService;
import com.stuffmanagement.staffmansys.employee.dto.EmployeeAdminUpdateDto;
import com.stuffmanagement.staffmansys.employee.dto.EmployeeSelfUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repo;
    private final PasswordEncoder encoder;
    private final DepartmentService departmentService;

    public Employee register(Employee e) {
        e.setPassword(encoder.encode(e.getPassword()));
        if (e.getAccessType() == null) e.setAccessType(AccessType.EMPLOYEE);
        return repo.save(e);
    }

    public List<Employee> findAll() { return repo.findAll(); }

    public Employee findByUsername(String username) {
        return repo.findByUsername(username).orElseThrow();
    }

    public void changePassword(Employee emp, String newPassword) {
        emp.setPassword(encoder.encode(newPassword));
        repo.save(emp);
    }

    public void changePasswordById(Long id, String newPassword) {
        var emp = repo.findById(id).orElseThrow();
        emp.setPassword(encoder.encode(newPassword));
        repo.save(emp);
    }

    /** Employee edits own personal fields – immutable: username, position, hireDate, department, accessType */
    public Employee updateSelf(String username, EmployeeSelfUpdateDto dto) {
        var emp = repo.findByUsername(username).orElseThrow();

        emp.setFirstName(dto.getFirstName());
        emp.setLastName(dto.getLastName());
        emp.setEmail(dto.getEmail());
        emp.setNic(dto.getNic());
        emp.setCity(dto.getCity());
        emp.setPostalCode(dto.getPostalCode());
        emp.setStreet(dto.getStreet());


        return repo.save(emp);
    }

    /** HR/SUPERUSER edits any employee – can set position/hireDate/department/accessType */
    public Employee updateByAdmin(Long id, EmployeeAdminUpdateDto dto) {
        var emp = repo.findById(id).orElseThrow();

        emp.setFirstName(dto.getFirstName());
        emp.setLastName(dto.getLastName());
        emp.setEmail(dto.getEmail());
        emp.setNic(dto.getNic());
        emp.setCity(dto.getCity());
        emp.setPostalCode(dto.getPostalCode());
        emp.setStreet(dto.getStreet());

        emp.setHireDate(dto.getHireDate());
        emp.setPosition(dto.getPosition());
        emp.setStatus(dto.getStatus());

        if (dto.getAccessType() != null) {
            emp.setAccessType(dto.getAccessType());
        }

        if (dto.getDeptId() != null) {
            var dept = departmentService.get(dto.getDeptId());
            emp.setDepartment(dept);                 //  relation (no setDeptId)
        } else {
            emp.setDepartment(null);                 // or keep existing
        }

        return repo.save(emp);
    }
    public Employee getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
    }

}
