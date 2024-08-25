package com.wavemaker.todotask.service.impl;

import com.wavemaker.todotask.model.TodoTask;
import com.wavemaker.todotask.repository.TodoTaskRepository;
import com.wavemaker.todotask.repository.impl.TodoTaskRepositoryInDbImpl;
import com.wavemaker.todotask.service.TodoTaskService;

import java.util.List;

public class TodoTaskServiceImpl implements TodoTaskService {
    private final TodoTaskRepository todoTaskRepository;

    public TodoTaskServiceImpl() {
        // Initialize the repository instance
        this.todoTaskRepository = new TodoTaskRepositoryInDbImpl();
    }

    @Override
    public List<TodoTask> getAllTasks(int userId) {
        return todoTaskRepository.getAllTasks(userId);
    }

    @Override
    public TodoTask getTaskById(int  id) {
        return todoTaskRepository.getTaskById(id);
    }

    @Override
    public TodoTask createTask(TodoTask task) {
        return todoTaskRepository.createTask(task);
    }

    @Override
    public TodoTask updateTask(TodoTask task) {
        return todoTaskRepository.updateTask(task);
    }

    @Override
    public boolean deleteTask(int id) {
        return todoTaskRepository.deleteTask(id);
    }

    @Override
    public TodoTask getTaskByTaskName(int userId, String taskName) {
        return  todoTaskRepository.getTaskByTaskName(userId,taskName);
    }
}
