package com.wavemaker.todotask.repository.impl;

import com.wavemaker.todotask.model.UserAuthentication;
import com.wavemaker.todotask.repository.LoginRepository;
import com.wavemaker.todotask.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginRepositoryImpl implements LoginRepository {

    private static final Logger logger = Logger.getLogger(LoginRepositoryImpl.class.getName());

    @Override
    public int isValidate(UserAuthentication userAuthentication) {
        String query = "SELECT * FROM USERS WHERE USERNAME=? AND PASSWORD=?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userAuthentication.getUsername());
            preparedStatement.setString(2, userAuthentication.getPassword());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("USER_ID");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error validating user", e);
        }

        return -1;  // Return -1 if validation fails
    }

    @Override
    public UserAuthentication addUser(UserAuthentication userAuthentication) {
        String query = "INSERT INTO USERS(USERNAME,PASSWORD) VALUES(?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userAuthentication.getUsername());
            preparedStatement.setString(2, userAuthentication.getPassword());
            preparedStatement.executeUpdate();

            return userAuthentication;  // Return the added user object

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding user", e);

        }
        return null;  // Return null if user addition fails
    }
}
