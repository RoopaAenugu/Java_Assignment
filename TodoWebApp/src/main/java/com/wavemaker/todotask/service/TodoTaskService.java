package com.wavemaker.todotask.service;

import com.wavemaker.todotask.model.TodoTask;

import java.util.List;
import java.util.Optional;

public interface TodoTaskService {

        public List<TodoTask> getAllTasks(int userId);
        public TodoTask getTaskById(int id);
        public TodoTask createTask(TodoTask task);
        public TodoTask updateTask(TodoTask task);
        public boolean deleteTask(int id);
        public TodoTask getTaskByTaskName(int userId,String taskName);
    }

