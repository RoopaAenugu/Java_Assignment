package com.wavemaker.employee.employeefactory;

import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.singleton.SingletonEmployeeRepository;

import java.util.HashMap;
import java.util.Map;

public class EmployeeRepositoryFactory {
    private static EmployeeRepository employeeRepository;
    private static final Map<Integer, EmployeeRepository> repositoryMap = new HashMap<>();

    public static EmployeeRepository getEmployeeRepositoryInstance(int opinion) {
        if(repositoryMap.containsKey(opinion)) {
            return repositoryMap.get(opinion);
        }
        if (opinion == 1) {
            employeeRepository = SingletonEmployeeRepository.getInMemoryEmployeeRepositoryInstance();
            repositoryMap.put(opinion, employeeRepository);
        } else if (opinion == 2) {
            employeeRepository = SingletonEmployeeRepository.getInFileEmployeeRepositoryInstance();
            repositoryMap.put(opinion, employeeRepository);
        }
        return repositoryMap.get(opinion);
    }
}
