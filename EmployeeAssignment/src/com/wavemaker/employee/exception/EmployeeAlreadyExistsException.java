package com.wavemaker.employee.exception;

public class EmployeeAlreadyExistsException extends RuntimeException {
    private String message;
    public EmployeeAlreadyExistsException(String message) {
        super(message);
        this.message = message;

    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

