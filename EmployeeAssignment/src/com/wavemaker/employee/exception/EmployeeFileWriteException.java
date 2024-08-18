package com.wavemaker.employee.exception;

public class EmployeeFileWriteException extends RuntimeException {
    private int statusCode;

    // Constructor that takes a message and a status code
    public EmployeeFileWriteException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    // Constructor that takes a message, a cause, and a status code
    public EmployeeFileWriteException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
