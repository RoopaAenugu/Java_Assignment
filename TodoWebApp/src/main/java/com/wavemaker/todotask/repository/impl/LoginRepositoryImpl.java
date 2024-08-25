package com.wavemaker.todotask.repository.impl;

import com.wavemaker.todotask.model.UserAuthentication;
import com.wavemaker.todotask.repository.LoginRepository;
import com.wavemaker.todotask.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepositoryImpl implements LoginRepository {

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

        }
        return -1;

    }
}
