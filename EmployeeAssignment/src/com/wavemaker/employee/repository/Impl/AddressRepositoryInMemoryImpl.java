package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.repository.AddressRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AddressRepositoryInMemoryImpl implements AddressRepository {
    private static final ConcurrentHashMap<Integer, Address> addressMap = new ConcurrentHashMap<>();


    @Override
    public Address getAddressByEmpId(int empId) {
        return addressMap.get(empId);
    }

    @Override
    public boolean addAddress(Address address) {
        Address addedAddress = addressMap.put(address.getEmpId(),address);
        return addedAddress != null;
    }

    @Override
    public Address deleteAddressByEmpId(int empId) {
        return addressMap.remove(empId);
    }

    @Override
    public Address updateAddress(Address address) {
        Address updatedAddress = null;
        if (addressMap.containsKey(address.getEmpId())) {
            updatedAddress = addressMap.put(address.getEmpId(), address);
        }
        return updatedAddress;
    }
}
