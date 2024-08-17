package com.wavemaker.employee.controller;

import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;

import java.util.List;
import java.util.Scanner;

public class EmployeeController {

    private static EmployeeService employeeService;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. In Memory Storage\n2. In File Storage");
        System.out.print("Enter your option to storage: ");
        int storageOption = scanner.nextInt();
        employeeService = new EmployeeServiceImpl(storageOption);

        while (true) {
            System.out.println("\nEmployee Management System");
            System.out.println("1. Add Employee");
            System.out.println("2. Get Employee by ID");
            System.out.println("3. Get All Employees");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline left-over

            switch (choice) {
                case 1:
                    addEmployee(scanner);
                    break;
                case 2:
                    getEmployeeById(scanner);
                    break;
                case 3:
                    getAllEmployees();
                    break;
                case 4:
                    updateEmployee(scanner);
                    break;
                case 5:
                    deleteEmployee(scanner);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private static void addEmployee(Scanner scanner) {
        Employee employee = getEmployeeDetails(scanner);
        employee.setAddress(getEmployeeAddress(scanner, "AddEmployee"));
        boolean added = employeeService.addEmployee(employee);
        if (added) {
            System.out.println("Employee added successfully.");
        } else {
            System.out.println("Failed to add employee.");
        }
    }

    private static void getEmployeeById(Scanner scanner) {
        System.out.print("Enter Employee ID: ");
        int id = scanner.nextInt();

        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            printEmployee(employee);
        } else {
            System.out.println("Employee not found.");
        }
    }

    private static void getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.println("All Employees:");
            for (Employee emp : employees) {
                printEmployee(emp);
            }
        }
    }

    private static void updateEmployee(Scanner scanner) {
        System.out.print("Enter Employee ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            employee = getEmployeeDetails(scanner);
            employee.setAddress(getEmployeeAddress(scanner, "UpdateEmployee"));
            boolean updated = employeeService.updateEmployee(employee);
            if (updated) {
                System.out.println("Employee updated successfully.");
            } else {
                System.out.println("Failed to update employee.");
            }
        } else {
            System.out.println("Employee not found.");
        }
    }

    private static void deleteEmployee(Scanner scanner) {
        System.out.print("Enter Employee ID to delete: ");
        int id = scanner.nextInt();

        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            boolean deleted = employeeService.deleteEmployee(employee);
            if (deleted) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Failed to delete employee.");
            }
        } else {
            System.out.println("Employee not found.");
        }
    }

    private static Address getEmployeeAddress(Scanner scanner, String operation) {
        int userChoice;
        System.out.println("Do You Want To " + operation + "  Address?\n1. For Yes and 2.For No");
        userChoice = scanner.nextInt();
        scanner.nextLine();
        if (userChoice == 1) {
            Address address = new Address();
            System.out.println("Enter Address Id : ");
            int addressId = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter State : ");
            String state = scanner.nextLine();
            System.out.println("Enter city :");
            String city = scanner.nextLine();
            System.out.println("Enter Pincode : ");
            int pincode = scanner.nextInt();
            scanner.nextLine();
            address.setAddressId(addressId);
            address.setState(state);
            address.setCity(city);
            address.setPincode(pincode);
            return address;
        }
        return null;
    }

    private static Employee getEmployeeDetails(Scanner scanner) {
        System.out.print("Enter Employee ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Employee Age: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Employee Gender ");
        String gender = scanner.nextLine();
        scanner.nextLine();
        Employee employee = new Employee();
        employee.setEmpId(id);
        employee.setEmpName(name);
        employee.setAge(age);
        employee.setGender(gender);
        return employee;


    }

    private static void printEmployee(Employee employee) {
        System.out.println("Employee Details of employeeId :"+employee.getEmpId());
        System.out.println("ID: " + employee.getEmpId());
        System.out.println("Name: " + employee.getEmpName());
        System.out.println("Age: " + employee.getAge());
        Address address = employee.getAddress();
        if (address != null) {
            System.out.println("Address ID: " + address.getAddressId());
            System.out.println("State: " + address.getState());
            System.out.println("City: " + address.getCity());
            System.out.println("Pincode: " + address.getPincode());
        } else {
            System.out.println("Address: Not assigned");
        }
    }
}

