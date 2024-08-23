package com.wavemaker.employee.repository;

import com.wavemaker.employee.model.Employee;

import java.util.List;

public interface EmployeeRepository {
    public Employee getEmployeeById(int empId);
    public  int addEmployee(Employee employee);
    public List<Employee> getAllEmployees();
    public boolean updateEmployee(Employee employee);
    public boolean deleteEmployeeByEmpId(int empId);
    public Employee getEmployeeByEmail(String email);
}
