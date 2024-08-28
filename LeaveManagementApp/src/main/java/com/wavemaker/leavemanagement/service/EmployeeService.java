package com.wavemaker.leavemanagement.service;

import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.LeaveRequest;

import java.util.List;

public interface EmployeeService {
    public Employee addEmployee(Employee employee);
    public boolean checkManager(String emailId);
    public Employee getEmployeeByLoginId(int loginId);
    public List<Integer> getEmpIdUnderManager(int managerId);
}
