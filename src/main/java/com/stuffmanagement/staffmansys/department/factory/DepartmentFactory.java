package com.stuffmanagement.staffmansys.department.factory;

import com.stuffmanagement.staffmansys.department.Department;
import com.stuffmanagement.staffmansys.employee.Employee;

/** Centralizes how a Department is constructed/normalized. */
public final class DepartmentFactory {

    private DepartmentFactory() {}

    public static Department create(String name, String description, Employee admin) {
        String normalizedName = normalizeName(name);
        Department d = Department.builder()
                .deptName(normalizedName)
                .description(normalizeText(description))
                .admin(admin)
                .build();
        return d;
    }

    /** Apply factory rules to an existing entity (for updates). */
    public static void apply(Department target, String name, String description, Employee admin) {
        target.setDeptName(normalizeName(name));
        target.setDescription(normalizeText(description));
        target.setAdmin(admin);
    }

    private static String normalizeName(String n) {
        if (n == null || n.isBlank()) throw new IllegalArgumentException("Department name is required");
        String trimmed = n.trim();
        if (trimmed.length() > 120) throw new IllegalArgumentException("Department name too long (max 120)");
        return trimmed;
    }

    private static String normalizeText(String t) {
        if (t == null) return null;
        String trimmed = t.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
