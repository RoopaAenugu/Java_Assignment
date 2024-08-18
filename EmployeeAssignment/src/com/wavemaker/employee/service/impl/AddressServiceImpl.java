package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.employeefactory.AddressRepositoryFactory;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.service.AddressService;

public class AddressServiceImpl implements AddressService {
    private AddressRepository addressRepository;

    public AddressServiceImpl(int option) {
        addressRepository = AddressRepositoryFactory.getAddressRepositoryInstance(option);
    }

    @Override
    public Address getAddressByEmpId(int empId) {
        return addressRepository.getAddressByEmpId(empId);
    }

    @Override
    public boolean addAddress(Address address) {
        return addressRepository.addAddress(address);
    }

    @Override
    public Address deleteAddressByEmpId(int empId) {
        return addressRepository.deleteAddressByEmpId(empId);
    }

    @Override
    public Address updateAddress(Address address) {
        return addressRepository.updateAddress(address);
    }
}
