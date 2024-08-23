package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.model.UserAuthentication;
import com.wavemaker.employee.repository.Impl.UserAuthenticationRepositoryImpl;
import com.wavemaker.employee.repository.UserAuthenticationRepository;
import com.wavemaker.employee.service.UserAuthenticationService;

public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    private final UserAuthenticationRepository userAuthenticationRepository;

    public UserAuthenticationServiceImpl() {
        userAuthenticationRepository = new UserAuthenticationRepositoryImpl();
    }

    @Override
    public boolean getUserByUsername(UserAuthentication userAuthentication) {
       return userAuthenticationRepository.getUserByUsername(userAuthentication);
    }
}

