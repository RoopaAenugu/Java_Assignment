package com.wavemaker.todotask.register;


import com.google.gson.Gson;
import com.wavemaker.todotask.model.UserAuthentication;
import com.wavemaker.todotask.service.LoginService;
import com.wavemaker.todotask.service.impl.LoginServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/signup")

public class RegistrationServlet extends HttpServlet {
    private static Gson gson;
    private static LoginService loginService;

    @Override
    public void init() {
        gson = new Gson();
        loginService = new LoginServiceImpl();

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("username");
        String userPassword = request.getParameter("password");
        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setUsername(userName);
        userAuthentication.setPassword(userPassword);
        try {
            UserAuthentication addUser = loginService.addUser(userAuthentication);
            if (addUser == null) {
                writeResponse(response, gson.toJson("An error occurred while processing your request."));
            } else {
                writeResponse(response, gson.toJson("Registration successful."));
                response.sendRedirect("login.html");

            }
        } catch (Exception e) {
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
