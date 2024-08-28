package com.wavemaker.leavemanagement.controller;

import com.google.gson.Gson;
import com.wavemaker.leavemanagement.model.LoginCredential;
import com.wavemaker.leavemanagement.service.EmployeeCookieService;
import com.wavemaker.leavemanagement.service.EmployeeService;
import com.wavemaker.leavemanagement.service.LoginCredentialService;
import com.wavemaker.leavemanagement.service.impl.EmployeeCookieServiceImpl;
import com.wavemaker.leavemanagement.service.impl.EmployeeServiceImpl;
import com.wavemaker.leavemanagement.service.impl.LoginCredentialServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.UUID;

@WebServlet("/manager-login")  // Ensure the servlet is properly mapped
public class ManagerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Gson gson;
    private LoginCredentialService loginCredentialService;
    private EmployeeCookieService employeeCookieService;
    private EmployeeService employeeService;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
        loginCredentialService = new LoginCredentialServiceImpl();
        employeeCookieService = new EmployeeCookieServiceImpl();
        employeeService = new EmployeeServiceImpl();

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            authenticate(request, response);
        } catch (Exception e) {
            e.printStackTrace();  // Log the exception
            writeResponse(response, gson.toJson("An error occurred while processing your request."));
        }
    }

    private void authenticate(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String emailId = request.getParameter("emailId");
        String password = request.getParameter("password");

        if (emailId == null || password == null) {
            writeResponse(response, gson.toJson("Missing required parameters."));
            return;
        }

        LoginCredential loginCredential = new LoginCredential();
        loginCredential.setEmailId(emailId);
        loginCredential.setPassword(password);

        try {
            int loginId = loginCredentialService.isValidate(loginCredential);
            if (loginId != -1) {
                if (employeeService.checkManager(emailId)) {
                    // Create and set the cookie
                    String cookieValue = UUID.randomUUID().toString();
                    Cookie cookie = new Cookie("auth_cookie", cookieValue);
                    cookie.setMaxAge(172800); // 2 days
                    response.addCookie(cookie);

                    // Create a session and store the loginId
                    HttpSession session = request.getSession(true);
                    session.setAttribute("loginId", loginId);

                    // Store the cookie value and loginId in the database
                    employeeCookieService.addCookie(cookieValue, loginId);

                    // Redirect to the main page
                    response.sendRedirect("managerpage.html");
                } else {
                    // Redirect with error message
                    response.sendRedirect("manager-login.html?error=" + URLEncoder.encode("Invalid username or password.", "UTF-8"));
                }
            }
        } catch (IOException e) {
            writeResponse(response, gson.toJson("An error occurred while processing your request."));
        }
    }

    private void writeResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println(message);
        }
    }
}
