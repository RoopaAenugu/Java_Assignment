package com.wavemaker.leavemanagement.model;

public class LeaveType {
    private String typeName;
    private int typeLimit;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "LeaveType{" +
                "typeLimit=" + typeLimit +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
