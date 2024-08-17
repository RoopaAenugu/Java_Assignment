package com.wavemaker.employee.singleton;

import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.repository.Impl.EmployeeRepositoryInFileImpl;
import com.wavemaker.employee.repository.Impl.EmployeeRepositoryInMemoryImpl;

public class SingletonEmployeeRepository {
    public static EmployeeRepository  employeeRepository;
    private static volatile EmployeeRepository inMemoryRepository;
    private static volatile EmployeeRepository inFileRepository;

    public static EmployeeRepository getInMemoryEmployeeRepositoryInstance() {
        if (inMemoryRepository == null) {
            synchronized (SingletonEmployeeRepository.class) {
                if (inMemoryRepository == null) {
                    inMemoryRepository = new EmployeeRepositoryInMemoryImpl();
                }
            }
        }
        return inMemoryRepository;
    }

    public static EmployeeRepository getInFileEmployeeRepositoryInstance() {
        if (inFileRepository == null) {
            synchronized (SingletonEmployeeRepository.class) {
                if (inFileRepository == null) {
                    inFileRepository = new EmployeeRepositoryInFileImpl();
                }
            }
        }
        return inFileRepository;
    }

}
