USE StaffManagment;

SET FOREIGN_KEY_CHECKS=0;

-- Departments
INSERT INTO departments (dept_id, dept_name, description, admin_id) VALUES
  (1, 'HR', 'Human Resources', NULL),
  (2, 'Finance', 'Finance Department', NULL)
ON DUPLICATE KEY UPDATE dept_name=VALUES(dept_name);

-- Employees (explicit IDs so related records can reference them)
INSERT INTO employees (emp_id, access_type, city, dob, email, first_name, hire_date, last_name, nic, password, position, postal_code, status, street, username, dept_id) VALUES
  (1, 'SUPERUSER', 'Colombo', '1990-01-01', 'admin@example.com', 'Admin', '2020-01-01', 'User', 'NIC123', '{noop}admin
  ', 'Administrator', '10000', 'ACTIVE', 'Main St', 'admin', 1),
  (2, 'EMPLOYEE', 'Colombo', '1992-05-10', 'jane.doe@example.com', 'Jane', '2021-06-01', 'Doe', 'NIC456', '{noop}password', 'Engineer', '10001', 'ACTIVE', 'Second St', 'jane', 2)
ON DUPLICATE KEY UPDATE email=VALUES(email);

-- Set department admin
UPDATE departments SET admin_id = 1 WHERE dept_id = 1;

-- Attendance record for employee 2
INSERT INTO attendance_records (attendance_id, remarks, attendance_status, time_in, time_out, work_date, emp_id) VALUES
  (1, 'On time', 'PRESENT', '09:00:00', '17:00:00', '2026-05-01', 2)
ON DUPLICATE KEY UPDATE remarks=VALUES(remarks);

-- Leave request for employee 2
INSERT INTO leave_requests (leave_id, decision_date, end_date, leave_type, reason, start_date, leave_status, approver_id, emp_id) VALUES
  (1, NULL, '2026-06-05', 'ANNUAL', 'Family event', '2026-06-01', 'PENDING', 1, 2)
ON DUPLICATE KEY UPDATE reason=VALUES(reason);

-- Payroll record for employee 2
INSERT INTO payroll_records (id, allowances, attendance_hours, basic_salary, deductions, generated_at, gross_salary, leave_hours, net_hours, net_salary, overtime_hours, overtime_rate, period_month, period_year, emp_id) VALUES
  (1, 100.00, 160.00, 2000.00, 50.00, NOW(), 2100.00, 8.00, 152.00, 2050.00, 5.00, 20.00, 5, 2026, 2)
ON DUPLICATE KEY UPDATE basic_salary=VALUES(basic_salary);

-- Performance evaluation for employee 2
INSERT INTO performance_evaluations (id, attendance_score, comments, evaluated_by, evaluation_date, task_completion_score, teamwork_score, employee_id) VALUES
  (1, 8, 'Good work', 'Manager', '2026-05-15', 9, 8, 2)
ON DUPLICATE KEY UPDATE comments=VALUES(comments);

SET FOREIGN_KEY_CHECKS=1;
