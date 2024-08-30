package com.wavemaker.leavemanagement.model;

public class EmployeeLeave extends LeaveRequest {
    String empName;
    int typeLimit;
    int totalEmployeeLeaves;

    public int getTotalEmployeeLeaves() {
        return totalEmployeeLeaves;
    }

    public void setTotalEmployeeLeaves(int totalEmployeeLeaves) {
        this.totalEmployeeLeaves = totalEmployeeLeaves;
    }

    public int getTypeLimit() {
        return typeLimit;
    }

    public void setTypeLimit(int typeLimit) {
        this.typeLimit = typeLimit;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    @Override
    public String toString() {
        return "EmployeeLeave{" +
                "empName='" + empName + '\'' +
                ", typeLimit=" + typeLimit +
                '}';
    }
}
