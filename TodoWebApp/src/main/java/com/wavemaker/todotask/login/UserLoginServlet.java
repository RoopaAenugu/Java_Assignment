package com.wavemaker.todotask.login;

import com.google.gson.Gson;
import com.wavemaker.todotask.pojo.UserAuthentication;
import com.wavemaker.todotask.util.DbConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/login")
public class UserLoginServlet extends HttpServlet {
    private static Gson gson;

    @Override
    public void init() {
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");


        // Parse the JSON request body to a UserAuthentication object
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null) {
            writeResponse(resp, gson.toJson("Missing required parameters."));
            return;
        }
        UserAuthentication userAuthentication=new UserAuthentication();
        userAuthentication.setUsername(username);
        userAuthentication.setPassword(password);

        // Create an instance of the service class
        boolean userAdded = getUserByUsername(userAuthentication);

        if (userAdded) {
            String cookieValue = UUID.randomUUID().toString();
            String cookieName = "auth_cookie";
            Cookie cookie = new Cookie(cookieName, cookieValue);
            HttpSession adminSession = req.getSession(true);
            adminSession.setAttribute(cookieValue, userAuthentication);
            resp.addCookie(cookie);
            resp.sendRedirect("index.html");
        } else {
            writeResponse(resp, "User login authentication is not added.");
        }
    }

    private void writeResponse(HttpServletResponse resp, String message) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");
        out.println(message);
    }
    private static boolean getUserByUsername(UserAuthentication userAuthentication){
        String query = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userAuthentication.getUsername());
            preparedStatement.setString(2, userAuthentication.getPassword());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {

        }
        return false;

    }
}


