package com.wavemaker.employee.controller;

import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.service.AddressService;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.service.impl.AddressServiceImpl;
import com.wavemaker.employee.service.impl.EmployeeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class EmployeeController {

    private static EmployeeService employeeService;
    private static AddressService addressService;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private static boolean employeeHeadings;
    private static boolean addressHeading;


    public static void initializeServices(int storageOption) {
        employeeService = new EmployeeServiceImpl(storageOption);
        addressService = new AddressServiceImpl(storageOption);
        logger.info("created addressservice object");
    }

    public static void getAddressByEmpId(Scanner scanner) {
        System.out.print("Enter Employee ID to get address: ");
        int empId = scanner.nextInt();

        Address address = addressService.getAddressByEmpId(empId);
        if (address != null) {
            printAddress(address);
        } else {
            System.out.println("Employee address not found.");
        }

    }

    public static void addAddress(Scanner scanner) {
        System.out.print("Enter empId you want to add: ");
        int empId = scanner.nextInt();
        Employee employee = employeeService.getEmployeeById(empId);

        if (employee != null) {
            if (employee.getAddress() == null) {
                Address address = getEmployeeAddress(scanner, "addAddress");
                if (address != null) {
                    address.setEmpId(empId);
                    boolean added = addressService.addAddress(address);
                    if (added) {
                        System.out.println("address added successfully");

                    } else {
                        System.out.println("Failed to add address.");
                    }

                }

            } else {
                System.out.println("Employee address already exists.");
            }
        } else {
            System.out.println("Employee does not exist.");
        }
    }


    public static void deleteAddress(Scanner scanner) {
        System.out.print("Enter Emp ID to delete: ");
        int empId = scanner.nextInt();

        // Delete address from AddressService
        Address address = addressService.getAddressByEmpId(empId);
        if (address != null) {
            Address deleted = addressService.deleteAddressByEmpId(empId);
            if (deleted != null) {
                // Address deleted successfully, now update Employee to remove the address reference
                Employee employee = employeeService.getEmployeeById(empId);
                if (employee != null) {
                    employee.setAddress(null); // Remove address reference
                    employeeService.updateEmployee(employee); // Update employee in the repository
                    System.out.println("Employee address deleted successfully.");
                } else {
                    System.out.println("Employee not found.");
                }
            } else {
                System.out.println("Failed to delete employee address.");
            }
        } else {
            System.out.println("Employee address not found.");
        }
    }


    public static void updateAddress(Scanner scanner) {
        System.out.print("Enter emp ID to update: ");
        int empId = scanner.nextInt();
        scanner.nextLine();

        Address address = addressService.getAddressByEmpId(empId);
        if (address != null) {
            int addressId = address.getAddressId();
            address = getEmployeeAddress(scanner, "updateAddress");
            if (address != null) {
                address.setEmpId(empId);// Pass the existing address ID
                address.setAddressId(addressId);
                Address updated = addressService.updateAddress(address);
                if (updated != null) {
                    System.out.println("Address updated successfully.");
                } else {
                    System.out.println("Failed to update address.");
                }


            }

        } else {
            System.out.println("Address not found.");
        }
    }


    public static void addEmployee(Scanner scanner) {
        Employee employee = getEmployeeDetails(scanner, "add");
        Address address = getEmployeeAddress(scanner, "addAddress");
        if (employee != null) {
            employee.setAddress(address);
        }
        int empId = employeeService.addEmployee(employee);
        if (empId != -1) {
            System.out.println("Employee added successfully.");
        } else {
            System.out.println("Failed to add employee.");
        }

    }

    public static void getEmployeeById(Scanner scanner) {
        System.out.print("Enter Employee ID: ");
        int id = scanner.nextInt();

        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            employeeHeadings = false;
            printEmployee(employee);
        } else {
            System.out.println("Employee not found.");
        }
    }

    public static void getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.println("All Employees:");
            employeeHeadings = false;
            for (Employee emp : employees) {
                printEmployee(emp);
            }
        }
    }

    public static void updateEmployee(Scanner scanner) {
        System.out.print("Enter Employee ID to update: ");
        int empId = scanner.nextInt();
        scanner.nextLine();
        Employee employee = employeeService.getEmployeeById(empId);
        if (employee != null) {
            employee = getEmployeeDetails(scanner, "update");
            if (employee != null) {
                employee.setEmpId(empId);

                Address address = addressService.getAddressByEmpId(employee.getEmpId());
                if (address != null) {
                    int addressId = address.getAddressId();
                    address = getEmployeeAddress(scanner, "updateaddress");
                    if (address != null) {
                        address.setEmpId(empId);
                        address.setAddressId(addressId);
                        addressService.updateAddress(address);
                    } else {
                        logger.warn("address is not updated");
                    }

                } else {
                    logger.warn("address is not found for {} employee id", empId);
                }
                employee.setAddress(address);
                boolean updated = employeeService.updateEmployee(employee);
                if (updated) {
                    System.out.println("Employee updated successfully.");
                } else {
                    System.out.println("Failed to update employee.");
                }
            } else {
                logger.warn("Employee details are not updated");
            }
        } else {
            System.out.println("Employee not found.");
        }
    }

    public static void deleteEmployee(Scanner scanner) {
        System.out.print("Enter Employee ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            boolean deleted = employeeService.deleteEmployeeByEmpId(id);
            if (deleted) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Failed to delete employee.");
            }
        } else {
            System.out.println("Employee not found.");
        }
    }

    public static void getEmployeeByEmail(Scanner scanner) {
        String email;
        System.out.println("enter email to search employee");
        email = scanner.nextLine();
        Employee employee = employeeService.getEmployeeByEmail(email);
        if (employee != null) {
            employeeHeadings = false;
            printEmployee(employee);
        } else {
            System.out.println("employee not found for" + email);
        }
    }

    private static Address getEmployeeAddress(Scanner scanner, String choice) {
        int userChoice;
        System.out.println("Do You Want To " + choice + "  Address?\n1. For Yes and 2.For No");
        userChoice = scanner.nextInt();
        scanner.nextLine();
        if (userChoice == 1) {
            Address address = new Address();
            System.out.println("Enter State : ");
            String state = scanner.nextLine();
            System.out.println("Enter city :");
            String city = scanner.nextLine();
            System.out.println("Enter Pincode : ");
            int pincode = scanner.nextInt();
            // Validate pincode
            if (pincode < 100000 || pincode > 999999) {
                System.out.println("Invalid pincode. It should be a 6-digit number.");
                return null;
            }
            scanner.nextLine();
            address.setState(state);
            address.setCity(city);
            address.setPincode(pincode);
            return address;
        }
        return null;
    }

    private static Employee getEmployeeDetails(Scanner scanner, String choice) {
        System.out.println(choice + " employee");//choice is either add or update employee
        Employee employee = new Employee();
        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Employee Age: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Employee Gender ");
        String gender = scanner.nextLine();
        System.out.println("Enter Employee email ");
        String email = scanner.nextLine();
        // Validate email format
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return null;
        }


        employee.setEmpName(name);
        employee.setAge(age);
        employee.setGender(gender);
        employee.setEmail(email);
        return employee;


    }

    private static boolean isValidEmail(String email) {
        // Simple regex for validating email format
        String emailRegex = "^[a-zA-Z0-9_+.-]+@[a-zA-Z0-9.-]+$";
        return Pattern.matches(emailRegex, email);

    }

    private static void printEmployee(Employee employee) {
        // Define table headers
        if (!employeeHeadings) {
            // Print the header row (only once)
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-10s %-20s %-5s %-10s %-30s %-50s%n",
                    "Emp_Id", "Name", "Age", "Gender", "Email", "Address");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------");
            employeeHeadings = true;
        }

        // Print the employee details
        System.out.printf("%-10d %-20s %-5d %-10s %-30s %-50s%n",
                employee.getEmpId(),
                employee.getEmpName(),
                employee.getAge(),
                employee.getGender(),
                employee.getEmail(),
                employee.getAddress() != null ?
                        String.format("ID: %d, State: %s, City: %s, Pincode: %d, Emp_Id: %d",
                                employee.getAddress().getAddressId(),
                                employee.getAddress().getState(),
                                employee.getAddress().getCity(),
                                employee.getAddress().getPincode(),
                                employee.getAddress().getEmpId())
                        : "Not assigned"
        );
    }

    private static void printAddress(Address address) {
        // Define table headers
        if (!addressHeading) {
            // Print the header row (only once)
            System.out.printf("%-10s %-20s %-20s %-10s %-10s%n",
                    "Address_ID", "State", "City", "Pincode", "Emp ID");
            System.out.println("--------------------------------------------------------------------");
            addressHeading = true;
        }

        // Print the address details
        System.out.printf("%-10d %-20s %-20s %-10d %-10d%n",
                address.getAddressId(),
                address.getState(),
                address.getCity(),
                address.getPincode(),
                address.getEmpId()
        );
    }


}

