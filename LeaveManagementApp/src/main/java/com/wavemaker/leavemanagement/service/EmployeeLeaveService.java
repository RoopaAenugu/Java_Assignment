package com.wavemaker.leavemanagement.service;

import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;

import java.util.List;

public interface EmployeeLeaveService {
    public LeaveRequest applyLeave(LeaveRequest leaveRequest);
    public List<LeaveRequest> getAllAppliedLeaves(int empId);
    public LeaveRequest acceptLeaveRequest(int leaveId);
    public LeaveRequest rejectLeaveRequest(int leaveId);
    public List<LeaveRequest> getMyTeamRequests(int managerId);
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds);
}
