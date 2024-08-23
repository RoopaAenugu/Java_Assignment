package com.wavemaker.employee.client;

import com.wavemaker.employee.controller.EmployeeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class EmployeeClient {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeClient.class);
    public static void getInputForStorage() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. In Memory Storage\n2. In File Storage\n3. In Database Storage");
        System.out.print("Enter your option to storage: ");
        int storageOption = scanner.nextInt();
        EmployeeController.initializeServices(storageOption);
        while (true) {
            System.out.println("\nEmployee Management System");
            System.out.println("1. Add Employee");
            System.out.println("2. Get Employee by empID");
            System.out.println("3. Get All Employees");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("6. add Address");
            System.out.println("7. update Address");
            System.out.println("8. delete Address");
            System.out.println("9. get Address by empId");
            System.out.println("10  search by email");
            System.out.println("11. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline left-over
            logger.info("user enter the choice for  storage{}", choice);

            switch (choice) {
                case 1:
                    EmployeeController.addEmployee(scanner);
                    break;
                case 2:
                    EmployeeController.getEmployeeById(scanner);
                    break;
                case 3:
                    EmployeeController.getAllEmployees();
                    break;
                case 4:
                    EmployeeController.updateEmployee(scanner);
                    break;
                case 5:
                    EmployeeController.deleteEmployee(scanner);
                    break;
                case 6:
                   EmployeeController.addAddress(scanner);
                    break;
                case 7:
                    EmployeeController.updateAddress(scanner);
                    break;
                case 8:
                    EmployeeController.deleteAddress(scanner);
                    break;
                case 9:
                    EmployeeController.getAddressByEmpId(scanner);
                    break;
                case 10:
                    EmployeeController.getEmployeeByEmail(scanner);
                    break;
                case 11:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }

    }
}
