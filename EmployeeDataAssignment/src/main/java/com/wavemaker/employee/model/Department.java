package com.wavemaker.employee.model;

import java.util.Objects;

public class Department {
    private int deptId;
    private String deptName;


    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return deptId == that.deptId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(deptId);
    }
}
