package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.exception.employee.EmployeeAlreadyExistsException;
import com.wavemaker.employee.exception.employee.EmployeeNotFoundException;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EmployeeRepositoryInMemoryImpl implements EmployeeRepository {
    private static AddressRepository addressRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRepositoryInMemoryImpl.class);
    private static final Map<Integer, Employee> employeeMap = new HashMap<>();
    private static final Map<String, Employee> employeeEmailMap = new HashMap<>();

    @Override
    public Employee getEmployeeById(int empId) {
        logger.info("Fetching employee with ID: {}", empId);
        Employee employee = employeeMap.get(empId);
        if (employee == null) {
            logger.error("Employee with ID {} not found.", empId);
            throw new EmployeeNotFoundException("Employee with ID " + empId + " not found.", 404);
        }
        logger.info("Employee found: {}", employee);
        return employee;
    }

    @Override
    public int addEmployee(Employee employee) {
        logger.info("Adding new employee: {}", employee);
        int empId = -1;
        empId = getMaxEmpId();
        empId += 1;
        employee.setEmpId(empId);
        logger.debug("Generated new employee ID: {}", empId);

        logger.info("Adding employee with ID: {}", employee.getEmpId());

        if (employeeMap.containsKey(employee.getEmpId())) {
            logger.error("Employee with ID {} already exists.", employee.getEmpId());
            throw new EmployeeAlreadyExistsException("Employee with ID " + employee.getEmpId() + " already exists.");
        }
        employeeMap.put(employee.getEmpId(), employee);
        employeeEmailMap.put(employee.getEmail(), employee);
        logger.info("Employee with ID {} added successfully.", employee.getEmpId());
        return empId;
    }

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees.");
        return new ArrayList<>(employeeMap.values());
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        logger.info("Updating employee with ID: {}", employee.getEmpId());
        if (!employeeMap.containsKey(employee.getEmpId())) {
            logger.error("Employee with ID {} not found.", employee.getEmpId());
            throw new EmployeeNotFoundException("Employee with ID " + employee.getEmpId() + " not found.", 404);
        }
        employeeMap.put(employee.getEmpId(), employee);
        logger.info("Employee with ID {} updated successfully.", employee.getEmpId());
        return true;
    }

    @Override
    public boolean deleteEmployeeByEmpId(int empId) {
        logger.info("Deleting employee with ID: {}", empId);
        if (!employeeMap.containsKey(empId)) {
            logger.error("Employee with ID {} not found.", empId);
            throw new EmployeeNotFoundException("Employee with ID " + empId + " not found.", 404);
        }
        employeeMap.remove(empId);
        logger.info("Employee with ID {} deleted successfully.", empId);
        return true;
    }


    private int getMaxEmpId() {
        int maxEmpId = 0;
        for (int i : employeeMap.keySet()) {
            maxEmpId = Math.max(maxEmpId, i);
        }
        return maxEmpId;
    }

    @Override
    public Employee getEmployeeByEmail(String email) {
        Employee employee = employeeEmailMap.get(email);
        if (employee != null) {
            return employee;
        }
        return null;
    }


}
