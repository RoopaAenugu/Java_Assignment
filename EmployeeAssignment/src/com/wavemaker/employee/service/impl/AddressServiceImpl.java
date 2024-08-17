package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.employeefactory.AddressRepositoryFactory;
import com.wavemaker.employee.employeefactory.EmployeeRepositoryFactory;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.service.AddressService;

public class AddressServiceImpl implements AddressService {
    private AddressRepository addressRepository;
    public AddressServiceImpl(int option) {
         addressRepository = AddressRepositoryFactory.getAddressRepositoryInstance(option);
    }


    @Override
    public Address getAddressById(int addressId) {
        return addressRepository.getAddressById(addressId);
    }

    @Override
    public boolean addAddress(Address address) {
       return addressRepository.addAddress(address);
    }

    @Override
    public Address deleteAddress(int addressId) {
      return addressRepository.deleteAddress(addressId);
    }

    @Override
    public Address updateAddress(Address address) {
       return addressRepository.updateAddress(address);
    }
}
