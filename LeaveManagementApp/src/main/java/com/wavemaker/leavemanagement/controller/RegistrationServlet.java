package com.wavemaker.leavemanagement.controller;


import com.google.gson.Gson;
import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.LoginCredential;
import com.wavemaker.leavemanagement.service.EmployeeService;
import com.wavemaker.leavemanagement.service.LoginCredentialService;
import com.wavemaker.leavemanagement.service.impl.EmployeeServiceImpl;
import com.wavemaker.leavemanagement.service.impl.LoginCredentialServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebServlet("/signup")

public class RegistrationServlet extends HttpServlet {
    private static Gson gson;
    private static LoginCredentialService loginCredentialService;
    private static EmployeeService employeeService;

    @Override
    public void init() {
        gson = new Gson();
        loginCredentialService = new LoginCredentialServiceImpl();
        employeeService = new EmployeeServiceImpl();


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String emailId = request.getParameter("emailId");
        String password = request.getParameter("password");
        String empName = request.getParameter("empName");
        String phoneNumber = request.getParameter("phoneNumber");
        String dateOfBirthString = request.getParameter("DateOfBirth");
        String managerId = request.getParameter("managerId");

        Employee employee = new Employee();
        try {
            // Validate and set dateOfBirth
            if (dateOfBirthString != null && !dateOfBirthString.trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateOfBirth = LocalDate.parse(dateOfBirthString, formatter);
                employee.setDateOfBirth(dateOfBirth);
            } else {
                throw new IllegalArgumentException("Date of birth cannot be null or empty");
            }

            // Validate and set other parameters
            employee.setEmpName(empName);
            employee.setEmail(emailId);
            employee.setPhoneNumber(Long.parseLong(phoneNumber));

            LoginCredential loginCredential = new LoginCredential();
            loginCredential.setEmailId(emailId);
            loginCredential.setPassword(password);

            // Check managerId and add employee

            boolean managerPresent = employeeService.checkManager(emailId);

            if (managerPresent) {
                employee.setManagerId(Integer.parseInt(managerId));
                Employee addEmployee = employeeService.addEmployee(employee);
                LoginCredential addEmployeeLogin = loginCredentialService.addEmployeeLogin(loginCredential);
                if (addEmployeeLogin == null || addEmployee == null) {
                    writeResponse(response, gson.toJson("An error occurred while processing your request."));
                } else {
                    writeResponse(response, gson.toJson("Registration successful."));
                    response.sendRedirect("login.html");
                }
            } else {
                writeResponse(response, gson.toJson("Manager ID not found."));
            }

        } catch (NumberFormatException e) {
            writeResponse(response, gson.toJson("Invalid number format: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            writeResponse(response, gson.toJson("Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            writeResponse(response, gson.toJson("An error occurred while processing your request."));
        }
    }


    private void writeResponse(HttpServletResponse response, String message) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.println(message);
    }
}
