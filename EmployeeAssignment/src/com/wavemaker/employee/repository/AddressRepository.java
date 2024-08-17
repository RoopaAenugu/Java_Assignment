package com.wavemaker.employee.repository;

import com.wavemaker.employee.model.Address;

public interface AddressRepository {
    public Address getAddressById(int addressId);
    public boolean addAddress(Address address);
    public Address deleteAddress(int addressId);
    public Address updateAddress(Address address);
}
