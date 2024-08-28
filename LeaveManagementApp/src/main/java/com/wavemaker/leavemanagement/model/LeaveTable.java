package com.wavemaker.leavemanagement.model;


import java.time.LocalDate;

public class LeaveTable {

    private int leavesId;
    private int numberOfLeaves;
    private LocalDate startLocalDate;
    private LocalDate endLocalDate;
    private int employeeId;

    // Default constructor
    public LeaveTable() {
    }

    // Parameterized constructor
    public LeaveTable(int leavesId, int numberOfLeaves, LocalDate startLocalDate, LocalDate endLocalDate, int employeeId) {
        this.leavesId = leavesId;
        this.numberOfLeaves = numberOfLeaves;
        this.startLocalDate = startLocalDate;
        this.endLocalDate = endLocalDate;
        this.employeeId = employeeId;
    }

    // Getters and Setters
    public int getLeavesId() {
        return leavesId;
    }

    public void setLeavesId(int leavesId) {
        this.leavesId = leavesId;
    }

    public int getNumberOfLeaves() {
        return numberOfLeaves;
    }

    public void setNumberOfLeaves(int numberOfLeaves) {
        this.numberOfLeaves = numberOfLeaves;
    }

    public LocalDate getStartLocalDate() {
        return startLocalDate;
    }

    public void setStartLocalDate(LocalDate startLocalDate) {
        this.startLocalDate = startLocalDate;
    }

    public LocalDate getEndLocalDate() {
        return endLocalDate;
    }

    public void setEndLocalDate(LocalDate endLocalDate) {
        this.endLocalDate = endLocalDate;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "LeaveTable{" +
                "leavesId=" + leavesId +
                ", numberOfLeaves=" + numberOfLeaves +
                ", startLocalDate=" + startLocalDate +
                ", endLocalDate=" + endLocalDate +
                ", employeeId=" + employeeId +
                '}';
    }
}
