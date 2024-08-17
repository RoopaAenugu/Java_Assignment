package com.javatraining;

import java.util.Arrays;

public class EmployeeData {
    private int empId;
    private String empName;
    private long phoneNumber;
    private short id;
    private float salary;
    private byte age;
    private char gender;
    private boolean marriedStatus;
    private double bank_balance;
    private String[] hobbies;

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public byte getAge() {
        return age;
    }

    public void setAge(byte age) {
        this.age = age;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public boolean isMarriedStatus() {
        return marriedStatus;
    }

    public void setMarriedStatus(boolean marriedStatus) {
        this.marriedStatus = marriedStatus;
    }

    public double getBank_balance() {
        return bank_balance;
    }

    public void setBank_balance(double bank_balance) {
        this.bank_balance = bank_balance;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public void setHobbies(String[] hobbies) {
        this.hobbies = hobbies;
    }

    @Override
    public String toString() {
        return "EmployeeData{" +
                "empId=" + empId +
                ", empName='" + empName + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", id=" + id +
                ", salary=" + salary +
                ", age=" + age +
                ", gender=" + gender +
                ", marriedStatus=" + marriedStatus +
                ", bank_balance=" + bank_balance +
                ", hobbies=" + Arrays.toString(hobbies) +
                '}';
    }
}