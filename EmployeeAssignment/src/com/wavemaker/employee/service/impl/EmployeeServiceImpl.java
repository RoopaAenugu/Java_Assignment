package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.employeefactory.EmployeeRepositoryFactory;
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
    }

    @Override
    public Employee getEmployeeById(int empId) {
        return employeeRepository.getEmployeeById(empId);
    }

    @Override
    public boolean addEmployee(Employee employee) {
        if(employee.getAddress()!=null){
            addressRepository.addAddress(employee.getAddress());
        }

        return employeeRepository.addEmployee(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();

    }

    @Override
    public boolean updateEmployee(Employee employee) {
        return employeeRepository.updateEmployee(employee);
    }

    @Override
    public boolean deleteEmployee(Employee employee) {
        return employeeRepository.deleteEmployee(employee);

    }
}
