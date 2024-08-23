package com.wavemaker.employee.employeefactory;

import com.wavemaker.employee.repository.AddressRepository;
import com.wavemaker.employee.singleton.SingletonAddressRepository;
import com.wavemaker.employee.singleton.SingletonEmployeeRepository;

import java.util.HashMap;
import java.util.Map;

public class AddressRepositoryFactory {
    private static final Map<Integer, AddressRepository> repositoryMap = new HashMap<>();

    public static AddressRepository getAddressRepositoryInstance(int opinion) {
        if (repositoryMap.containsKey(opinion)) {
            return repositoryMap.get(opinion);
        }
        AddressRepository addressRepository = null;
        if (opinion == 1) {
            addressRepository = SingletonAddressRepository.getInMemoryAddressRepositoryInstance();
            repositoryMap.put(opinion, addressRepository);
        } else if (opinion == 2) {
            addressRepository = SingletonAddressRepository.getInFileAddressRepositoryInstance();
            repositoryMap.put(opinion, addressRepository);
        } else if (opinion == 3) {   //opinion 3 for In Database Storage
            addressRepository = SingletonAddressRepository.getInDbAddressepositoryInstance();
        }

        return addressRepository;
    }
}
