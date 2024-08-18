package com.wavemaker.employee.repository.Impl;

import com.wavemaker.employee.exception.*;
import com.wavemaker.employee.model.Address;
import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.util.FileCreateUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AddressRepositoryInFileImpl implements AddressRepository {
    private static final String FILE_PATH = "C:\\Users\\roopaa_700059\\IdeaProjects\\Java_Assignments\\address.txt";

    private final File file;

    public AddressRepositoryInFileImpl() {
        this.file = FileCreateUtil.createFileIfNotExists(FILE_PATH);
    }

    @Override
    public Address getAddressByEmpId(int empId) throws AddressFileReadException {
        BufferedReader reader = null;
        Address address = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    continue; // Skip any malformed lines
                }
                int currentEmpId = Integer.parseInt(fields[1]);
                if (currentEmpId == empId) {
                    int addressId = Integer.parseInt(fields[0]);
                    String state = fields[2];
                    String city = fields[3];
                    int pincode = Integer.parseInt(fields[4]);

                    address = new Address();
                    address.setAddressId(addressId);
                    address.setEmpId(currentEmpId);
                    address.setState(state);
                    address.setCity(city);
                    address.setPincode(pincode);

                    return address;
                }
            }
        } catch (IOException e) {
            throw new AddressFileReadException("Error reading address details from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return address;
    }

    @Override
    public List<Address> readAllAddresses() throws AddressFileReadException {
        List<Address> addresses = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    continue; // Skip any malformed lines
                }
                int addressId = Integer.parseInt(fields[0]);
                int empId = Integer.parseInt(fields[1]);
                String state = fields[2];
                String city = fields[3];
                int pincode = Integer.parseInt(fields[4]);

                Address address = new Address();
                address.setAddressId(addressId);
                address.setEmpId(empId);
                address.setState(state);
                address.setCity(city);
                address.setPincode(pincode);

                addresses.add(address);
            }
        } catch (IOException e) {
            throw new AddressFileReadException("Error reading address details from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return addresses;
    }

    @Override
    public boolean addAddress(Address address) throws AddressFileWriteException, DuplicateAddressRecordFoundException {
        if (isAddressExistsForEmpId(address.getEmpId())) {
            throw new DuplicateAddressRecordFoundException("Address for Employee with ID: " + address.getEmpId() + " already exists.", 409);
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.file, true));
            String line = String.format("%d,%d,%s,%s,%d",
                    address.getAddressId(),
                    address.getEmpId(),
                    address.getState(),
                    address.getCity(),
                    address.getPincode());

            writer.write(line);
            writer.newLine();
            writer.flush();
            return true;
        } catch (IOException e) {
            throw new AddressFileWriteException("Error writing address details to file", 500);
        } finally {
            closeBufferedWriter(writer);
        }
    }

    @Override
    public boolean isAddressExistsForEmpId(int empId) throws AddressFileReadException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 5) {
                    continue; // Skip any malformed lines
                }
                int existingEmpId = Integer.parseInt(fields[1]);
                if (existingEmpId == empId) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new AddressFileReadException("Failed to read address data from file", 500);
        } finally {
            closeBufferedReader(reader);
        }
        return false;
    }

    @Override
    public Address deleteAddressByEmpId(int empId) throws AddressFileDeletionException, AddressNotFoundException {
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
                    continue; // Skip any malformed lines
                }
                int currentEmpId = Integer.parseInt(fields[1]);
                if (currentEmpId == empId) {
                    deletedAddress = new Address();
                    deletedAddress.setAddressId(Integer.parseInt(fields[0]));
                    deletedAddress.setEmpId(currentEmpId);
                    deletedAddress.setState(fields[2]);
                    deletedAddress.setCity(fields[3]);
                    deletedAddress.setPincode(Integer.parseInt(fields[4]));
                    continue;
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new AddressFileDeletionException("Error processing address file for deletion", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }
        if (!renameTo(tempFile, file)) {
            throw new AddressFileDeletionException("Error replacing the original file with the updated file after deletion", 500);
        }
        if (deletedAddress == null) {
            throw new AddressNotFoundException("Address for Employee with ID " + empId + " not found", 404);
        }
        return deletedAddress;
    }

    @Override
    public Address updateAddress(Address address) throws AddressFileUpdateException {
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
                }
                writer.write(line + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new AddressFileUpdateException("Error updating address file", 500);
        } finally {
            closeBufferedReader(reader);
            closeBufferedWriter(writer);
        }

        if (!renameTo(tempFile, file)) {
            throw new AddressFileUpdateException("Error replacing the original file with the updated file", 500);
        }

        return address;
    }

    private boolean renameTo(File source, File destination) throws AddressFileUpdateException {
        try {
            if (destination.exists() && !destination.delete()) {
                throw new IOException("Failed to delete existing file: " + destination.getAbsolutePath());
            }
            return source.renameTo(destination);
        } catch (IOException e) {
            throw new AddressFileUpdateException("Error deleting original file during update", 500);
        }
    }

    private void closeBufferedReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Failed to close the reader: " + e.getMessage());
            }
        }
    }

    private void closeBufferedWriter(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to close the writer: " + e.getMessage());
            }
        }
    }
}
