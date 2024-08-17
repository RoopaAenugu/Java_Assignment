package com.wavemaker.employee.employeefactory;

import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.singleton.SingletonAddressRepository;

import java.util.HashMap;
import java.util.Map;

public class AddressRepositoryFactory {
    private static AddressRepository addressRepository;
    private static final Map<Integer, AddressRepository> repositoryMap = new HashMap<>();

    public static AddressRepository getAddressRepositoryInstance(int opinion) {
        if (repositoryMap.containsKey(opinion)) {
            return repositoryMap.get(opinion);
        }
        if (opinion == 1) {
            addressRepository = SingletonAddressRepository.getInMemoryAddressRepositoryInstance();
            repositoryMap.put(opinion, addressRepository);
        } else if (opinion == 2) {
            addressRepository = SingletonAddressRepository.getInFileAddressRepositoryInstance();
            repositoryMap.put(opinion, addressRepository);
        }
        return repositoryMap.get(opinion);
    }
}
