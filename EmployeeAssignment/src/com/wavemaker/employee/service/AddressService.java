package com.wavemaker.employee.service;

import com.wavemaker.employee.model.Address;

public interface AddressService {
    public Address getAddressById(int addressId);
    public boolean addAddress(Address address);
    public Address deleteAddress(int addressId);
    public Address updateAddress(Address address);
}
