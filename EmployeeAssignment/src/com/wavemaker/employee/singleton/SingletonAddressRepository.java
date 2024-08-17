package com.wavemaker.employee.singleton;

import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.repository.Impl.AddressRepositoryInFileImpl;
import com.wavemaker.employee.repository.Impl.AddressRepositoryInMemoryImpl;

public class SingletonAddressRepository {

    private static volatile AddressRepository inMemoryRepository;
    private static volatile AddressRepository inFileRepository;

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

}
