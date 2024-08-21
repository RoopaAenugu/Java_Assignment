package com.wavemaker.employee.exception.employee;

public class EmployeeFileWriteException extends RuntimeException {
    private int statusCode;


    public EmployeeFileWriteException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }


    public EmployeeFileWriteException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
