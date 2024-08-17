package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.repository.AddressRepository;

public class AddressRepositoryInFileImpl implements AddressRepository {

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
