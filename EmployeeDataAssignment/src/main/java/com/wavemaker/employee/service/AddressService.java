package com.wavemaker.employee.service;

import com.wavemaker.employee.model.Address;

public interface AddressService {
    public Address getAddressByEmpId(int empId);
    public boolean addAddress(Address address);
    public Address deleteAddressByEmpId(int empId);
    public Address updateAddress(Address address);
}