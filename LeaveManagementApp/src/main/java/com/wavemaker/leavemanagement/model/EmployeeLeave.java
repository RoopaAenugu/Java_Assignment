package com.wavemaker.leavemanagement.model;

public class EmployeeLeave extends LeaveRequest{
    String empName;

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }
}
