package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.repository.Impl.EmployeeRepositoryInFileImpl;
import com.wavemaker.employee.repository.Impl.EmployeeRepositoryInMemoryImpl;
import com.wavemaker.employee.service.EmployeeService;

import java.util.ArrayList;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private int option;
    private List<Employee> employeeList;
    private EmployeeRepository employeeRepository;
    public EmployeeServiceImpl(int option) {
        this.option = option;
        if(option == 1){
            employeeRepository=new EmployeeRepositoryInMemoryImpl();
        }
        else if(option == 2){
            employeeRepository=new EmployeeRepositoryInFileImpl();
        }


    }
    @Override
    public Employee getEmployeeById(int empId) {
        return employeeRepository.getEmployeeById(empId);
    }

    @Override
    public boolean addEmployee(Employee employee) {

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
