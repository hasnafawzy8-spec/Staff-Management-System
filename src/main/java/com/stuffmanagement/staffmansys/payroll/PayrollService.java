package com.stuffmanagement.staffmansys.payroll;

import com.stuffmanagement.staffmansys.attendance.AttendanceRecord;
import com.stuffmanagement.staffmansys.attendance.AttendanceRepository;
import com.stuffmanagement.staffmansys.leave.LeaveRepository;
import com.stuffmanagement.staffmansys.leave.LeaveStatus;
import com.stuffmanagement.staffmansys.employee.Employee;
import com.stuffmanagement.staffmansys.employee.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;
    private final AttendanceRepository attendanceRepo;
    private final LeaveRepository leaveRepo;

    private static final BigDecimal BASE_MONTHLY_HOURS = new BigDecimal("160"); // 20 days * 8h
    private static final BigDecimal HOURS_PER_LEAVE_DAY = new BigDecimal("8");


    // Create
    public PayrollRecord generateOrUpdate(Long empId, int month, int year,
                                          BigDecimal basicSalary,
                                          BigDecimal overtimeRate,
                                          BigDecimal allowances,
                                          BigDecimal deductions) {

        Employee emp = employeeRepo.findById(empId).orElseThrow();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal attHours = calculateAttendanceHours(emp, start, end);
        BigDecimal leaveHours = calculateLeaveHours(emp, start, end);
        BigDecimal netHours = attHours.subtract(leaveHours).max(BigDecimal.ZERO);
        BigDecimal overtimeHours = netHours.subtract(BASE_MONTHLY_HOURS).max(BigDecimal.ZERO);

        BigDecimal gross = nz(basicSalary)
                .add(overtimeHours.multiply(nz(overtimeRate)))
                .add(nz(allowances))
                .subtract(nz(deductions));

        BigDecimal net = gross;

        PayrollRecord rec = payrollRepo
                .findByEmployeeAndPeriodMonthAndPeriodYear(emp, month, year)
                .orElseGet(PayrollRecord::new);

        rec.setEmployee(emp);
        rec.setPeriodMonth(month);
        rec.setPeriodYear(year);
        rec.setBasicSalary(nz(basicSalary));
        rec.setOvertimeRate(nz(overtimeRate));
        rec.setAllowances(nz(allowances));
        rec.setDeductions(nz(deductions));
        rec.setAttendanceHours(scale(attHours));
        rec.setLeaveHours(scale(leaveHours));
        rec.setNetHours(scale(netHours));
        rec.setOvertimeHours(scale(overtimeHours));
        rec.setGrossSalary(scale(gross));
        rec.setNetSalary(scale(net));
        rec.setGeneratedAt(LocalDateTime.now());

        return payrollRepo.save(rec);
    }

    /** Update an existing record (admin edit).
     *  Recomputes time-based fields from attendance/leaves for the given period,
     *  and applies edited money fields & period.
     */
    @Transactional
    public PayrollRecord update(Long id,
                                BigDecimal basicSalary,
                                BigDecimal overtimeRate,
                                BigDecimal allowances,
                                BigDecimal deductions,
                                Integer month,
                                Integer year) {

        PayrollRecord rec = payrollRepo.findById(id).orElseThrow();
        Employee emp = rec.getEmployee();

        // If period is changed by admin, use the new one; otherwise keep existing
        int m = (month != null && month >= 1 && month <= 12) ? month : rec.getPeriodMonth();
        int y = (year != null && year >= 1900) ? year : rec.getPeriodYear();

        LocalDate start = LocalDate.of(y, m, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal attHours = calculateAttendanceHours(emp, start, end);
        BigDecimal leaveHours = calculateLeaveHours(emp, start, end);
        BigDecimal netHours = attHours.subtract(leaveHours).max(BigDecimal.ZERO);
        BigDecimal overtimeHours = netHours.subtract(BASE_MONTHLY_HOURS).max(BigDecimal.ZERO);

        BigDecimal gross = nz(basicSalary != null ? basicSalary : rec.getBasicSalary())
                .add(overtimeHours.multiply(nz(overtimeRate != null ? overtimeRate : rec.getOvertimeRate())))
                .add(nz(allowances != null ? allowances : rec.getAllowances()))
                .subtract(nz(deductions != null ? deductions : rec.getDeductions()));

        BigDecimal net = gross;

        rec.setPeriodMonth(m);
        rec.setPeriodYear(y);
        if (basicSalary != null)   rec.setBasicSalary(scale(nz(basicSalary)));
        if (overtimeRate != null)  rec.setOvertimeRate(scale(nz(overtimeRate)));
        if (allowances != null)    rec.setAllowances(scale(nz(allowances)));
        if (deductions != null)    rec.setDeductions(scale(nz(deductions)));

        rec.setAttendanceHours(scale(attHours));
        rec.setLeaveHours(scale(leaveHours));
        rec.setNetHours(scale(netHours));
        rec.setOvertimeHours(scale(overtimeHours));
        rec.setGrossSalary(scale(gross));
        rec.setNetSalary(scale(net));
        rec.setGeneratedAt(LocalDateTime.now());

        return payrollRepo.save(rec);
    }

    public List<PayrollRecord> myPayrolls(String username) {
        Employee emp = employeeRepo.findByUsername(username).orElseThrow();
        return payrollRepo.findByEmployeeOrderByPeriodYearDescPeriodMonthDesc(emp);
    }

    public PayrollRecord get(Long id) { return payrollRepo.findById(id).orElseThrow(); }

    private BigDecimal calculateAttendanceHours(Employee emp, LocalDate start, LocalDate end) {
        List<AttendanceRecord> all = attendanceRepo.findAll().stream()
                .filter(a -> a.getEmployee().getId().equals(emp.getId()))
                .filter(a -> !a.getWorkDate().isBefore(start) && !a.getWorkDate().isAfter(end))
                .toList();
        long seconds = 0;
        for (AttendanceRecord r : all) {
            if (r.getTimeIn() != null && r.getTimeOut() != null) {
                Duration d = Duration.between(r.getTimeIn(), r.getTimeOut());
                seconds += Math.max(d.getSeconds(), 0);
            }
        }
        return new BigDecimal(seconds).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLeaveHours(Employee emp, LocalDate start, LocalDate end) {
        var leaves = leaveRepo.findAll().stream()
                .filter(l -> l.getEmployee().getId().equals(emp.getId()))
                .filter(l -> l.getStatus() == LeaveStatus.APPROVED)
                .filter(l -> !l.getEndDate().isBefore(start) && !l.getStartDate().isAfter(end))
                .toList();
        long days = 0;
        for (var l : leaves) {
            LocalDate s = l.getStartDate().isBefore(start) ? start : l.getStartDate();
            LocalDate e = l.getEndDate().isAfter(end) ? end : l.getEndDate();
            days += Duration.between(s.atStartOfDay(), e.plusDays(1).atStartOfDay()).toDays();
        }
        return HOURS_PER_LEAVE_DAY.multiply(new BigDecimal(days));
    }

    // Read

    public List<PayrollRecord> allPayrolls() { return payrollRepo.findAll(); }

    // Delete

    @Transactional
    public void deleteById(Long id) {
        payrollRepo.deleteById(id);
    }

    private static BigDecimal nz(BigDecimal v){ return v == null ? BigDecimal.ZERO : v; }
    private static BigDecimal scale(BigDecimal v){ return v.setScale(2, RoundingMode.HALF_UP); }
}
