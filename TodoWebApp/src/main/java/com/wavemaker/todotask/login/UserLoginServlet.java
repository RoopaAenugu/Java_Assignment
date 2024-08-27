package com.wavemaker.todotask.login;

import com.google.gson.Gson;
import com.wavemaker.todotask.model.UserAuthentication;
import com.wavemaker.todotask.service.LoginService;
import com.wavemaker.todotask.service.UserCookieTaskService;
import com.wavemaker.todotask.service.impl.LoginServiceImpl;
import com.wavemaker.todotask.service.impl.UserCookieTaskServiceImpl;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.UUID;

@WebServlet("/login")
public class UserLoginServlet extends HttpServlet {
    private static Gson gson;
    private static LoginService loginService;
    private static UserCookieTaskService userCookieTaskService;

    @Override
    public void init() {
        gson = new Gson();
        loginService = new LoginServiceImpl();
        userCookieTaskService = new UserCookieTaskServiceImpl();

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException{
        try {
            authenticate(request, response);
        } catch (ServletException | IOException e) {
            writeResponse(response, gson.toJson("An error occurred while processing your request."));
        }
    }

    private void authenticate(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            writeResponse(response, gson.toJson("Missing required parameters."));
            return;
        }

        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setUsername(username);
        userAuthentication.setPassword(password);

        try {
            int userId = loginService.isValidate(userAuthentication);
            if (userId != -1) {
                String cookieValue = UUID.randomUUID().toString();
                String cookieName = "auth_cookie";
                Cookie cookie = new Cookie(cookieName, cookieValue);
                HttpSession adminSession = request.getSession(true);
                adminSession.setAttribute(cookieValue, userId);
                response.addCookie(cookie);
                userAuthentication.setUserId(userId);
                userCookieTaskService.addCookie(cookieValue, userId);
                response.sendRedirect("index.html");
            } else {
                response.sendRedirect("login.html?error=" + URLEncoder.encode("Invalid username or password.", "UTF-8"));
            }
        } catch (IOException e) {
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
