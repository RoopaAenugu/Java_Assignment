package com.wavemaker.leavemanagement.repository.impl;

import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeRepository;
import com.wavemaker.leavemanagement.util.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private static final String FIND_EMPLOYEES_BY_MANAGER_QUERY =
            "SELECT * FROM EMPLOYEES WHERE MANAGER_ID = ?";


    @Override
    public Employee addEmployee(Employee employee) {
        String insertEmployeeSQL = "INSERT INTO EMPLOYEES (EMPLOYEE_ID, NAME, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, MANAGER_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL)) {

            preparedStatement.setInt(1, employee.getEmployeeId());
            preparedStatement.setString(2, employee.getEmpName());
            preparedStatement.setString(3, employee.getEmail());
            preparedStatement.setDate(4, java.sql.Date.valueOf(employee.getDateOfBirth()));  // Convert LocalDate to java.sql.Date
            preparedStatement.setBigDecimal(5, new BigDecimal(employee.getPhoneNumber()));
            preparedStatement.setInt(6, employee.getManagerId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee added successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., logging)
        }

        return employee;

    }

    @Override
    public boolean checkManager(String emailId) {
        String sqlQuery = "SELECT COUNT(*) FROM EMPLOYEES WHERE EMAIL = ? AND EMPLOYEE_ID IN (SELECT DISTINCT MANAGER_ID FROM EMPLOYEES WHERE MANAGER_ID IS NOT NULL)";


        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            preparedStatement.setString(1, emailId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    // If the count is greater than 0, the employee is a manager
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., logging)
        }

        return false;
    }

    @Override
    public Employee getEmployeeByLoginId(int loginId) {
        Employee employee = null;

        // SQL query to get the employee_id associated with the loginId
        String getEmployeeIdQuery = "SELECT EMPLOYEE_ID FROM LOGIN_CREDENTIALS WHERE LOGIN_ID = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement getEmployeeIdStatement = connection.prepareStatement(getEmployeeIdQuery)) {

            getEmployeeIdStatement.setInt(1, loginId);

            try (ResultSet employeeIdResultSet = getEmployeeIdStatement.executeQuery()) {
                if (employeeIdResultSet.next()) {
                    int employeeId = employeeIdResultSet.getInt("EMPLOYEE_ID");

                    // SQL query to get employee details using the employeeId
                    String getEmployeeQuery = "SELECT * FROM EMPLOYEES WHERE EMPLOYEE_ID = ?";

                    try (PreparedStatement getEmployeeStatement = connection.prepareStatement(getEmployeeQuery)) {
                        getEmployeeStatement.setInt(1, employeeId);

                        try (ResultSet employeeResultSet = getEmployeeStatement.executeQuery()) {
                            if (employeeResultSet.next()) {
                                employee = new Employee();
                                employee.setEmployeeId(employeeResultSet.getInt("EMPLOYEE_ID"));
                                employee.setEmpName(employeeResultSet.getString("NAME"));
                                employee.setEmail(employeeResultSet.getString("EMAIL"));
                                employee.setDateOfBirth(employeeResultSet.getDate("DATE_OF_BIRTH").toLocalDate());
                                employee.setPhoneNumber(employeeResultSet.getLong("PHONE_NUMBER"));
                                employee.setManagerId(employeeResultSet.getInt("MANAGER_ID"));
                                // Set other fields as necessary
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., logging)
        }

        return employee;
    }
    @Override
    public List<Integer> getEmpIdUnderManager(int managerId) {
        List<Integer> employeeIds = new ArrayList<>();
        String query = "SELECT EMPLOYEE_ID FROM EMPLOYEES WHERE MANAGER_ID = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, managerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    employeeIds.add(resultSet.getInt("EMPLOYEE_ID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., logging)
        }
        return employeeIds;
    }

}
