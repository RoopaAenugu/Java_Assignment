-- Create the LEAVEMANAGEMENT database
CREATE DATABASE LEAVEMANAGEMENT;

-- Use the LEAVEMANAGEMENT database
USE LEAVEMANAGEMENT;

-- Create the EMPLOYEES table
CREATE TABLE EMPLOYEES (
    EMPLOYEE_ID INT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(100) NOT NULL,
    EMAIL VARCHAR(100) UNIQUE NOT NULL,
    MANAGER_ID INT,
    DATE_OF_BIRTH DATE,
    PHONE_NUMBER INT,
    FOREIGN KEY (MANAGER_ID) REFERENCES EMPLOYEES(EMPLOYEE_ID)
);

-- Create the LOGIN_CREDENTIALS table
CREATE TABLE LOGIN_CREDENTIALS(
    LOGIN_ID INT PRIMARY KEY AUTO_INCREMENT,
    EMAILID VARCHAR(100) UNIQUE NOT NULL,
    EMPLOYEE_ID INT,
    PASSWORD VARCHAR(255) NOT NULL,
    FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEES(EMPLOYEE_ID)
);

-- Create the LEAVE_TYPES table
CREATE TABLE LEAVE_TYPES (
    LEAVE_TYPE_ID INT PRIMARY KEY AUTO_INCREMENT,
    TYPE_NAME VARCHAR(100) NOT NULL,
    LIMIT_FOR_LEAVES INT NOT NULL,
    GENDER ENUM('MALE', 'FEMALE', 'BOTH') DEFAULT 'BOTH',
    DESCRIPTION VARCHAR(500)
);

-- Create the LEAVE_REQUEST table
CREATE TABLE LEAVE_REQUEST (
    LEAVE_ID INT PRIMARY KEY AUTO_INCREMENT,
    EMPLOYEE_ID INT,
    LEAVE_TYPE_ID INT NOT NULL,
    FROM_DATE DATE NOT NULL,
    TO_DATE DATE NOT NULL,
    REASON VARCHAR(255) NOT NULL,
    STATUS ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    MANAGER_ID INT,
    COMMENTS VARCHAR(255),
    DATE_OF_APPLICATION DATE,
    FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEES(EMPLOYEE_ID),
    FOREIGN KEY (MANAGER_ID) REFERENCES EMPLOYEES(EMPLOYEE_ID),
    FOREIGN KEY (LEAVE_TYPE_ID) REFERENCES LEAVE_TYPES(LEAVE_TYPE_ID)
);


-- Create the COOKIE table
CREATE TABLE COOKIE (
    COOKIE_ID INT AUTO_INCREMENT PRIMARY KEY,
    COOKIE_NAME VARCHAR(255) NOT NULL,
    COOKIE_VALUE VARCHAR(255) NOT NULL,
    LOGIN_ID INT,
    EXPIRY_DATE DATE,
    FOREIGN KEY (LOGIN_ID) REFERENCES LOGIN_CREDENTIALS(LOGIN_ID)
);

-- Create the EMPLOYEE_LEAVE_SUMMARY table
CREATE TABLE EMPLOYEE_LEAVE_SUMMARY (
    SUMMARY_ID INT AUTO_INCREMENT PRIMARY KEY,
    EMPLOYEE_ID INT NOT NULL,
    LEAVE_TYPE_ID INT NOT NULL,
    LEAVE_TYPE VARCHAR(255) NOT NULL,
    PENDING_LEAVES INT DEFAULT 0,
    TOTAL_LEAVES_TAKEN INT DEFAULT 0,
    FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEES(EMPLOYEE_ID),
    FOREIGN KEY (LEAVE_TYPE_ID) REFERENCES LEAVE_TYPES(LEAVE_TYPE_ID)
);
