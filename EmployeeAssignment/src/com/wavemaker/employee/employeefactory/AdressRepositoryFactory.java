package com.wavemaker.employee.employeefactory;

import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.singleton.SingletonEmployeeRepository;

import java.util.HashMap;
import java.util.Map;

public class AdressRepositoryFactory {
    private static AddressRepository addressRepository;
    private static final Map<Integer, AddressRepository> repositoryMap = new HashMap<>();

    public static AddressRepository getEmployeeRepositoryInstance(int opinion) {
        if(repositoryMap.containsKey(opinion)) {
            return repositoryMap.get(opinion);
        }
        if (opinion == 1) {
            addressRepository = SingletonEmployeeRepository.getInMemoryEmployeeRepositoryInstance();
            repositoryMap.put(opinion,addressRepository);
        } else if (opinion == 2) {
            addressRepository= SingletonEmployeeRepository.getInFileEmployeeRepositoryInstance();
            repositoryMap.put(opinion, addressRepository);
        }
        return repositoryMap.get(opinion);
    }
}
