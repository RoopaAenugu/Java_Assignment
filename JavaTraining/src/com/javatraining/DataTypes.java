package com.javatraining;

import com.javatraining.EmployeeData;

public class DataTypes {

    public static void main(String[] args) {
      EmployeeData emp = new EmployeeData();
        emp.setEmpId(10);
        emp.setEmpName("roopa");
        emp.setPhoneNumber((long)775072569);
        emp.setSalary(5000);
        emp.setGender('f');
        emp.setId((short) 1);
        emp.setAge((byte)22);
        emp. setMarriedStatus(false);
        emp.setBank_balance(1000000);
       emp.setHobbies(new String[]{"music", "watching movies", "reading books"});
      System.out.println(emp.toString());



    }


}
