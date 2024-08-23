package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.employeefactory.AddressRepositoryFactory;
import com.wavemaker.employee.employeefactory.EmployeeRepositoryFactory;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    public EmployeeServiceImpl(int option) {
        logger.info("Initializing EmployeeServiceImpl with option: {}", option);
        employeeRepository = EmployeeRepositoryFactory.getEmployeeRepositoryInstance(option);
        addressRepository = AddressRepositoryFactory.getAddressRepositoryInstance(option);
    }

    @Override
    public Employee getEmployeeById(int empId) {
        logger.info("Fetching employee with ID: {}", empId);
        Employee employee = employeeRepository.getEmployeeById(empId);
        if (employee == null) {
            logger.warn("Employee with ID {} not found.", empId);
            return null;
        }
        Address address = addressRepository.getAddressByEmpId(empId);
        if (address != null) {
            employee.setAddress(address);
            logger.info("Address found and set for employee ID: {}", empId);
        } else {
            logger.warn("No address found for employee ID: {}", empId);
        }
        return employee;
    }

    @Override
    public int addEmployee(Employee employee) {
        int empId = employeeRepository.addEmployee(employee);
        logger.info("Employee added successfully: {}", empId);

        if (employee.getAddress() != null) {
            employee.getAddress().setEmpId(empId);
            addressRepository.addAddress(employee.getAddress());
            logger.info("Address added for employee ID: {}", employee.getEmpId());
        }


        return empId;
    }

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        List<Employee> employees = employeeRepository.getAllEmployees();
        for (Employee employee : employees) {
            Address address = addressRepository.getAddressByEmpId(employee.getEmpId());
            if (address != null) {
                employee.setAddress(address);
                logger.info("Address found and set for employee ID: {}", employee.getEmpId());
            } else {
                logger.warn("No address found for employee ID: {}", employee.getEmpId());
            }
        }
        return employees;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        logger.info("Updating employee: {}", employee);
        if (employee.getAddress() != null) {
            addressRepository.updateAddress(employee.getAddress());
            logger.info("Address updated for employee ID: {}", employee.getEmpId());
        }
        boolean result = employeeRepository.updateEmployee(employee);
        logger.info("Employee updated successfully: {}", result);
        return result;
    }

    @Override
    public boolean deleteEmployeeByEmpId(int empId) {
        logger.info("Deleting employee emId: {}", empId);
        Employee employee=employeeRepository.getEmployeeById(empId);
        if (employee!=null&&employee.getAddress() != null) {
            addressRepository.deleteAddressByEmpId(employee.getEmpId());
            logger.info("Address deleted for employee ID: {}", employee.getEmpId());
        }
        boolean result = employeeRepository.deleteEmployeeByEmpId(empId);
        logger.info("Employee deleted successfully: {}", result);
        return result;
    }

    @Override
    public Employee getEmployeeByEmail(String email) {
        logger.info("Fetching employee with email: {}", email);
        Employee employee = employeeRepository.getEmployeeByEmail(email);
        if (employee != null) {
            Address address = addressRepository.getAddressByEmpId(employee.getEmpId());
            if (address != null) {
                employee.setAddress(address);
                logger.info("Address found and set for employee ID: {}", employee.getEmpId());
            } else {
                logger.warn("No address found for employee ID: {}", employee.getEmpId());
            }
        } else {
            logger.warn("No employee found with email: {}", email);
        }
        return employee;
    }
}
