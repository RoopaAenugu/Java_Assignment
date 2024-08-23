package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.model.UserAuthentication;
import com.wavemaker.employee.repository.UserAuthenticationRepository;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
import com.wavemaker.employee.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthenticationRepositoryImpl implements UserAuthenticationRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationRepositoryImpl.class);

    @Override
    public boolean getUserByUsername(UserAuthentication userAuthentication) {
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
            logger.error("Error adding employee", e);
        }
        return false;
    }
}
