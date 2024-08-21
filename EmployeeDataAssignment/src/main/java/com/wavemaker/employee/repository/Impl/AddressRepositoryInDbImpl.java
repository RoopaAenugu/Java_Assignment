package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.util.DbConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressRepositoryInDbImpl implements AddressRepository {
    private static final Logger logger = LoggerFactory.getLogger(AddressRepositoryInDbImpl.class);

    @Override
    public Address getAddressByEmpId(int empId) {
        String query = "SELECT * FROM ADDRESS WHERE EMP_ID = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, empId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToAddress(resultSet);
            }

        } catch (SQLException e) {
            logger.error("SQL Error Code: " + e.getErrorCode());
            logger.error("SQL State: " + e.getSQLState());
            logger.error("Error fetching address by employee ID", e);
        }
        return null;
    }

    @Override
    public boolean addAddress(Address address) {
        String query = "INSERT INTO ADDRESS (STATE, CITY, PINCODE, EMP_ID) VALUES (?, ?, ?, ?)";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, address.getState());
            preparedStatement.setString(2, address.getCity());
            preparedStatement.setInt(3, address.getPincode());
            preparedStatement.setInt(4, address.getEmpId());

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        address.setAddressId(generatedKeys.getInt(1)); // Set the generated ADDRESS_ID
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("SQL Error Code: " + e.getErrorCode());
            logger.error("SQL State: " + e.getSQLState());
            logger.error("Error adding address", e);
        }
        return false;
    }

    @Override
    public Address deleteAddressByEmpId(int empId) {
        Address address = getAddressByEmpId(empId);
        if (address != null) {
            String query = "DELETE FROM ADDRESS WHERE EMP_ID = ?";
            try (Connection connection = DbConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, empId);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                logger.error("SQL Error Code: " + e.getErrorCode());
                logger.error("SQL State: " + e.getSQLState());
                logger.error("Error deleting address by employee ID", e);
            }
        }
        return address;
    }

    @Override
    public Address updateAddress(Address address) {
        String query = "UPDATE ADDRESS SET STATE = ?, CITY = ?, PINCODE = ? WHERE EMP_ID = ?";
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, address.getState());
            preparedStatement.setString(2, address.getCity());
            preparedStatement.setInt(3, address.getPincode());
            preparedStatement.setInt(4, address.getEmpId());

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                return address;
            }

        } catch (SQLException e) {
            logger.error("SQL Error Code: " + e.getErrorCode());
            logger.error("SQL State: " + e.getSQLState());
            logger.error("Error updating address", e);
        }
        return null;
    }

    private Address mapResultSetToAddress(ResultSet resultSet) throws SQLException {
        Address address = new Address();
        address.setAddressId(resultSet.getInt("ADDRESS_ID"));
        address.setState(resultSet.getString("STATE"));
        address.setCity(resultSet.getString("CITY"));
        address.setPincode(resultSet.getInt("PINCODE"));
        address.setEmpId(resultSet.getInt("EMP_ID"));
        return address;
    }
}
