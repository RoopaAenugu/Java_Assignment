package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.model.Employee;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
import com.wavemaker.leavemanagement.repository.EmployeeRepository;
import com.wavemaker.leavemanagement.repository.impl.EmployeeLeaveRepositoryImpl;
import com.wavemaker.leavemanagement.repository.impl.EmployeeRepositoryImpl;
import com.wavemaker.leavemanagement.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    // Constructor to inject UserCookieTaskRepository
    public EmployeeServiceImpl() {
        this.employeeRepository = new  EmployeeRepositoryImpl();
    }


    @Override
    public Employee addEmployee(Employee employee) {
        return employeeRepository.addEmployee(employee);
    }

    @Override
    public boolean checkManager(String emailId) {
      return  employeeRepository.checkManager(emailId);
    }

    @Override
    public Employee getEmployeeByLoginId(int loginId) {
        return employeeRepository.getEmployeeByLoginId(loginId);
    }
    @Override
    public List<Integer> getEmpIdUnderManager(int managerId) {
        return employeeRepository.getEmpIdUnderManager(managerId);
    }
}
