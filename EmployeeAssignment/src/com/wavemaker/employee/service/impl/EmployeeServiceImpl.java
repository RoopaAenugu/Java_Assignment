package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.employeefactory.AddressRepositoryFactory;
import com.wavemaker.employee.employeefactory.EmployeeRepositoryFactory;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private AddressRepository addressRepository;

    public EmployeeServiceImpl(int option) {
        employeeRepository = EmployeeRepositoryFactory.getEmployeeRepositoryInstance(option);
        addressRepository = AddressRepositoryFactory.getAddressRepositoryInstance(option);
    }

    @Override
    public Employee getEmployeeById(int empId) {
        Employee employee = employeeRepository.getEmployeeById(empId);
        Address address = addressRepository.getAddressByEmpId(empId);
        if (address != null) {
            employee.setAddress(address);
        }
        return employee;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        if (employee.getAddress() != null) {
            addressRepository.addAddress(employee.getAddress());
        }
        return employeeRepository.addEmployee(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = employeeRepository.getAllEmployees();
        for (Employee employee : employees) {
            Address address = addressRepository.getAddressByEmpId(employee.getEmpId());

            if (address != null) {
                employee.setAddress(address);
            }
        }
        return employees;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        if (employee.getAddress() != null) {
            addressRepository.updateAddress(employee.getAddress());;
        }
        return employeeRepository.updateEmployee(employee);
    }

    @Override
    public boolean deleteEmployee(Employee employee) {
        if (employee.getAddress() != null) {
            addressRepository.deleteAddressByEmpId(employee.getEmpId());
        }
        return employeeRepository.deleteEmployee(employee);
    }
}
