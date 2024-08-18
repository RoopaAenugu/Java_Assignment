package com.wavemaker.employee.controller;

import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.service.AddressService;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.AddressServiceImpl;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;

import java.util.List;
import java.util.Scanner;

public class EmployeeController {

    private static EmployeeService employeeService;
    private static AddressService addressService;


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. In Memory Storage\n2. In File Storage");
        System.out.print("Enter your option to storage: ");
        int storageOption = scanner.nextInt();
        employeeService = new EmployeeServiceImpl(storageOption);
        addressService = new AddressServiceImpl(storageOption);

        while (true) {
            System.out.println("\nEmployee Management System");
            System.out.println("1. Add Employee");
            System.out.println("2. Get Employee by empID");
            System.out.println("3. Get All Employees");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("6. add Address");
            System.out.println("7.  update Address");
            System.out.println("8.  delete Address");
            System.out.println("9.  get Address by empId");
            System.out.println("10. Exit");
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
                    addAddress(scanner);
                    break;
                case 7:
                    updateAddress(scanner);
                    break;
                case 8:
                    deleteAddress(scanner);
                    break;
                case 9:
                    getAddressByEmpId(scanner);
                    break;
                case 10:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private static void getAddressByEmpId(Scanner scanner) {
        System.out.print("Enter Employee ID to get address: ");
        int empId = scanner.nextInt();

        Address address = addressService.getAddressByEmpId(empId);
        if (address != null) {
            printAddress(address);
        } else {
            System.out.println("Employee not found.");
        }

    }

    private static void addAddress(Scanner scanner) {
        System.out.print("Enter empId you want to add: ");
        int empId = scanner.nextInt();
        Employee employee = employeeService.getEmployeeById(empId);

        if (employee != null) {
            if (employee.getAddress() == null) {
                Address address = getEmployeeAddress(scanner, "addAddress");
                address.setEmpId(empId);

                boolean added = addressService.addAddress(address);

                if (added) {

                    employee.setAddress(address);


                    boolean employeeUpdated = employeeService.updateEmployee(employee);

                    if (employeeUpdated) {
                        System.out.println("Address added successfully and associated with the employee.");
                    } else {
                        System.out.println("Failed to update employee with the new address.");
                    }
                } else {
                    System.out.println("Failed to add address.");
                }
            } else {
                System.out.println("Employee address already exists.");
            }
        } else {
            System.out.println("Employee does not exist.");
        }
    }


    private static void deleteAddress(Scanner scanner) {
        System.out.print("Enter Emp ID to delete: ");
        int empId = scanner.nextInt();

        Address address = addressService.getAddressByEmpId(empId);
        if (address != null) {
            Address deleted = addressService.deleteAddressByEmpId(empId);
            if (deleted != null) {
                System.out.println("Employee  address deleted successfully.");
            } else {
                System.out.println("Failed to delete employee address.");
            }
        } else {
            System.out.println("Employee address  not found.");
        }

    }

    private static void updateAddress(Scanner scanner) {
        System.out.print("Enter emp ID to update: ");
        int empId = scanner.nextInt();
        scanner.nextLine();

        Address address = addressService.getAddressByEmpId(empId);
        if (address != null) {
            address = getEmployeeAddress(scanner, "updateAddress");
            address.setEmpId(empId);// Pass the existing address ID
            Address updated = addressService.updateAddress(address);
            if (updated != null) {
                System.out.println("Address updated successfully.");
            } else {
                System.out.println("Failed to update address.");
            }
        } else {
            System.out.println("Address not found.");
        }
    }


    private static void addEmployee(Scanner scanner) {
        Employee employee = getEmployeeDetails(scanner, "add");
        Address address = getEmployeeAddress(scanner, "addAddress");
        if (address != null) {
            address.setEmpId(employee.getEmpId());
            boolean addressAdded = addressService.addAddress(address);
            if (addressAdded) {
                employee.setAddress(getEmployeeAddress(scanner, "addAddress"));
            }
        } else {
            System.out.println("Address details were not provided.");
        }
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
            employee = getEmployeeDetails(scanner, "update");
            employee.setEmpId(id);
            Address address = addressService.getAddressByEmpId(employee.getEmpId());
            if (address != null) {
                address = getEmployeeAddress(scanner, "updateaddress");
                address.setEmpId(id);
            }
            employee.setAddress(address);
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
            if (operation.equals("addAddress")) {
                System.out.println("Enter addressID : ");
                int addressId = scanner.nextInt();
                scanner.nextLine();
                address.setAddressId(addressId);

            }

            System.out.println("Enter State : ");
            String state = scanner.nextLine();
            System.out.println("Enter city :");
            String city = scanner.nextLine();
            System.out.println("Enter Pincode : ");
            int pincode = scanner.nextInt();
            scanner.nextLine();
            address.setState(state);

            address.setCity(city);
            address.setPincode(pincode);
            return address;
        }
        return null;
    }

    private static Employee getEmployeeDetails(Scanner scanner, String operation) {
        Employee employee = new Employee();
        if (operation.equals("add")) {
            System.out.print("Enter Employee ID: ");
            int empId = scanner.nextInt();
            scanner.nextLine();
            employee.setEmpId(empId);


        }

        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Employee Age: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Employee Gender ");
        String gender = scanner.nextLine();
        scanner.nextLine();


        employee.setEmpName(name);
        employee.setAge(age);
        employee.setGender(gender);
        return employee;


    }

    private static void printEmployee(Employee employee) {
        String employeeDetails = String.format(
                "Employee Details -{ empID: %d, Name: %s, Age: %d, Address: %s }",
                employee.getEmpId(),
                employee.getEmpName(),
                employee.getAge(),
                employee.getAddress() != null ?
                        String.format("addressID: %d, State: %s, City: %s, Pincode: %d",
                                employee.getAddress().getAddressId(),
                                employee.getAddress().getState(),
                                employee.getAddress().getCity(),
                                employee.getAddress().getPincode())
                        : "Not assigned"
        );

        System.out.println(employeeDetails);
    }

    private static void printAddress(Address address) {
        String addressDetails = address != null ?
                String.format("Employee address details: {ID: %d, State: %s, City: %s, Pincode: %d}",
                        address.getAddressId(),
                        address.getState(),
                        address.getCity(),
                        address.getPincode())
                : "Address: Not assigned";



        System.out.println(addressDetails);

    }
}

