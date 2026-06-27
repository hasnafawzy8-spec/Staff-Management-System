package com.stuffmanagement.staffmansys.department;

public class DepartmentHasEmployeesException extends RuntimeException {
    private final long count;

    public DepartmentHasEmployeesException(long count) {
        super("Department has " + count + " active employee(s).");
        this.count = count;
    }

    public long getCount() { return count; }
}