package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.exception.employee.*;
import com.wavemaker.employee.model.Employee;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.util.FileCreateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryInFileImpl implements EmployeeRepository {
    private static final String FILE_PATH = "C:\\Users\\roopaa_700059\\IdeaProjects\\Java_Assignments\\Employees.txt";
    private static File file;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeRepositoryInFileImpl.class);

    public EmployeeRepositoryInFileImpl() {
        file = FileCreateUtil.createFileIfNotExists(FILE_PATH);
    }

    @Override
    public Employee getEmployeeById(int empId) {
        logger.info("Fetching employee with ID: {}", empId);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    logger.warn("Malformed line skipped: {}", line);
                    continue;
                }
                int currentEmpId = Integer.parseInt(fields[0]);
                if (currentEmpId == empId) {
                    logger.info("Employee found with ID: {}", empId);
                    return createEmployeeFromFields(fields);
                }
            }
            logger.warn("Employee with ID: {} not found", empId);
            throw new EmployeeNotFoundException("Employee with ID: " + empId + " not found", 404);
        } catch (IOException e) {
            logger.error("Error reading employee details from file", e);
            throw new EmployeeFileReadException("Error reading employee details from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
    }

    @Override
    public int addEmployee(Employee employee) {
        logger.info("Adding new employee: {}", employee);
        int empId = -1;
        empId = getMaxEmpId() + 1;
        employee.setEmpId(empId);
        logger.debug("Generated new employee ID: {}", empId);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            String line = createEmployeeLine(employee);
            writer.write(line);
            writer.newLine();
            logger.info("Employee with ID: {} added successfully", employee.getEmpId());


        } catch (IOException e) {
            logger.error("Error writing employee details to file", e);
            throw new EmployeeFileWriteException("Error writing employee details to file", e, 500);
        } finally {
            closeBufferedWriter(writer);
        }
        return empId;
    }

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    logger.warn("Malformed line skipped: {}", line);
                    continue;
                }
                employees.add(createEmployeeFromFields(fields));
            }
            logger.info("Total {} employees found", employees.size());
        } catch (IOException e) {
            logger.error("Error reading employee details from file", e);
            throw new EmployeeFileReadException("Error reading employee details from file", 500);
        }
        return employees;
    }

    public boolean updateEmployee(Employee employee) throws EmployeeFileUpdateException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = new File(file.getAbsolutePath().replace(".txt", "_temp.txt"));

        try {
            reader = new BufferedReader(new FileReader(this.file));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int empId = Integer.parseInt(fields[0]);
                if (empId == employee.getEmpId()) {
                    line = createEmployeeLine(employee);
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new EmployeeFileUpdateException("Error updating employee file", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }

        if (!renameTo(tempFile, file)) {
            throw new EmployeeFileUpdateException("Error replacing the original file with the updated file", 500);
        }

        if (employee != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEmployee(Employee employee) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = new File(file.getAbsolutePath().replace(".txt", "_temp.txt"));
        boolean isEmployeeDeleted = false;

        try {
            reader = new BufferedReader(new FileReader(this.file));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int currentEmpId = Integer.parseInt(fields[0]);
                if (currentEmpId == employee.getEmpId()) {
                    isEmployeeDeleted=true;

                    continue;
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new EmployeeFileDeletionException("Error processing employee file for deletion", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }
        if (!renameTo(tempFile, file)) {
            throw new EmployeeFileDeletionException("Error replacing the original file with the updated file after deletion", 500);
        }

        return isEmployeeDeleted;
    }


    private int getMaxEmpId() {
        int maxEmpId = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int currentEmpId = Integer.parseInt(fields[0]);
                maxEmpId = Math.max(maxEmpId, currentEmpId);
            }
        } catch (IOException e) {
            throw new EmployeeFileReadException("Error reading employee details from file", 500);
        }
        return maxEmpId;
    }

    private boolean renameTo(File source, File destination) throws EmployeeFileUpdateException {
        try {
            if (destination.exists() && !destination.delete()) {
                throw new IOException("Failed to delete existing file: " + destination.getAbsolutePath());
            }
            return source.renameTo(destination);
        } catch (IOException e) {
            throw new EmployeeFileUpdateException("Error deleting original file during update", 500);
        }
    }

    @Override
    public Employee getEmployeeByEmail(String email) {
        logger.info("Fetching employee with email: {}", email);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    logger.warn("Malformed line skipped: {}", line);
                    continue;
                }
                String currentEmail = String.valueOf(fields[4]);
                if (currentEmail.equals(email)) {
                    logger.info("Employee found with email: {}", email);
                    return createEmployeeFromFields(fields);
                }
            }
            logger.warn("Employee with email: {} not found", email);
            throw new EmployeeNotFoundException("Employee with email: " + email + " not found", 404);
        } catch (IOException e) {
            logger.error("Error reading employee details from file", e);
            throw new EmployeeFileReadException("Error reading employee details from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
    }


    private Employee createEmployeeFromFields(String[] fields) {
        Employee employee = new Employee();
        employee.setEmpId(Integer.parseInt(fields[0]));
        employee.setEmpName(String.valueOf(fields[1]));
        employee.setAge(Integer.parseInt(fields[2]));
        employee.setGender(String.valueOf(fields[3]));
        employee.setEmail(String.valueOf(fields[4]));
        logger.info("Employee object created: {}", employee);
        return employee;
    }

    private String createEmployeeLine(Employee employee) {
        return employee.getEmpId() + "," +
                employee.getEmpName() + "," +
                employee.getAge() + "," +
                employee.getGender() + "," +
                employee.getEmail();
    }

    private void closeBufferedReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
                logger.debug("BufferedReader closed successfully");
            } catch (IOException e) {
                logger.error("Failed to close BufferedReader", e);
            }
        }
    }

    private void closeBufferedWriter(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
                logger.debug("BufferedWriter closed successfully");
            } catch (IOException e) {
                logger.error("Failed to close BufferedWriter", e);
            }
        }
    }
}
