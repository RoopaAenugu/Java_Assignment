package com.wavemaker.employee.login;

import com.google.gson.Gson;
import com.wavemaker.employee.model.UserAuthentication;
import com.wavemaker.employee.service.UserAuthenticationService;
import com.wavemaker.employee.service.impl.UserAuthenticationServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet("/login")
public class UserLoginServlet extends HttpServlet {
    private static UserAuthenticationService userAuthService;
    private static Gson gson;

    @Override
    public void init() {
        userAuthService = new UserAuthenticationServiceImpl();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Parse the JSON request body to a UserAuthentication object

        UserAuthentication userAuthentication = gson.fromJson(req.getReader(), UserAuthentication.class);
        if (userAuthentication.getUsername() == null || userAuthentication.getPassword() == null) {
            writeResponse(resp, gson.toJson("Missing required parameters."));
            return;
        }

        // Create an instance of the service class
        userAuthService = new UserAuthenticationServiceImpl();
        boolean userAdded = userAuthService.getUserByUsername(userAuthentication);

        if (userAdded) {
            String cookieValue = UUID.randomUUID().toString();
            String cookieName = "auth_cookie";
            Cookie cookie = new Cookie(cookieName, cookieValue);
            HttpSession adminSession = req.getSession(true);
            adminSession.setAttribute(cookieValue, userAuthentication);
            resp.addCookie(cookie);
            writeResponse(resp, "User  login with correct authentication.");
        } else {
            writeResponse(resp, "User login authentication is not added.");
        }
    }

    private void writeResponse(HttpServletResponse resp, String message) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");
        out.println(message);
    }
}


