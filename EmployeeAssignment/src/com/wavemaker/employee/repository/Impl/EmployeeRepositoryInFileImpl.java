package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.exception.EmployeeFileReadException;
import com.wavemaker.employee.exception.EmployeeFileWriteException;
import com.wavemaker.employee.exception.EmployeeNotFoundException;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.util.FileCreateUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryInFileImpl implements EmployeeRepository {
    private static final String FILE_PATH = "C:\\Users\\roopaa_700059\\IdeaProjects\\Java_Assignments\\Employees.txt";
    private static File file;

    public EmployeeRepositoryInFileImpl() {
        file = FileCreateUtil.createFileIfNotExists(FILE_PATH);
    }

    @Override
    public Employee getEmployeeById(int empId) {

        BufferedReader reader=null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 4) {
                    continue;
                }
                int currentEmpId = Integer.parseInt(fields[0]);
                if (currentEmpId == empId) {
                    return createEmployeeFromFields(fields);
                }
            }
            throw new EmployeeNotFoundException("Employee with ID: " + empId + " not found", 404);
        } catch (IOException e) {
            throw new EmployeeFileReadException("Error reading employee details from file",  500);
        } finally {
            closeBufferedReader(reader);

        }
    }


    @Override
    public boolean addEmployee(Employee employee) {
        BufferedWriter writer=null;
        try {
            writer = new BufferedWriter(new FileWriter(this.file,true));
            String line = createEmployeeLine(employee);
            writer.write(line);
            writer.newLine();
            return true;
        } catch (IOException e) {
            throw new EmployeeFileWriteException("Error writing employee details to file", e, 500);
        }finally {
            closeBufferedWriter(writer);
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 4) {
                    continue; // Skip any malformed lines
                }
                employees.add(createEmployeeFromFields(fields));
            }
        } catch (IOException e) {
            throw new EmployeeFileReadException("Error reading employee details from file",  500);
        }
        return employees;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        List<Employee> employees = getAllEmployees();
        boolean updated = false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file))) {
            for (Employee emp : employees) {
                if (emp.getEmpId() == employee.getEmpId()) {
                    writer.write(createEmployeeLine(employee));
                    writer.newLine();
                    updated = true;
                } else {
                    writer.write(createEmployeeLine(emp));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new EmployeeFileWriteException("Error updating employee details in file", e, 500);
        }
        return updated;
    }

    @Override
    public boolean deleteEmployee(Employee employee) {
        List<Employee> employees = getAllEmployees();
        boolean deleted = employees.removeIf(emp -> emp.getEmpId() == employee.getEmpId());

        if (deleted) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file))) {
                for (Employee emp : employees) {
                    writer.write(createEmployeeLine(emp));
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new EmployeeFileWriteException("Error deleting employee details from file", e, 500);
            }
        }

        return deleted;
    }

    private Employee createEmployeeFromFields(String[] fields) {
        Employee employee = new Employee();
        employee.setEmpId(Integer.parseInt(fields[0]));
        employee.setEmpName(fields[1]);
        employee.setAge(Integer.parseInt(fields[2]));
        employee.setGender(fields[3]);
        return employee;
    }

    private String createEmployeeLine(Employee employee) {
        return employee.getEmpId() + "," +
                employee.getEmpName() + "," +
                employee.getAge() + "," +
                employee.getGender();
    }

    private void closeBufferedReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Failed to Close the Reader," + e.getMessage());
            }
        }
    }

    private void closeBufferedWriter(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to close writer: " + e.getMessage());
            }
        }
    }
}
