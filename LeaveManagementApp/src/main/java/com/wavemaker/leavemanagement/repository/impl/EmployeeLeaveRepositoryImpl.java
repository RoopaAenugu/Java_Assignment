package com.wavemaker.leavemanagement.repository.impl;

import com.wavemaker.leavemanagement.model.Employee;
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
            "SELECT * FROM LEAVE_REQUESTS WHERE EMPLOYEE_ID=?";
    private static final String GET_MY_TEAM_LEAVE_REQUESTS_QUERY =
            "SELECT lr.LEAVE_ID, lr.EMPLOYEE_ID, lr.LEAVE_TYPE, lr.REASON, lr.FROM_DATE, lr.TO_DATE, " +
                    "lr.STATUS, lr.COMMENTS " +
                    "FROM LEAVE_REQUESTS lr " +
                    "JOIN EMPLOYEES e ON lr.EMPLOYEE_ID = e.EMPLOYEE_ID " +
                    "WHERE e.MANAGER_ID = ?";
   private static final  String updateLeaveStatusQuery = "UPDATE LEAVE_REQUESTS SET STATUS = 'APPROVED' WHERE LEAVE_ID = ?";
    // SQL query to retrieve the updated leave request
   private static final String getLeaveRequestQuery = "SELECT * FROM LEAVE_REQUESTS WHERE LEAVE_ID = ?";


    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) {
        String query = "INSERT INTO LEAVE_REQUESTS (EMPLOYEE_ID, LEAVE_TYPE, FROM_DATE, TO_DATE, REASON, STATUS, MANAGER_ID, COMMENTS) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

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
    public List<LeaveRequest> getAllAppliedLeaves(int empId) {
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_LEAVES_BY_EMPLOYEE_ID_QUERY)) {

            preparedStatement.setInt(1, empId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    LeaveRequest leaveRequest = mapResultSetToLeaveRequest(resultSet);
                    leaveRequests.add(leaveRequest);
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
             PreparedStatement updateStatement = connection.prepareStatement(updateLeaveStatusQuery);
             PreparedStatement selectStatement = connection.prepareStatement(getLeaveRequestQuery)) {
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
    public List<LeaveRequest> getMyTeamRequests(int managerId) {
        List<LeaveRequest> leaves = new ArrayList<>();
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_MY_TEAM_LEAVE_REQUESTS_QUERY )) {

            preparedStatement.setInt(1, managerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
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

                    leaves.add(leaveRequest);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (e.g., logging)
        }

        return leaves;
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
    public LeaveRequest rejectLeaveRequest(int leaveId) {
        // SQL query to update the leave status
        String updateLeaveStatusQuery = "UPDATE LEAVE_REQUESTS SET STATUS = 'REJECTED' WHERE LEAVE_ID = ?";
        // SQL query to retrieve the updated leave request
        String getLeaveRequestQuery = "SELECT * FROM LEAVE_REQUESTS WHERE LEAVE_ID = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateLeaveStatusQuery);
             PreparedStatement selectStatement = connection.prepareStatement(getLeaveRequestQuery)) {
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
