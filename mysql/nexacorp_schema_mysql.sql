CREATE DATABASE IF NOT EXISTS nexacorp;
USE nexacorp;

CREATE TABLE departments (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employees (
    id CHAR(36) PRIMARY KEY,
    department_id CHAR(36) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    role VARCHAR(100) NOT NULL,
    salary DECIMAL(12,2) NOT NULL,
    join_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
);

CREATE TABLE projects (
    id CHAR(36) PRIMARY KEY,
    department_id CHAR(36) NOT NULL,
    name VARCHAR(150) NOT NULL,
    budget DECIMAL(14,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(30) NOT NULL,
    CONSTRAINT fk_project_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
);

CREATE TABLE project_assignments (
    id CHAR(36) PRIMARY KEY,
    project_id CHAR(36) NOT NULL,
    employee_id CHAR(36) NOT NULL,
    allocation_pct INT NOT NULL,
    assigned_date DATE NOT NULL,
    CONSTRAINT chk_allocation_pct
        CHECK (allocation_pct BETWEEN 1 AND 100),
    CONSTRAINT fk_assignment_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id),
    CONSTRAINT fk_assignment_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id),
    CONSTRAINT uq_project_employee
        UNIQUE (project_id, employee_id)
);

CREATE TABLE customers (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    industry VARCHAR(100),
    country VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id CHAR(36) PRIMARY KEY,
    customer_id CHAR(36) NOT NULL,
    order_date DATE NOT NULL,
    total_amount DECIMAL(14,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    CONSTRAINT fk_order_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);

CREATE TABLE invoices (
    id CHAR(36) PRIMARY KEY,
    order_id CHAR(36) NOT NULL,
    invoice_date DATE NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    CONSTRAINT fk_invoice_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
);

CREATE TABLE payments (
    id CHAR(36) PRIMARY KEY,
    invoice_id CHAR(36) NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    method VARCHAR(50) NOT NULL,
    CONSTRAINT fk_payment_invoice
        FOREIGN KEY (invoice_id)
        REFERENCES invoices(id)
);