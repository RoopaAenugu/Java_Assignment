package com.javatraining;

import java.util.HashMap;
import java.util.Map;

abstract class Abstraction {
    public abstract void display();
}
class AbstractChild extends Abstraction {

    @Override
    public void display() {
        System.out.println("Abstraction");

    }
}
class main{
    public static void main(String[] args) {
        AbstractChild a = new AbstractChild();
        a.display();
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        HashMap<Integer, Employee> employeeMap = new HashMap<>();
        employeeMap.put(1, employee1);
        employeeMap.put(2, employee2);
        System.out.println(employeeMap.containsValue(employee1));
        System.out.println(employeeMap);
    }
}
