package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.employeefactory.EmployeeRepositoryFactory;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.service.AddressService;

public class AddressServiceImpl implements AddressService {
    public EmployeeServiceImpl(int option) {
        employeeRepository = EmployeeRepositoryFactory.getEmployeeRepositoryInstance(option);
    }


    @Override
    public Address getAddressById(int addressId) {
        return null;
    }

    @Override
    public boolean addAddress(Address address) {
        return false;
    }

    @Override
    public Address deleteAddress(int addressId) {
        return null;
    }

    @Override
    public Address updateAddress(Address address) {
        return null;
    }
}
