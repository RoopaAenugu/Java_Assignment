package com.wavemaker.employee.exception.address;

public class AddressNotFoundException extends RuntimeException {
    private int statusCode;
    private String errorMessage;

    public  AddressNotFoundException(String errorMessage, int statusCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
