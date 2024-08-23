package com.wavemaker.employee.singleton;

import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.repository.Impl.AddressRepositoryInDbImpl;
import com.wavemaker.employee.repository.Impl.AddressRepositoryInFileImpl;
import com.wavemaker.employee.repository.Impl.AddressRepositoryInMemoryImpl;
import com.wavemaker.employee.repository.Impl.EmployeeRepositoryInDbImpl;

public class SingletonAddressRepository {

    private static volatile AddressRepository inMemoryRepository;
    private static volatile AddressRepository inFileRepository;
    private static volatile AddressRepository inDbRepository;

    public static AddressRepository getInMemoryAddressRepositoryInstance() {
        if (inMemoryRepository == null) {
            synchronized (SingletonAddressRepository.class) {
                if (inMemoryRepository == null) {
                    inMemoryRepository = new AddressRepositoryInMemoryImpl();
                }
            }
        }
        return inMemoryRepository;
    }

    public static AddressRepository getInFileAddressRepositoryInstance() {
        if (inFileRepository == null) {
            synchronized (SingletonAddressRepository.class) {
                if (inFileRepository == null) {
                    inFileRepository = new AddressRepositoryInFileImpl();
                }
            }
        }
        return inFileRepository;
    }

    public static AddressRepository getInDbAddressepositoryInstance() {
        if (inDbRepository == null) {
            synchronized (SingletonEmployeeRepository.class) {
                if (inDbRepository == null) {
                    inDbRepository = new AddressRepositoryInDbImpl();
                }
            }
        }
        return inDbRepository;

    }
}
