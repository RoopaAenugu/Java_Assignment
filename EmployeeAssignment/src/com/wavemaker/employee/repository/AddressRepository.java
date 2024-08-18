package com.wavemaker.employee.repository;

import com.wavemaker.employee.model.Address;

import java.util.List;

public interface AddressRepository {
    public Address getAddressByEmpId(int empId);
    public boolean addAddress(Address address);
    public Address deleteAddressByEmpId(int empId);
    public Address updateAddress(Address address);
    public List<Address> readAllAddresses();
    public boolean isAddressExistsForEmpId(int empId);
}
