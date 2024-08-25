package com.wavemaker.todotask.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wavemaker.todotask.model.TodoTask;
import com.wavemaker.todotask.service.TodoTaskService;
import com.wavemaker.todotask.service.UserCookieTaskService;
import com.wavemaker.todotask.service.impl.TodoTaskServiceImpl;
import com.wavemaker.todotask.service.impl.UserCookieTaskServiceImpl;
import com.wavemaker.todotask.util.CookieUserHolder;
import com.wavemaker.todotask.util.LocalDateAdapter;
import com.wavemaker.todotask.util.LocalTimeAdapter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/api/todos/*")
public class TodoTaskServlet extends HttpServlet {

    private static TodoTaskService todoTaskService;
    private static UserCookieTaskService userCookieTaskService;
    private static Gson gson;

    @Override
    public void init() {
        todoTaskService = new TodoTaskServiceImpl();
        userCookieTaskService = new UserCookieTaskServiceImpl();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/insert".equals(pathInfo)) {
            insertTodo(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.matches("/\\d+")) {
            updateTodo(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.matches("/\\d+")) {
            deleteTodo(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/list".equals(pathInfo)) {
            listTodo(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/search/")) {
            searchTodoByTaskName(request, response);
        } else if (pathInfo != null && pathInfo.matches("/\\d+")) {
            getTodoById(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listTodo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", request);
        try {
            int userId = userCookieTaskService.getUserIdByCookieValue(cookieValue);
            if (userId != -1) {
                List<TodoTask> listTodoTask = todoTaskService.getAllTasks(userId);
                if (!listTodoTask.isEmpty()) {
                    String jsonResponse = gson.toJson(listTodoTask);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter printWriter = response.getWriter();
                    printWriter.print(jsonResponse);
                    printWriter.flush();
                } else {
                    writeResponse(response, "No tasks found.");
                }
            } else {
                writeResponse(response, "Invalid user");
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
        }
    }

    private void getTodoById(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getPathInfo().substring(1));
        try {
            TodoTask todo = todoTaskService.getTaskById(id);
            if (todo != null) {
                String jsonResponse = gson.toJson(todo);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Todo not found");
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
        }
    }

    private void insertTodo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", request);
            if (cookieValue != null) {
                int userId = userCookieTaskService.getUserIdByCookieValue(cookieValue);
                if (userId != -1) {
                    TodoTask todoTask = gson.fromJson(request.getReader(), TodoTask.class);
                    todoTask.setUserId(userId);
                    TodoTask addedTask = todoTaskService.createTask(todoTask);
                    String jsonResponse = gson.toJson(addedTask);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(jsonResponse);
                } else {
                    writeResponse(response, "User is not present.");
                }
            } else {
                writeResponse(response, "Authentication cookie is missing.");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while inserting the task: " + e.getMessage());
        }
    }

    private void updateTodo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getPathInfo().substring(1));
            String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", request);

            if (cookieValue != null) {
                int userId = userCookieTaskService.getUserIdByCookieValue(cookieValue);
                if (userId != -1) {
                    // Parse the incoming JSON request to a TodoTask object
                    TodoTask updatedTodoTask = gson.fromJson(request.getReader(), TodoTask.class);
                    updatedTodoTask.setUserId(userId);
                    updatedTodoTask.setTaskId(id);

                    // Update the task using the service
                    TodoTask todoTask = todoTaskService.updateTask(updatedTodoTask);

                    // Set response content type
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    // Send appropriate response based on the outcome
                    if (todoTask != null) {
                        // Return the updated task as JSON
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write(gson.toJson(todoTask));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\": \"Task not found\"}");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user authentication");
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }


    private void deleteTodo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getPathInfo().substring(1)); // Get the task ID from the path
            String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", request);
            if (cookieValue != null) {
                int userId = userCookieTaskService.getUserIdByCookieValue(cookieValue);
                if (userId != -1) {
                    boolean deletedTask = todoTaskService.deleteTask(id);
                    if (deletedTask) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        writeResponse(response, "task deleted successfully.");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writeResponse(response, "Task not found.");
                    }
                } else {
                    writeResponse(response, "Invalid user");
                }
            } else {
                writeResponse(response, "Authentication cookie is missing.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    private void searchTodoByTaskName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String taskName = request.getPathInfo().substring("/search/".length()).trim();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!taskName.isEmpty()) {
            String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", request);
            try {
                int userId = userCookieTaskService.getUserIdByCookieValue(cookieValue);
                if (userId != -1) {
                    TodoTask todoTask = todoTaskService.getTaskByTaskName(userId, taskName);
                    PrintWriter printWriter = response.getWriter();
                    if (todoTask != null) {
                        String jsonResponse = gson.toJson(todoTask);
                        printWriter.print(jsonResponse);
                    } else {
                        // Return a JSON error message
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        printWriter.print("{\"error\": \"Task not found.\"}");
                    }
                    printWriter.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    writeJsonError(response, "Invalid user");
                }
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writeJsonError(response, "An unexpected error occurred: " + ex.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJsonError(response, "Task name parameter is missing.");
        }
    }

    private void writeJsonError(HttpServletResponse response, String errorMessage) throws IOException {
        PrintWriter printWriter = response.getWriter();
        printWriter.print("{\"error\": \"" + errorMessage + "\"}");
        printWriter.flush();
    }


    private void writeResponse(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.println(message);
    }
}
