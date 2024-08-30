package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;
import com.wavemaker.leavemanagement.repository.EmployeeCookieRepository;
import com.wavemaker.leavemanagement.repository.EmployeeLeaveRepository;
import com.wavemaker.leavemanagement.repository.impl.EmployeeCookieRepositoryImpl;
import com.wavemaker.leavemanagement.repository.impl.EmployeeLeaveRepositoryImpl;
import com.wavemaker.leavemanagement.service.EmployeeLeaveService;

import java.util.List;

public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {
    private final EmployeeLeaveRepository employeeLeaveRepository;

    // Constructor to inject UserCookieTaskRepository
    public EmployeeLeaveServiceImpl() {
        this.employeeLeaveRepository = new EmployeeLeaveRepositoryImpl();
    }

    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) {
      return  employeeLeaveRepository.applyLeave(leaveRequest);
    }
    @Override
    public List<EmployeeLeave> getAllAppliedLeaves(int empId) {
        return employeeLeaveRepository.getAllAppliedLeaves(empId);
    }

    @Override
    public LeaveRequest acceptLeaveRequest(int leaveId) {
        return employeeLeaveRepository.acceptLeaveRequest(leaveId);
    }

    @Override
    public LeaveRequest rejectLeaveRequest(int leaveId) {
        return employeeLeaveRepository.rejectLeaveRequest(leaveId);
    }



    @Override
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds) {
        return employeeLeaveRepository.getLeavesOfEmployees(employeeIds);
    }

    @Override
    public int getNumberOfLeavesAllocated(EmployeeLeave employeeLeave) {
        return employeeLeaveRepository.getNumberOfLeavesAllocated(employeeLeave);
    }

    @Override
    public int getTotalNumberOfLeavesTaken(int empId) {
        return  employeeLeaveRepository.getTotalNumberOfLeavesTaken(empId);
    }

}
