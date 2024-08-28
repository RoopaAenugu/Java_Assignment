package com.wavemaker.leavemanagement.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.service.EmployeeCookieService;
import com.wavemaker.leavemanagement.service.EmployeeLeaveService;
import com.wavemaker.leavemanagement.service.EmployeeNumberOfLeavesService;
import com.wavemaker.leavemanagement.service.EmployeeService;
import com.wavemaker.leavemanagement.service.impl.EmployeeCookieServiceImpl;
import com.wavemaker.leavemanagement.service.impl.EmployeeLeaveServiceImpl;
import com.wavemaker.leavemanagement.service.impl.EmployeeNumberOfLeavesServiceImpl;
import com.wavemaker.leavemanagement.service.impl.EmployeeServiceImpl;
import com.wavemaker.leavemanagement.util.LocalDateAdapter;
import com.wavemaker.leavemanagement.util.LocalTimeAdapter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/employee/leave/*")
public class EmployeeLeaveServlet extends HttpServlet {
    private static Gson gson;
    private static EmployeeLeaveService employeeLeaveService;
    private static EmployeeCookieService employeeCookieService;
    private static EmployeeNumberOfLeavesService employeeNumberOfLeavesService;
    private static EmployeeService employeeService;

    @Override
    public void init() {
        employeeLeaveService = new EmployeeLeaveServiceImpl();
        employeeCookieService = new EmployeeCookieServiceImpl();
        employeeNumberOfLeavesService = new EmployeeNumberOfLeavesServiceImpl();
        employeeService = new EmployeeServiceImpl();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/getNoOfLeaves")) {
            getNoOfLeaves(request, response);
        } else if (pathInfo != null && pathInfo.equals("/getAllAppliedLeaves")) {
            getAllAppliedLeaves(request, response);
        } else if (pathInfo != null && pathInfo.equals("/getMyTeamRequests")) {
            getMyTeamRequests(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeResponse(response, "The requested resource [" + pathInfo + "] is not available.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/applyEmployeeLeave")) {
            applyEmployeeLeave(request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/acceptLeaveRequest")) {
            acceptLeaveRequest(request, response);
        } else if (pathInfo != null && pathInfo.equals("/rejectLeaveRequest")) {
            rejectLeaveRequest(request, response);
        }
    }

    private void applyEmployeeLeave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer loginId = (Integer) session.getAttribute("loginId");
                if (loginId != null) {
                    LeaveRequest leaveRequest = gson.fromJson(request.getReader(), LeaveRequest.class);
                    Employee employee = employeeService.getEmployeeByLoginId(loginId);
                    if (employee != null) {
                        leaveRequest.setEmployeeId(employee.getEmployeeId());
                        leaveRequest.setManagerId(employee.getManagerId());
                        leaveRequest.setStatus("pending");
                    } else {
                        writeResponse(response, "Employee not found.");
                        return;
                    }

                    LeaveRequest addLeaveRequest = employeeLeaveService.applyLeave(leaveRequest);
                    String jsonResponse = gson.toJson(addLeaveRequest);
                    writeResponse(response, jsonResponse);
                } else {
                    writeResponse(response, "User is not present.");
                }
            } else {
                writeResponse(response, "Session is not valid.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while inserting the leave request: " + e.getMessage());
        }
    }

    private void getNoOfLeaves(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String empId = request.getParameter("employeeId");
            if (empId != null) {
                empId = empId.trim();
                int employeeId = Integer.parseInt(empId);
                int noOfLeaves = employeeNumberOfLeavesService.getNoOfLeaves(employeeId);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(String.valueOf(noOfLeaves));
            } else {
                writeResponse(response, "Employee ID is missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllAppliedLeaves(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer loginId = (Integer) session.getAttribute("loginId");
                if (loginId != null) {
                    Employee employee = employeeService.getEmployeeByLoginId(loginId);
                    if (employee != null) {
                        int employeeId = employee.getEmployeeId();
                        List<LeaveRequest> leaveRequests = employeeLeaveService.getAllAppliedLeaves(employeeId);
                        String jsonResponse = gson.toJson(leaveRequests);
                        writeResponse(response, jsonResponse);
                    } else {
                        writeResponse(response, "Employee not found.");
                    }
                } else {
                    writeResponse(response, "User is not present.");
                }
            } else {
                writeResponse(response, "Session is not valid.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching leaves for the logged-in employee: " + e.getMessage());
        }
    }

    private void getMyTeamRequests(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer loginId = (Integer) session.getAttribute("loginId");
                if (loginId != null) {
                     Employee employee= employeeService.getEmployeeByLoginId(loginId);
                     int managerId = employee.getEmployeeId();
                        List<Integer> employeeIds= employeeService.getEmpIdUnderManager(managerId);
                        List<EmployeeLeave> employeeLeaves = employeeLeaveService.getLeavesOfEmployees(employeeIds);
                        String jsonResponse = gson.toJson(employeeLeaves);
                        writeResponse(response, jsonResponse);
                    } else {
                        writeResponse(response, "Manager ID is missing.");
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while retrieving team leave requests: " + e.getMessage());
        }

    }

    private void acceptLeaveRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer loginId = (Integer) session.getAttribute("loginId");
                if (loginId != null) {
                    String leaveIdStr = request.getParameter("leaveId");
                    if (leaveIdStr != null) {
                        leaveIdStr = leaveIdStr.trim();
                        int leaveId = Integer.parseInt(leaveIdStr);
                        LeaveRequest leaveRequest = employeeLeaveService.acceptLeaveRequest(leaveId);
                        if (leaveRequest != null) {
                            String jsonResponse = gson.toJson(leaveRequest);
                            writeResponse(response, jsonResponse);
                        } else {
                            writeResponse(response, "Leave request not found or already accepted.");
                        }
                    } else {
                        writeResponse(response, "Leave ID is missing.");
                    }
                } else {
                    writeResponse(response, "User is not present.");
                }
            } else {
                writeResponse(response, "Session is not valid.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while accepting the leave request: " + e.getMessage());
        }
    }

    private void rejectLeaveRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer loginId = (Integer) session.getAttribute("loginId");
                if (loginId != null) {
                    String leaveIdStr = request.getParameter("leaveId");
                    if (leaveIdStr != null) {
                        leaveIdStr = leaveIdStr.trim();
                        int leaveId = Integer.parseInt(leaveIdStr);
                        LeaveRequest leaveRequest = employeeLeaveService.rejectLeaveRequest(leaveId);
                        if (leaveRequest != null) {
                            String jsonResponse = gson.toJson(leaveRequest);
                            writeResponse(response, jsonResponse);
                        } else {
                            writeResponse(response, "Leave request not found.");
                        }
                    } else {
                        writeResponse(response, "Leave ID is missing.");
                    }
                } else {
                    writeResponse(response, "User is not present.");
                }
            } else {
                writeResponse(response, "Session is not valid.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while rejecting the leave request: " + e.getMessage());
        }
    }

    private void writeResponse(HttpServletResponse response, String jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
