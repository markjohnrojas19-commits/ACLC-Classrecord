# ACLC Class Record System

A NetBeans Java Swing desktop application for ACLC instructors to manage students, subjects, and grades. Built with MySQL (XAMPP) for data persistence.

## Features

- **Login System** — Admin and Instructor roles with secure authentication
- **Student Management** — Add, edit, delete, and search students
- **Subject Management** — Add, edit, delete subjects with subject codes
- **Grade Management** — Input scores, auto-compute final grades, Pass/Fail remarks
- **Dashboard** — Summary statistics (total students, subjects, passed, failed)

## Tech Stack

| Tool | Purpose |
|------|---------|
| Java | Language |
| Java Swing | GUI framework |
| MySQL | Database |
| XAMPP | Local MySQL server |
| JDBC | Database connectivity |
| NetBeans IDE | Development environment |

## Prerequisites

- NetBeans IDE
- XAMPP (with MySQL running)
- MySQL Connector/J (JDBC driver)

## Setup

1. Start XAMPP and ensure MySQL is running
2. Import `sql/schema.sql` into phpMyAdmin to create the database
3. Open the project in NetBeans
4. Add MySQL Connector/J to project libraries
5. Run the application
