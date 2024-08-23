package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.service.AddressService;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.AddressServiceImpl;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
import com.wavemaker.employee.util.CookieUserHolder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/employee")
public class EmployeeServlet extends HttpServlet {

    private static EmployeeService employeeService;
    private static AddressService addressService;
    private static Gson gson;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServlet.class);

    @Override
    public void init() {
        int storageOption = 3; // here storageOption 3 is InDbstorage
        employeeService = new EmployeeServiceImpl(storageOption);
        addressService = new AddressServiceImpl(storageOption);
        gson = new Gson();
        logger.info("Created EmployeeService and AddressService instances.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String actionPerformed = req.getParameter("action");

        if ("getEmployeeById".equals(actionPerformed)) {
            getEmployeeById(req, resp);
        } else if ("getAllEmployees".equals(actionPerformed)) {
            getAllEmployees(resp);
        } else if ("getEmployeeByEmail".equals(actionPerformed)) {
            getEmployeeByEmail(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(resp, "Invalid action parameter.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession adminSession = req.getSession(true);
        String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", req);
        if (adminSession.getAttribute(cookieValue) != null) {
            String empIdStr = req.getParameter("empId");
            if (empIdStr == null || empIdStr.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(resp, "Missing or invalid empId.");
                return;
            }

            try {
                int empId = Integer.parseInt(empIdStr);

                Employee employee = employeeService.getEmployeeById(empId);
                if (employee == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writeResponse(resp, "Employee not found.");
                    return;
                }

                boolean deleted = employeeService.deleteEmployeeByEmpId(empId);
                if (deleted) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    writeResponse(resp, "Employee deleted successfully.");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writeResponse(resp, "Failed to delete employee.");
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(resp, "Invalid empId format.");
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writeResponse(resp, "An error occurred while deleting the employee.");
                logger.error("Error deleting employee with empId " + empIdStr, e);
            }
        } else {
            writeResponse(resp, gson.toJson("Invalid user"));

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession adminSession = req.getSession(true);
        String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", req);
        if (adminSession.getAttribute(cookieValue) != null) {

            // Set the content type to JSON
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            // Parse the JSON request body to an Employee object
            Employee employee = gson.fromJson(req.getReader(), Employee.class);

            if (employee != null) {
                int empId = employeeService.addEmployee(employee);
                if (empId != -1) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    writeResponse(resp, gson.toJson("Employee added successfully with ID: " + empId));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writeResponse(resp, gson.toJson("Failed to add employee."));
                }
            } else {
                // Send an error response if the employee object is null (e.g., invalid JSON format)
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(resp, gson.toJson("Invalid employee data."));
            }
        } else {
            writeResponse(resp, gson.toJson("Invalid user"));
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession adminSession = req.getSession(true);
        String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", req);
        if (adminSession.getAttribute(cookieValue) != null) {

            // Set the content type to JSON
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            String idStr = req.getParameter("empId");
            if (idStr == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(resp, "{\"error\":\"Missing employee ID.\"}");
                return;
            }

            int empId;
            try {
                empId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(resp, "{\"error\":\"Invalid employee ID format.\"}");
                return;
            }

            Employee existingEmployee = employeeService.getEmployeeById(empId);
            if (existingEmployee == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeResponse(resp, "{\"error\":\"Employee not found.\"}");
                return;
            }

            // Parse the JSON request body to an Employee object
            Employee updatedEmployee = gson.fromJson(req.getReader(), Employee.class);

            if (updatedEmployee == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(resp, "{\"error\":\"Invalid employee data.\"}");
                return;
            }

            updatedEmployee.setEmpId(empId);  // Ensure the ID is set correctly

            // Update address if provided in the request
            if (updatedEmployee.getAddress() != null) {
                Address existingAddress = addressService.getAddressByEmpId(empId);
                if (existingAddress != null) {
                    Address updatedAddress = updatedEmployee.getAddress();
                    updatedAddress.setEmpId(empId);
                    updatedAddress.setAddressId(existingAddress.getAddressId());
                    addressService.updateAddress(updatedAddress);
                } else {
                    updatedEmployee.setAddress(null);
                }
            }

            // Update the employee
            boolean updated = employeeService.updateEmployee(updatedEmployee);
            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                writeResponse(resp, gson.toJson("Employee updated successfully."));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writeResponse(resp, gson.toJson("Failed to update employee."));
            }
        }
        else{
            writeResponse(resp, gson.toJson("Invalid user"));

        }
    }

    private void getEmployeeById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) {
            writeResponse(resp, "Missing employee ID.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Employee employee = employeeService.getEmployeeById(id);

            if (employee != null) {
                String jsonEmployee = sendEmployee(resp, employee);
                PrintWriter printWriter = resp.getWriter();
                printWriter.print(jsonEmployee);
                printWriter.flush();
            } else {
                writeResponse(resp, "Employee not found.");
            }
        } catch (NumberFormatException e) {
            writeResponse(resp, "Invalid employee ID format.");
        }
    }

    private void getAllEmployees(HttpServletResponse resp) throws IOException {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            writeResponse(resp, "No employees found.");
        } else {
            for (Employee emp : employees) {
                String jsonEmployee = sendEmployee(resp, emp);//sendEmployee returns jsonEmployee
                PrintWriter printWriter = resp.getWriter();
                printWriter.print(jsonEmployee);
                printWriter.flush();
            }
        }
    }


    private void getEmployeeByEmail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        if (email == null) {
            writeResponse(resp, "Missing email parameter.");
            return;
        }

        Employee employee = employeeService.getEmployeeByEmail(email);
        if (employee != null) {
            String jsonEmployee = sendEmployee(resp, employee);
            PrintWriter printWriter = resp.getWriter();
            printWriter.print(jsonEmployee);
            printWriter.flush();
        } else {
            writeResponse(resp, "Employee not found for email: " + email);
        }
    }

    private String sendEmployee(HttpServletResponse resp, Employee emp) {
        String jsonEmployee = gson.toJson(emp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        return jsonEmployee;

    }

    private void writeResponse(HttpServletResponse resp, String message) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");
        out.println(message);
    }
}
