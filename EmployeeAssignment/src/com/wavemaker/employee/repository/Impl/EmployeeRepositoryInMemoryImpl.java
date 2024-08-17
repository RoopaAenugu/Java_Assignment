package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.exception.EmployeeAlreadyExistsException;
import com.wavemaker.employee.exception.EmployeeNotFoundException;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.EmployeeRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmployeeRepositoryInMemoryImpl implements EmployeeRepository {
    private  static  AddressRepository addressRepository;

    private static final ConcurrentHashMap<Integer,Employee> employeeMap = new ConcurrentHashMap<>();

    @Override
    public Employee getEmployeeById(int empId) {
        Employee employee = employeeMap.get(empId);
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee with ID " + empId + " not found.");
        }
        return employee;
    }

    @Override
    public boolean addEmployee(Employee employee) {
        if (employeeMap.containsKey(employee.getEmpId())) {
            throw new EmployeeAlreadyExistsException("Employee with ID " + employee.getEmpId() + " already exists.");
        }
        employeeMap.put(employee.getEmpId(), employee);
        return true;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        if (!employeeMap.containsKey(employee.getEmpId())) {
            throw new EmployeeNotFoundException("Employee with ID " + employee.getEmpId() + " not found.");
        }
        employeeMap.put(employee.getEmpId(), employee); // Update existing employee
        return true;
    }

    @Override
    public boolean deleteEmployee(Employee employee) {
        if (!employeeMap.containsKey(employee.getEmpId())) {
            throw new EmployeeNotFoundException("Employee with ID " + employee.getEmpId() + " not found.");
        }
        employeeMap.remove(employee.getEmpId());
        return true;
    }
}
