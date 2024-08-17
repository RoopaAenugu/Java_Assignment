package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.repository.AddressRepository;

import java.util.concurrent.ConcurrentHashMap;

public class AddressRepositoryInMemoryImpl implements AddressRepository {
    private static final ConcurrentHashMap<Integer, Address> addressMap = new ConcurrentHashMap<>();

    @Override
    public Address getAddressById(int addressId) {
        return addressMap.get(addressId);
    }

    @Override
    public boolean addAddress(Address address) {
        if (addressMap.containsKey(address.getAddressId())) {
            return false;
        }
        addressMap.put(address.getAddressId(), address);
        return true;
    }

    @Override
    public Address deleteAddress(int addressId) {
        return addressMap.remove(addressId);
    }

    @Override
    public Address updateAddress(Address address) {
        if (!addressMap.containsKey(address.getAddressId())) {
            return null;
        }
        addressMap.put(address.getAddressId(), address);
        return address;
    }


}

