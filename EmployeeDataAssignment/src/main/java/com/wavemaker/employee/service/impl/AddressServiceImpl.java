package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.employeefactory.AddressRepositoryFactory;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    public AddressServiceImpl(int option) {
        logger.info("Initializing AddressServiceImpl with option: {}", option);
        addressRepository = AddressRepositoryFactory.getAddressRepositoryInstance(option);
    }

    @Override
    public Address getAddressByEmpId(int empId) {
        logger.info("Fetching address for employee ID: {}", empId);
        Address address = addressRepository.getAddressByEmpId(empId);
        if (address != null) {
            logger.info("Address found for employee ID: {}", empId);
        } else {
            logger.warn("No address found for employee ID: {}", empId);
        }
        return address;
    }

    @Override
    public boolean addAddress(Address address) {
        logger.info("Adding new address for employee ID: {}", address.getEmpId());
        boolean result = addressRepository.addAddress(address);
        logger.info("Address added successfully: {}", result);
        return result;
    }

    @Override
    public Address deleteAddressByEmpId(int empId) {
        logger.info("Deleting address for employee ID: {}", empId);
        Address deletedAddress = addressRepository.deleteAddressByEmpId(empId);
        if (deletedAddress != null) {
            logger.info("Address deleted successfully for employee ID: {}", empId);
        } else {
            logger.warn("No address found to delete for employee ID: {}", empId);
        }
        return deletedAddress;
    }

    @Override
    public Address updateAddress(Address address) {
        logger.info("Updating address for employee ID: {}", address.getEmpId());
        Address updatedAddress = addressRepository.updateAddress(address);
        if (updatedAddress != null) {
            logger.info("Address updated successfully for employee ID: {}", address.getEmpId());
        } else {
            logger.warn("Failed to update address for employee ID: {}", address.getEmpId());
        }
        return updatedAddress;
    }
}
