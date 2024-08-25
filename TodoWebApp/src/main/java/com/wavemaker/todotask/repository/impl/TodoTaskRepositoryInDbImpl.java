package com.wavemaker.todotask.repository.impl;

import com.wavemaker.todotask.model.TodoTask;
import com.wavemaker.todotask.repository.TodoTaskRepository;
import com.wavemaker.todotask.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TodoTaskRepositoryInDbImpl implements TodoTaskRepository {
    private static final Logger logger = LoggerFactory.getLogger(TodoTaskRepositoryInDbImpl.class);
    // Define priority mapping
    private static final Map<String, Integer> PRIORITY_ORDER = Map.of(
            "High", 1,
            "Medium", 2,
            "Low", 3
    );

    @Override
    public List<TodoTask> getAllTasks(int userId) {
        List<TodoTask> tasks = new ArrayList<>();
        String query = "SELECT * FROM TODO_TASK WHERE USER_ID = ? ORDER BY CASE TASK_PRIORITY " +
                "WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 WHEN 'Low' THEN 3 END";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId); // Set the userId parameter

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(mapResultSetToTodoTask(resultSet));
                }
            }

        } catch (SQLException e) {
            logger.error("Error fetching all tasks for user ID " + userId, e);
        }

        return tasks;
    }

    @Override
    public TodoTask getTaskById(int id) {
        String query = "SELECT * FROM TODO_TASK WHERE TASK_ID = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToTodoTask(resultSet);
            }

        } catch (SQLException e) {
            logger.error("Error fetching task by ID", e);
        }
        return null;
    }

    @Override
    public TodoTask createTask(TodoTask todoTask) {
        String query = "INSERT INTO TODO_TASK (TASK_NAME, TASK_PRIORITY, TASK_DUE_DATE, TASK_DUE_TIME, COMPLETED, USER_ID) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, todoTask.getTaskName());
            preparedStatement.setString(2, todoTask.getTaskPriority());

            if (todoTask.getTaskDueDate() != null) {
                preparedStatement.setDate(3, Date.valueOf(todoTask.getTaskDueDate()));
            } else {
                preparedStatement.setNull(3, Types.DATE);
            }

            if (todoTask.getTaskDueTime() != null) {
                preparedStatement.setTime(4, Time.valueOf(todoTask.getTaskDueTime()));
            } else {
                preparedStatement.setNull(4, Types.TIME);
            }

            preparedStatement.setBoolean(5, todoTask.isCompleted()); // Set the completed parameter
            preparedStatement.setInt(6, todoTask.getUserId()); // Set the userId parameter

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        todoTask.setTaskId(generatedKeys.getInt(1)); // Set the auto-generated TASK_ID
                        return todoTask;
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error adding task", e);
        }
        return null;
    }

    @Override
    public TodoTask updateTask(TodoTask todoTask) {
        String query = "UPDATE TODO_TASK SET TASK_NAME = ?, TASK_PRIORITY = ?, TASK_DUE_DATE = ?, TASK_DUE_TIME = ?, COMPLETED = ? WHERE TASK_ID = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, todoTask.getTaskName());
            preparedStatement.setString(2, todoTask.getTaskPriority());

            if (todoTask.getTaskDueDate() != null) {
                preparedStatement.setDate(3, Date.valueOf(todoTask.getTaskDueDate()));
            } else {
                preparedStatement.setNull(3, Types.DATE);
            }

            if (todoTask.getTaskDueTime() != null) {
                preparedStatement.setTime(4, Time.valueOf(todoTask.getTaskDueTime()));
            } else {
                preparedStatement.setNull(4, Types.TIME);
            }

            preparedStatement.setBoolean(5, todoTask.isCompleted()); // Set the completed parameter
            preparedStatement.setInt(6, todoTask.getTaskId()); // Set the taskId parameter

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0 ? todoTask : null;

        } catch (SQLException e) {
            logger.error("Error updating task", e);
        }
        return null;
    }

    @Override
    public boolean deleteTask(int id) {
        String query = "DELETE FROM TODO_TASK WHERE TASK_ID = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            logger.error("Error deleting task", e);
        }
        return false;
    }

    @Override
    public TodoTask getTaskByTaskName(int userId, String taskName) {
        TodoTask todoTask = null;
        String query = "SELECT * FROM TODO_TASK WHERE USER_ID = ? AND TASK_NAME = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, taskName);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    todoTask = mapResultSetToTodoTask(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        return todoTask;
    }

    private TodoTask mapResultSetToTodoTask(ResultSet resultSet) throws SQLException {
        TodoTask task = new TodoTask();
        task.setTaskId(resultSet.getInt("TASK_ID"));
        task.setTaskName(resultSet.getString("TASK_NAME"));
        task.setTaskPriority(resultSet.getString("TASK_PRIORITY"));

        Date dueDate = resultSet.getDate("TASK_DUE_DATE");
        if (dueDate != null) {
            task.setTaskDueDate(dueDate.toLocalDate());
        }

        Time dueTime = resultSet.getTime("TASK_DUE_TIME");
        if (dueTime != null) {
            task.setTaskDueTime(dueTime.toLocalTime());
        }
        task.setCompleted(resultSet.getBoolean("COMPLETED")); // Map COMPLETED field
        task.setUserId(resultSet.getInt("USER_ID")); // Map USER_ID if necessary
        return task;
    }
}
