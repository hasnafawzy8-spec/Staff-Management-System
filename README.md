StaffWave – Enterprise Employee Management System

🛠️ Tech Stack & Architecture

* **Backend Framework:** Java Spring Boot (v3.x)
* **Security Layer:** Spring Security (Form-Based Authentication, Delegating Password Encoders)
* **Data Access Layer:** Spring Data JPA / Hibernate (ORM)
* **Database Engine:** MySQL 9.x (Relational Schema with complex Foreign Key Constraints)
* **Frontend Template Engine:** Thymeleaf & HTML5
* **Styling Framework:** Tailwind CSS & Custom CSS Modern Interactive Layouts

* 🚀 Key Features

### 🔐 Advanced Security & RBAC
* **Role-Based Access Control:** Strict authorization routing mapped to discrete corporate roles including `SUPERUSER`, `HR`, `FINANCE`, and `EMPLOYEE`.
* **Dynamic User Sessions:** Leverages custom implementations of `UserDetailsService` to inject context-aware user permissions natively into the Spring Security Context.
* **Safe Authentication Filters:** Custom query overrides handling distinct database constraints seamlessly.

### 🏢 Database Architecture & Relationship Management
* Multi-table relational hierarchy binding `employees` to their respective structural `departments` via tight database foreign keys.
* Automatic constraint handling preventing orphan records or data inconsistencies during cascading updates or deletions.

### 💼 Operational Modules
* **Employee Directory:** Full Lifecycle management for staff indexing, status updates (`ACTIVE`, `INACTIVE`), and role transitions.
* **Department Infrastructure:** Tracking of managerial designations, descriptions, and dynamic administrative hierarchies.
* **Workforce Metrics:** Integrated sub-modules for Attendance tracking, Leave requests, and Finance/Payroll management.
