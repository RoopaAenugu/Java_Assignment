package com.wavemaker.leavemanagement.repository;

import com.wavemaker.leavemanagement.model.EmployeeLeave;
import com.wavemaker.leavemanagement.model.LeaveRequest;

import java.util.List;

public interface EmployeeLeaveRepository {
    public LeaveRequest applyLeave(LeaveRequest leaveRequest);
    public List<EmployeeLeave> getAllAppliedLeaves(int empId);
    public LeaveRequest acceptLeaveRequest(int leaveId);
    public LeaveRequest rejectLeaveRequest(int leaveId);
    public List<EmployeeLeave> getLeavesOfEmployees(List<Integer> employeeIds);
    public int getNumberOfLeavesAllocated(EmployeeLeave employeeLeave);
    public  int getTotalNumberOfLeavesTaken(int empId);

}
