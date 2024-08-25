package com.wavemaker.todotask.repository;

import com.wavemaker.todotask.model.TodoTask;

import java.util.List;

public interface TodoTaskRepository {
    public List<TodoTask> getAllTasks(int userId);
    public TodoTask getTaskById(int id);
    public TodoTask createTask(TodoTask task);
    public TodoTask updateTask(TodoTask task);
    public boolean deleteTask(int id);
    public TodoTask getTaskByTaskName(int userId,String taskName);
}
