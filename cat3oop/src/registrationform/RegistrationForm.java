// Java Swing Registration Form - CAT 3

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationForm extends JFrame implements ActionListener {

    // Form components
    JLabel nameLabel, mobileLabel, genderLabel, dobLabel, addressLabel;
    JTextField nameField, mobileField;
    JRadioButton maleButton, femaleButton;
    ButtonGroup genderGroup;
    JComboBox<String> dayBox, monthBox, yearBox;
    JTextArea addressArea;
    JCheckBox termsCheck;
    JButton submitButton, resetButton;
    JTextArea outputArea;

    Connection conn;

    RegistrationForm() {
        setTitle("Registration Form");
        setSize(800, 400);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        nameLabel = new JLabel("Name");
        nameField = new JTextField();

        mobileLabel = new JLabel("Mobile");
        mobileField = new JTextField();

        genderLabel = new JLabel("Gender");
        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        dobLabel = new JLabel("DOB");
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) days[i - 1] = String.valueOf(i);
        dayBox = new JComboBox<>(days);

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        monthBox = new JComboBox<>(months);

        String[] years = new String[100];
        for (int i = 1925; i <= 2024; i++) years[i - 1925] = String.valueOf(i);
        yearBox = new JComboBox<>(years);

        addressLabel = new JLabel("Address");
        addressArea = new JTextArea();

        termsCheck = new JCheckBox("Accept Terms And Conditions.");
        submitButton = new JButton("Submit");
        resetButton = new JButton("Reset");

        outputArea = new JTextArea();
        outputArea.setEditable(false);

        // Set bounds (LHS)
        int x = 20, y = 30, width = 120, height = 25;
        nameLabel.setBounds(x, y, width, height);
        nameField.setBounds(x + 100, y, 150, height);

        y += 40;
        mobileLabel.setBounds(x, y, width, height);
        mobileField.setBounds(x + 100, y, 150, height);

        y += 40;
        genderLabel.setBounds(x, y, width, height);
        maleButton.setBounds(x + 100, y, 70, height);
        femaleButton.setBounds(x + 180, y, 80, height);

        y += 40;
        dobLabel.setBounds(x, y, width, height);
        dayBox.setBounds(x + 100, y, 50, height);
        monthBox.setBounds(x + 160, y, 60, height);
        yearBox.setBounds(x + 230, y, 70, height);

        y += 40;
        addressLabel.setBounds(x, y, width, height);
        addressArea.setBounds(x + 100, y, 200, 60);

        y += 70;
        termsCheck.setBounds(x, y, 250, height);

        y += 40;
        submitButton.setBounds(x + 20, y, 80, height);
        resetButton.setBounds(x + 120, y, 80, height);

        outputArea.setBounds(400, 30, 350, 250);

        add(nameLabel); add(nameField);
        add(mobileLabel); add(mobileField);
        add(genderLabel); add(maleButton); add(femaleButton);
        add(dobLabel); add(dayBox); add(monthBox); add(yearBox);
        add(addressLabel); add(addressArea);
        add(termsCheck); add(submitButton); add(resetButton);
        add(outputArea);

        submitButton.addActionListener(this);
        resetButton.addActionListener(this);

        connectDB();
        displayData();

        setVisible(true);
    }

    public void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + e.getMessage());
        }
    }

    public void displayData() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM registration");
            outputArea.setText("");
            while (rs.next()) {
                outputArea.append(rs.getString("name") + ", " + rs.getString("mobile") + ", " + rs.getString("gender") + ", " + rs.getString("dob") + ", " + rs.getString("address") + "\n");
            }
        } catch (SQLException e) {
            outputArea.setText("Error fetching data: " + e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            if (!termsCheck.isSelected()) {
                JOptionPane.showMessageDialog(this, "You must accept the terms and conditions.");
                return;
            }

            String name = nameField.getText();
            String mobile = mobileField.getText();
            String gender = maleButton.isSelected() ? "Male" : (femaleButton.isSelected() ? "Female" : "");
            String dob = yearBox.getSelectedItem() + "-" + (monthBox.getSelectedIndex() + 1) + "-" + dayBox.getSelectedItem();
            String address = addressArea.getText();

            try {
                String query = "INSERT INTO registration(name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, mobile);
                pst.setString(3, gender);
                pst.setString(4, dob);
                pst.setString(5, address);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Data Submitted Successfully");
                displayData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }

        } else if (e.getSource() == resetButton) {
            nameField.setText("");
            mobileField.setText("");
            genderGroup.clearSelection();
            addressArea.setText("");
            termsCheck.setSelected(false);
        }
    }

    public static void main(String[] args) {
        new RegistrationForm();
    }
}
