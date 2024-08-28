package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.model.LoginCredential;
import com.wavemaker.leavemanagement.repository.LoginCredentialRepository;
import com.wavemaker.leavemanagement.repository.impl.LoginCredentialRepositoryImpl;
import com.wavemaker.leavemanagement.service.LoginCredentialService;

public class LoginCredentialServiceImpl implements LoginCredentialService {
    private final LoginCredentialRepository loginCredentialRepository;
    public LoginCredentialServiceImpl(){
        this.loginCredentialRepository = new LoginCredentialRepositoryImpl();
    }
    @Override
    public int isValidate(LoginCredential loginCredential) {
        return loginCredentialRepository.isValidate(loginCredential);

    }

    @Override
    public LoginCredential addEmployeeLogin(LoginCredential loginCredential) {
        return loginCredentialRepository.addEmployeeLogin(loginCredential);
    }
}
