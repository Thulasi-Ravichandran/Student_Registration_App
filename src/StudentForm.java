// Full updated StudentForm.java with validations and features

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentForm extends JFrame {
    private JTextField nameField, regNoField, phoneField, emailField, courseField;
    private JButton addButton, viewButton, deleteButton, updateButton;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public StudentForm() {
        setTitle("Student Registration System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 5, 5));

        nameField = new JTextField();
        regNoField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        courseField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Register Number:"));
        inputPanel.add(regNoField);
        inputPanel.add(new JLabel("Phone Number:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Email ID:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Course/Degree:"));
        inputPanel.add(courseField);

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        inputPanel.add(addButton);
        inputPanel.add(updateButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table Panel
        tableModel = new DefaultTableModel(new String[]{"Name", "Reg No", "Phone", "Email", "Course"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Bottom Panel with filter and buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());

        filterCombo = new JComboBox<>(new String[]{"All Students", "Regular", "Lateral"});
        viewButton = new JButton("View");
        deleteButton = new JButton("Delete");

        bottomPanel.add(new JLabel("Filter: "));
        bottomPanel.add(filterCombo);
        bottomPanel.add(viewButton);
        bottomPanel.add(deleteButton);

        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        addButton.addActionListener(e -> addStudent());
        viewButton.addActionListener(e -> viewStudents());
        deleteButton.addActionListener(e -> deleteStudent());
        updateButton.addActionListener(e -> updateStudent());
    }

    private boolean isValidInput() {
        String name = nameField.getText().trim();
        String regNo = regNoField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String course = courseField.getText().trim();

        if (name.isEmpty() || regNo.isEmpty() || phone.isEmpty() || email.isEmpty() || course.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return false;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "Name should contain only letters and spaces.");
            return false;
        }

        if (!regNo.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Register Number must be exactly 10 digits.");
            return false;
        }

        if (!phone.matches("\\d{10,12}")) {
            JOptionPane.showMessageDialog(this, "Phone Number must be 10-12 digits and numeric only.");
            return false;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.com$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format. Must contain @ and .com");
            return false;
        }

        return true;
    }

    private void addStudent() {
        if (!isValidInput()) return;

        String name = nameField.getText();
        String regNo = regNoField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String course = courseField.getText();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst1 = conn.prepareStatement("INSERT INTO student (name, reg_no, phone, email, course) VALUES (?, ?, ?, ?, ?)");
            pst1.setString(1, name);
            pst1.setString(2, regNo);
            pst1.setString(3, phone);
            pst1.setString(4, email);
            pst1.setString(5, course);
            pst1.executeUpdate();

            String type = getStudentType(regNo);
            String tableName = type.equals("Regular") ? "regular_stu" : "lateral_stu";

            PreparedStatement pst2 = conn.prepareStatement("INSERT INTO " + tableName + " (name, reg_no, phone, email, course) VALUES (?, ?, ?, ?, ?)");
            pst2.setString(1, name);
            pst2.setString(2, regNo);
            pst2.setString(3, phone);
            pst2.setString(4, email);
            pst2.setString(5, course);
            pst2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student Added Successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void viewStudents() {
        tableModel.setRowCount(0);
        String type = filterCombo.getSelectedItem().toString();
        String query = "SELECT * FROM student";

        if (type.equals("Regular")) {
            query = "SELECT * FROM regular_stu";
        } else if (type.equals("Lateral")) {
            query = "SELECT * FROM lateral_stu";
        }

        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getString("name"), rs.getString("reg_no"), rs.getString("phone"), rs.getString("email"), rs.getString("course")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }

        String regNo = tableModel.getValueAt(selectedRow, 1).toString();
        String type = getStudentType(regNo);
        String tableName = type.equals("Regular") ? "regular_stu" : "lateral_stu";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst1 = conn.prepareStatement("DELETE FROM student WHERE reg_no = ?");
            pst1.setString(1, regNo);
            pst1.executeUpdate();

            PreparedStatement pst2 = conn.prepareStatement("DELETE FROM " + tableName + " WHERE reg_no = ?");
            pst2.setString(1, regNo);
            pst2.executeUpdate();

            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Student Deleted Successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateStudent() {
        if (!isValidInput()) return;

        String regNo = regNoField.getText();
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String course = courseField.getText();

        String type = getStudentType(regNo);
        String tableName = type.equals("Regular") ? "regular_stu" : "lateral_stu";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst1 = conn.prepareStatement("UPDATE student SET name=?, phone=?, email=?, course=? WHERE reg_no=?");
            pst1.setString(1, name);
            pst1.setString(2, phone);
            pst1.setString(3, email);
            pst1.setString(4, course);
            pst1.setString(5, regNo);
            pst1.executeUpdate();

            PreparedStatement pst2 = conn.prepareStatement("UPDATE " + tableName + " SET name=?, phone=?, email=?, course=? WHERE reg_no=?");
            pst2.setString(1, name);
            pst2.setString(2, phone);
            pst2.setString(3, email);
            pst2.setString(4, course);
            pst2.setString(5, regNo);
            pst2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student Updated Successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private String getStudentType(String regNo) {
        if (regNo.length() >= 8) {
            String digits = regNo.substring(6, 8);
            if (digits.equals("30") || digits.equals("35")) return "Regular";
            else if (digits.equals("33") || digits.equals("37")) return "Lateral";
        }
        return "Unknown";
    }
}
