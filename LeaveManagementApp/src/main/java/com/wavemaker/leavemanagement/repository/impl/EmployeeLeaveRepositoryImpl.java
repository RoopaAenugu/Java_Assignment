package com.wavemaker.leavemanagement.repository.impl;

import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
import com.wavemaker.leavemanagement.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeLeaveRepositoryImpl implements EmployeeLeaveRepository {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveRepositoryImpl.class);
    private static final String GET_LEAVES_BY_EMPLOYEE_ID_QUERY =
            "SELECT  DISTINCT lr.LEAVE_ID, lr.EMPLOYEE_ID, lr.LEAVE_TYPE, lr.FROM_DATE, lr.TO_DATE, " +
                    "lr.REASON, lr.STATUS, lr.COMMENTS, e.NAME AS EMPLOYEE_NAME, lt.LIMIT_FOR_LEAVES " +
                    "FROM LEAVE_REQUESTS lr " +
                    "JOIN EMPLOYEES e ON lr.EMPLOYEE_ID = e.EMPLOYEE_ID " +
                    "JOIN LEAVE_TYPES lt ON lr.LEAVE_TYPE = lt.TYPE_NAME " +
                    "WHERE lr.EMPLOYEE_ID = ?";
    private static final String UPDATE_LEAVE_STATUS_TO_APPROVED_QUERY = "UPDATE LEAVE_REQUESTS SET STATUS = 'APPROVED' " +
            "WHERE LEAVE_ID = ?";
    private static final String UPDATE_LEAVE_STATUS_TO_REJECTED_QUERY = "UPDATE LEAVE_REQUESTS SET STATUS = 'REJECTED' " +
            "WHERE LEAVE_ID = ?";
    private static final String GET_LEAVE_REQUEST_QUERY = "SELECT * FROM LEAVE_REQUESTS WHERE LEAVE_ID = ?";
    private static final String INSERT_LEAVE_REQUEST_QUERY = "INSERT INTO LEAVE_REQUESTS (EMPLOYEE_ID, LEAVE_TYPE," +
            " FROM_DATE, TO_DATE, REASON, STATUS, MANAGER_ID, COMMENTS) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_NUMBER_OF_LEAVES_ALLOCATED =
            "SELECT LIMIT_FOR_LEAVES " +
                    "FROM LEAVE_TYPES " +
                    "WHERE TYPE_NAME = ?";
    private static final String COUNT_APPROVED_LEAVES_QUERY =
            "SELECT SUM(DATEDIFF(TO_DATE, FROM_DATE) + 1) AS total_leaves " +
                    "FROM LEAVE_REQUESTS " +
                    "WHERE EMPLOYEE_ID = ? AND STATUS = 'APPROVED' " +
                    "AND FROM_DATE >= '2024-04-01' " +
                    "AND TO_DATE <= '2025-03-31'";


    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LEAVE_REQUEST_QUERY)) {

            preparedStatement.setInt(1, leaveRequest.getEmployeeId());
            preparedStatement.setString(2, leaveRequest.getLeaveType());
            preparedStatement.setDate(3, java.sql.Date.valueOf(leaveRequest.getFromDate()));
            preparedStatement.setDate(4, java.sql.Date.valueOf(leaveRequest.getToDate()));
            preparedStatement.setString(5, leaveRequest.getReason());
            preparedStatement.setString(6, leaveRequest.getStatus());
            preparedStatement.setInt(7, leaveRequest.getManagerId());
            preparedStatement.setString(8, leaveRequest.getComments());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Leave request submitted successfully.");
                return leaveRequest; // Return the leave request if insertion is successful
            }
        } catch (SQLException e) {
            logger.error("Error applying leave request", e);
        }

        return null; // Return null if insertion fails
    }

    @Override
    public List<EmployeeLeave> getAllAppliedLeaves(int employeeId) {
        List<EmployeeLeave> leaveRequests = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_LEAVES_BY_EMPLOYEE_ID_QUERY)) {

            preparedStatement.setInt(1, employeeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    EmployeeLeave employeeLeave = new EmployeeLeave();
                    employeeLeave.setLeaveId(resultSet.getInt("LEAVE_ID"));
                    employeeLeave.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
                    employeeLeave.setLeaveType(resultSet.getString("LEAVE_TYPE"));
                    employeeLeave.setFromDate(resultSet.getDate("FROM_DATE").toLocalDate());
                    employeeLeave.setToDate(resultSet.getDate("TO_DATE").toLocalDate());
                    employeeLeave.setReason(resultSet.getString("REASON"));
                    employeeLeave.setStatus(resultSet.getString("STATUS"));
                    employeeLeave.setComments(resultSet.getString("COMMENTS"));
                    employeeLeave.setEmpName(resultSet.getString("EMPLOYEE_NAME"));
                    employeeLeave.setTypeLimit(resultSet.getInt("LIMIT_FOR_LEAVES"));
                    leaveRequests.add(employeeLeave);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., logging)
        }
        return leaveRequests;
    }

    public LeaveRequest acceptLeaveRequest(int leaveId) {
        // SQL query to update the leave status
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_LEAVE_STATUS_TO_APPROVED_QUERY);
             PreparedStatement selectStatement = connection.prepareStatement(GET_LEAVE_REQUEST_QUERY)) {
            // Update the leave request status
            updateStatement.setInt(1, leaveId);
            int rowsAffected = updateStatement.executeUpdate();
            // Check if the update was successful
            if (rowsAffected > 0) {
                // Retrieve the updated leave request
                selectStatement.setInt(1, leaveId);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapResultSetToLeaveRequest(resultSet);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions (e.g., logging)
        }

        return null; // Return null if the leave request was not found or update failed
    }

    @Override
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds) {
        List<EmployeeLeave> employeeLeaves = new ArrayList<>();
        if (employeeIds == null || employeeIds.isEmpty()) {
            return employeeLeaves; // Return an empty list if no employee IDs are provided
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT e.EMPLOYEE_ID, e.NAME AS EMPLOYEE_NAME, lr.LEAVE_ID, lr.LEAVE_TYPE, ")
                .append("lr.FROM_DATE, lr.TO_DATE, lr.REASON, lr.STATUS, lr.COMMENTS ")
                .append("FROM EMPLOYEES e ")
                .append("JOIN LEAVE_REQUESTS lr ON e.EMPLOYEE_ID = lr.EMPLOYEE_ID ")
                .append("WHERE e.EMPLOYEE_ID IN (");
        for (int i = 0; i < employeeIds.size(); i++) {
            queryBuilder.append("?");
            if (i < employeeIds.size() - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(")");

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < employeeIds.size(); i++) {
                preparedStatement.setInt(i + 1, employeeIds.get(i));
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    EmployeeLeave employeeLeave = new EmployeeLeave();
                    employeeLeave.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
                    employeeLeave.setEmpName(resultSet.getString("EMPLOYEE_NAME")); // Correct column name
                    employeeLeave.setLeaveId(resultSet.getInt("LEAVE_ID"));
                    employeeLeave.setLeaveType(resultSet.getString("LEAVE_TYPE"));
                    employeeLeave.setFromDate(resultSet.getDate("FROM_DATE").toLocalDate());
                    employeeLeave.setToDate(resultSet.getDate("TO_DATE").toLocalDate());
                    employeeLeave.setReason(resultSet.getString("REASON"));
                    employeeLeave.setStatus(resultSet.getString("STATUS"));
                    employeeLeave.setComments(resultSet.getString("COMMENTS"));

                    employeeLeaves.add(employeeLeave);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching leave details for employees", e);
        }


        return employeeLeaves;
    }

    @Override
    public int getNumberOfLeavesAllocated(EmployeeLeave employeeLeave) {
        int leaveLimit = 0;
        String leaveType = employeeLeave.getLeaveType().trim(); // Ensure trimming

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_NUMBER_OF_LEAVES_ALLOCATED)) {

            preparedStatement.setString(1, leaveType);
            logger.info("Executing query to get leave limit for type: {}", leaveType);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    leaveLimit = resultSet.getInt("LIMIT_FOR_LEAVES");
                    logger.info("Leave limit for type '{}' is: {} ", leaveType, leaveLimit);
                } else {
                    logger.warn("No leave type found for: {}", leaveType);
                }
            } catch (Exception e) {
                logger.error("Error while exeucuting Limit Leaves Query", e);
                throw e;
            }
        } catch (Exception e) {
            logger.error("SQL Error in getNumberOfLeavesAllocated", e);
        }
        logger.info("Final Leave limit for type '{}' is: {} ", leaveType, leaveLimit);
        return leaveLimit;
    }

    @Override
    public int getTotalNumberOfLeavesTaken(int employeeId) {
        int totalLeaves = 0;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_APPROVED_LEAVES_QUERY)) {
            preparedStatement.setInt(1, employeeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    totalLeaves = resultSet.getInt("total_leaves");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., logging)
        }

        return totalLeaves;
    }

    @Override
    public LeaveRequest rejectLeaveRequest(int leaveId) {

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_LEAVE_STATUS_TO_REJECTED_QUERY);
             PreparedStatement selectStatement = connection.prepareStatement(GET_LEAVE_REQUEST_QUERY)) {
            // Update the leave request status
            updateStatement.setInt(1, leaveId);
            int rowsAffected = updateStatement.executeUpdate();
            // Check if the update was successful
            if (rowsAffected > 0) {
                // Retrieve the updated leave request
                selectStatement.setInt(1, leaveId);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapResultSetToLeaveRequest(resultSet);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions (e.g., logging)
        }

        return null;

    }

    private LeaveRequest mapResultSetToLeaveRequest(ResultSet resultSet) throws SQLException {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setLeaveId(resultSet.getInt("LEAVE_ID"));
        leaveRequest.setEmployeeId(resultSet.getInt("EMPLOYEE_ID"));
        leaveRequest.setLeaveType(resultSet.getString("LEAVE_TYPE"));
        leaveRequest.setFromDate(resultSet.getDate("FROM_DATE").toLocalDate());
        leaveRequest.setToDate(resultSet.getDate("TO_DATE").toLocalDate());
        leaveRequest.setReason(resultSet.getString("REASON"));
        leaveRequest.setStatus(resultSet.getString("STATUS"));
        leaveRequest.setManagerId(resultSet.getInt("MANAGER_ID"));
        leaveRequest.setComments(resultSet.getString("COMMENTS"));
        return leaveRequest;

    }

}
