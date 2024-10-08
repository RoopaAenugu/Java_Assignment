package com.wavemaker.employee.employeefactory;

import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.singleton.SingletonEmployeeRepository;

import java.util.HashMap;
import java.util.Map;

public class EmployeeRepositoryFactory {
    private static final Map<Integer, EmployeeRepository> repositoryMap = new HashMap<>();

    public static EmployeeRepository getEmployeeRepositoryInstance(int opinion) {
        if (repositoryMap.containsKey(opinion)) {
            return repositoryMap.get(opinion);
        }
        EmployeeRepository employeeRepository = null;
        if (opinion == 1) {//opinion 1 is for In memory storage
            employeeRepository = SingletonEmployeeRepository.getInMemoryEmployeeRepositoryInstance();
            repositoryMap.put(opinion, employeeRepository);
        } else if (opinion == 2) {//opinion 2 for In file storage
            employeeRepository = SingletonEmployeeRepository.getInFileEmployeeRepositoryInstance();
            repositoryMap.put(opinion, employeeRepository);
        } else if (opinion == 3) {   //opinion 3 for In Database Storage
            employeeRepository = SingletonEmployeeRepository.getInDbEmployeeRepositoryInstance();

        }

        return employeeRepository;
    }
}
