package com.stuffmanagement.staffmansys.attendance;

import com.stuffmanagement.staffmansys.employee.Employee;
import com.stuffmanagement.staffmansys.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository repo;
    private final EmployeeRepository employeeRepo;

    public AttendanceRecord addForUser(String username, AttendanceRecord rec) {
        Employee emp = employeeRepo.findByUsername(username).orElseThrow();
        rec.setEmployee(emp);
        if (rec.getWorkDate() == null) rec.setWorkDate(LocalDate.now());
        return repo.save(rec);
    }

    public List<AttendanceRecord> my(String username) {
        Employee emp = employeeRepo.findByUsername(username).orElseThrow();
        return repo.findByEmployeeOrderByWorkDateDesc(emp);
    }

    public List<AttendanceRecord> all() {
        return repo.findAll();
    }

    public List<AttendanceRecord> byDate(LocalDate date) {
        return repo.findByWorkDateOrderByEmployee_IdAsc(date);
    }

    public AttendanceRecord punch(String username, boolean in) {
        Employee emp = employeeRepo.findByUsername(username).orElseThrow();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        var openOpt = repo.findFirstByEmployeeAndWorkDateAndTimeOutIsNullOrderByTimeInDesc(emp, today);

        if (in) {
            AttendanceRecord rec = AttendanceRecord.builder()
                    .employee(emp)
                    .workDate(today)
                    .timeIn(now)
                    .status(AttendanceStatus.PRESENT)
                    .build();
            return repo.save(rec);
        } else {
            if (openOpt.isPresent()) {
                AttendanceRecord rec = openOpt.get();
                rec.setTimeOut(now);
                return repo.save(rec);
            } else {
                AttendanceRecord rec = AttendanceRecord.builder()
                        .employee(emp)
                        .workDate(today)
                        .timeOut(now)
                        .status(AttendanceStatus.PRESENT)
                        .build();
                return repo.save(rec);
            }
        }
    }

}
