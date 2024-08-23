package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.exception.address.*;
import com.wavemaker.employee.exception.employee.EmployeeFileReadException;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.util.FileCreateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AddressRepositoryInFileImpl implements AddressRepository {
    private static final String FILE_PATH = "C:\\Users\\roopaa_700059\\IdeaProjects\\Java_Assignments\\address.txt";

    private final File file;
    private static final Logger logger = LoggerFactory.getLogger(AddressRepositoryInFileImpl.class);

    public AddressRepositoryInFileImpl() {
        this.file = FileCreateUtil.createFileIfNotExists(FILE_PATH);
        logger.info("Address file initialized at: {}", FILE_PATH);
    }

    @Override
    public Address getAddressByEmpId(int empId) throws AddressFileReadException {
        logger.info("Fetching address for employee ID: {}", empId);
        BufferedReader reader = null;
        Address address = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    logger.warn("Malformed line skipped: {}", line);
                    continue;
                }
                int currentEmpId = Integer.parseInt(fields[1]);
                if (currentEmpId == empId) {
                    logger.info("Address found for employee ID: {}", empId);
                    return createAddressFromFields(fields);
                }
            }
            logger.warn("No address found for employee ID: {}", empId);

        } catch (IOException e) {
            logger.error("Error reading address details from file", e);
            throw new AddressFileReadException("Error reading address details from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return null;
    }

    @Override
    public boolean addAddress(Address address) throws AddressFileWriteException, DuplicateAddressRecordFoundException {
        logger.info("Adding address for employee ID: {}", address.getEmpId());
        int addressId=getMaxAddressId()+1;
        address.setAddressId(addressId);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.file, true));
            String line = createAddressLine(address);
            writer.write(line);
            writer.newLine();
            logger.info("Address for employee ID: {} added successfully", address.getEmpId());
            return true;
        } catch (IOException e) {
            logger.error("Error writing address details to file", e);
            throw new AddressFileWriteException("Error writing address details to file", 500);
        } finally {
            closeBufferedWriter(writer);
        }
    }


    @Override
    public Address deleteAddressByEmpId(int empId) throws AddressFileDeletionException, AddressNotFoundException {
        logger.info("Deleting address for employee ID: {}", empId);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = new File(file.getAbsolutePath().replace(".txt", "_temp.txt"));
        Address deletedAddress = null;

        try {
            reader = new BufferedReader(new FileReader(this.file));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    logger.warn("Malformed line skipped: {}", line);
                    continue;
                }
                int currentEmpId = Integer.parseInt(fields[1]);
                if (currentEmpId == empId) {
                    deletedAddress = new Address();
                    deletedAddress.setAddressId(Integer.parseInt(fields[0]));
                    deletedAddress.setEmpId(currentEmpId);
                    deletedAddress.setState(fields[2]);
                    deletedAddress.setCity(fields[3]);
                    deletedAddress.setPincode(Integer.parseInt(fields[4]));
                    logger.info("Address found and marked for deletion: {}", deletedAddress);
                    continue;
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            logger.error("Error processing address file for deletion", e);
            throw new AddressFileDeletionException("Error processing address file for deletion", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }
        if (!renameTo(tempFile, file)) {
            logger.error("Error replacing the original file with the updated file after deletion");
            throw new AddressFileDeletionException("Error replacing the original file with the updated file after deletion", 500);
        }
        if (deletedAddress == null) {
            logger.warn("Address not found for employee ID: {}", empId);
            throw new AddressNotFoundException("Address for Employee with ID " + empId + " not found", 404);
        }
        logger.info("Address for employee ID: {} deleted successfully", empId);
        return deletedAddress;
    }

    @Override
    public Address updateAddress(Address address) throws AddressFileUpdateException {
        logger.info("Updating address for employee ID: {}", address.getEmpId());
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File tempFile = new File(file.getAbsolutePath().replace(".txt", "_temp.txt"));

        try {
            reader = new BufferedReader(new FileReader(this.file));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int currentEmpId = Integer.parseInt(fields[1]);
                if (currentEmpId == address.getEmpId()) {
                    line = String.format("%d,%d,%s,%s,%d",
                            address.getAddressId(),
                            address.getEmpId(),
                            address.getState(),
                            address.getCity(),
                            address.getPincode());
                    logger.info("Address updated for employee ID: {}", address.getEmpId());
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            logger.error("Error updating address file", e);
            throw new AddressFileUpdateException("Error updating address file", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }

        if (!renameTo(tempFile, file)) {
            logger.error("Error replacing the original file with the updated file");
            throw new AddressFileUpdateException("Error replacing the original file with the updated file", 500);
        }

        logger.info("Address for employee ID: {} updated successfully", address.getEmpId());
        return address;
    }

    private boolean renameTo(File source, File destination) throws AddressFileUpdateException {
        logger.info("Renaming temporary file to original file name");
        try {
            if (destination.exists() && !destination.delete()) {
                logger.error("Failed to delete existing file: {}", destination.getAbsolutePath());
                throw new IOException("Failed to delete existing file: " + destination.getAbsolutePath());
            }
            boolean success = source.renameTo(destination);
            if (!success) {
                logger.error("Failed to rename temporary file to: {}", destination.getAbsolutePath());
            }
            return success;
        } catch (IOException e) {
            logger.error("Error deleting original file during update", e);
            throw new AddressFileUpdateException("Error deleting original file during update", 500);
        }
    }
    private int getMaxAddressId() {
        int maxAddressId = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String addressId= String.valueOf(fields[0]);
                if(addressId.isEmpty()){
                    maxAddressId=0;
                    return maxAddressId;
                }
                int currentAddressId = Integer.parseInt(fields[0]);
                maxAddressId = Math.max(maxAddressId, currentAddressId);
            }
        } catch (IOException e) {
            throw new EmployeeFileReadException("Error reading employee details from file", 500);
        }
        return maxAddressId;
    }
    private Address createAddressFromFields(String[] fields) {
        Address address = new Address();
        address.setAddressId(Integer.parseInt(fields[0]));
        address.setEmpId(Integer.parseInt(fields[1]));
        address.setState(fields[2]);
        address.setCity(fields[3]);
        address.setPincode(Integer.parseInt(fields[4]));
        logger.info("Address object created: {}", address);
        return address;
    }
    private String createAddressLine(Address address) {
        return address.getAddressId() + "," +
                address.getEmpId() + "," +
                address.getState() + "," +
                address.getCity() + "," +
                address.getPincode();
    }


    private void closeBufferedReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
                logger.debug("BufferedReader closed successfully");
            } catch (IOException e) {
                logger.error("Failed to close the reader", e);
            }
        }
    }

    private void closeBufferedWriter(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
                logger.debug("BufferedWriter closed successfully");
            } catch (IOException e) {
                logger.error("Failed to close the writer", e);
            }
        }
    }
}
