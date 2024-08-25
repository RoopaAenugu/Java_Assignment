package com.wavemaker.employee.controller;

import com.google.gson.Gson;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.service.AddressService;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.AddressServiceImpl;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
import com.wavemaker.employee.util.CookieUserHolder;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

@WebServlet("/address")
public class AddressController extends HttpServlet {
    private static EmployeeService employeeService;
    private static AddressService addressService;
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    private static Gson gson;

    public void init() throws ServletException {
        int storageOption = 3; // Example value, could be dynamically set
        employeeService = new EmployeeServiceImpl(storageOption);
        addressService = new AddressServiceImpl(storageOption);
        gson = new Gson();
        logger.info("Created EmployeeService and AddressService instances.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String empId = req.getParameter("empId");

        if (empId == null || empId.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(resp, "Missing or invalid empId.");
            return;
        }

        try {
            int id = Integer.parseInt(empId);
            Address address = addressService.getAddressByEmpId(id);

            if (address == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeResponse(resp, "Address not found for empId.");
            } else {
                Gson gson = new Gson();
                String jsonAddress = gson.toJson(address);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");

                PrintWriter printWriter = resp.getWriter();
                printWriter.print(jsonAddress);
                printWriter.flush();
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(resp, "Invalid empId format.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
                Address address = addressService.getAddressByEmpId(empId);
                if (address == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writeResponse(resp, "Address not found.");
                    return;
                }
                Address deleted = addressService.deleteAddressByEmpId(empId);
                if (deleted != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    writeResponse(resp, "Address deleted successfully.");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writeResponse(resp, "Failed to delete address.");
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(resp, "Invalid empId format.");
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writeResponse(resp, "An error occurred while deleting the address.");
                logger.error("Error deleting address with empId " + empIdStr, e);
            }
        } else {
            writeResponse(resp, gson.toJson("Invalid user"));
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession adminSession = request.getSession(true);
        String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", request);
        if (adminSession.getAttribute(cookieValue) != null) {
            Address address = gson.fromJson(request.getReader(), Address.class);

            if (address.getEmpId() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeResponse(response, gson.toJson("Missing or invalid empId."));
                return;
            }

            try {
                Employee employee = employeeService.getEmployeeById(address.getEmpId());
                if (employee != null) {
                    if (employee.getAddress() == null) {
                        boolean added = addressService.addAddress(address);
                        if (added) {
                            writeResponse(response, gson.toJson("Address added successfully."));
                        } else {
                            writeResponse(response, gson.toJson("Failed to add address."));
                        }
                    } else {
                        writeResponse(response, gson.toJson("Employee address already exists for empId " + address.getEmpId()));
                    }
                } else {
                    writeResponse(response, gson.toJson("Employee does not exist for empId " + address.getEmpId()));
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writeResponse(response, gson.toJson("An error occurred while adding the address."));
                logger.error("Error adding address with empId " + address.getEmpId(), e);
            }
        } else {
            writeResponse(response, gson.toJson("Invalid user"));
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession adminSession = request.getSession(true);
        String cookieValue = CookieUserHolder.getCookieValue("auth_cookie", request);
        if (adminSession.getAttribute(cookieValue) != null) {
            Address address = gson.fromJson(request.getReader(), Address.class);
            if (address.getEmpId() == 0) {
                writeResponse(response, gson.toJson("Missing employee ID."));
                return;
            }
            Address existingAddress = addressService.getAddressByEmpId(address.getEmpId());
            if (existingAddress != null) {
                address.setAddressId(existingAddress.getAddressId());
                Address updated = addressService.updateAddress(address);
                if (updated != null) {
                    writeResponse(response, gson.toJson("Address updated successfully."));
                } else {
                    writeResponse(response, gson.toJson("Failed to update address."));
                }
            } else {
                writeResponse(response, gson.toJson("Address not found."));
            }
        } else {
            writeResponse(response, gson.toJson("Invalid user"));

        }
    }

    private static void writeResponse(HttpServletResponse resp, String message) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.println(message);
    }
}
