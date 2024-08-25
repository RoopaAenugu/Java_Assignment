package com.wavemaker.todotask.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class TodoTask {
    private int taskId;
    private String taskName;
    private String taskPriority;
    private LocalDate taskDueDate;
    private LocalTime taskDueTime;
    private boolean completed; // New field for task completion status
    private int userId;

    // Constructors
    public TodoTask() {
    }

    public TodoTask(int taskId, String taskName, String taskPriority, LocalDate taskDueDate, LocalTime taskDueTime, boolean completed) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskPriority = taskPriority;
        this.taskDueDate = taskDueDate;
        this.taskDueTime = taskDueTime;
        this.completed = completed; // Initialize the new field
    }

    // Getters and Setters
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

    public LocalDate getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(LocalDate taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public LocalTime getTaskDueTime() {
        return taskDueTime;
    }

    public void setTaskDueTime(LocalTime taskDueTime) {
        this.taskDueTime = taskDueTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoTask todoTask = (TodoTask) o;
        return taskId == todoTask.taskId &&
                completed == todoTask.completed && // Include the completed field in equality check
                Objects.equals(taskName, todoTask.taskName) &&
                Objects.equals(taskPriority, todoTask.taskPriority) &&
                Objects.equals(taskDueDate, todoTask.taskDueDate) &&
                Objects.equals(taskDueTime, todoTask.taskDueTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, taskName, taskPriority, taskDueDate, taskDueTime, completed);
    }

    @Override
    public String toString() {
        return "TodoTask{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskPriority='" + taskPriority + '\'' +
                ", taskDueDate=" + taskDueDate +
                ", taskDueTime=" + taskDueTime +
                ", completed=" + completed + // Include completed status in string representation
                ", userId=" + userId +
                '}';
    }
}
