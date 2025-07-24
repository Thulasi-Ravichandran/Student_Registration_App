# 🧾 Student Registration System (Java Swing + MySQL)

This project is a **Java Swing GUI Application** connected to a **MySQL database**, built to manage student registrations with input validations, classification (Regular vs Lateral), and full CRUD operations.

---

## 🚀 Features

- 🧑 Add New Student
- 🔍 View Students by Type:
  - All Students
  - Regular Students (`regular_stu` table)
  - Lateral Students (`lateral_stu` table)
- 📝 Update Student Details
- ❌ Delete Student (select row and delete)
- 🔐 Input Validations:
  - No empty fields
  - Name must not contain digits
  - Phone number must be 10–12 digits only
  - Email must contain `@` and `.com`
  - Register number must be 10 digits
- 🧠 Auto-classification:
  - 7th and 8th digits `30` or `35` → **Regular**
  - 7th and 8th digits `33` or `37` → **Lateral**

---

## 🖼️ UI Snapshot

> Beautiful Java Swing interface with JTable, dropdown filters, and validation pop-ups.

---

## 🏗️ Technologies Used

- ☕ Java (Swing)
- 🐬 MySQL
- 🧩 JDBC (MySQL Connector/J 9.3.0)
- 💻 IntelliJ IDEA

---

## ⚙️ How to Run

### 1️⃣ Prerequisites

- Java JDK 17+
- MySQL installed
- IntelliJ IDEA
- MySQL Connector/J 

### 2️⃣ MySQL Setup

Create a database and tables:

```sql
CREATE DATABASE student_db;

USE student_db;

CREATE TABLE student (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  reg_no VARCHAR(20),
  phone VARCHAR(15),
  email VARCHAR(100),
  course VARCHAR(100)
);

CREATE TABLE regular_stu LIKE student;
CREATE TABLE lateral_stu LIKE student;
