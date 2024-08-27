package com.wavemaker.todotask.service.impl;

import com.wavemaker.todotask.model.UserAuthentication;
import com.wavemaker.todotask.repository.LoginRepository;
import com.wavemaker.todotask.repository.impl.LoginRepositoryImpl;
import com.wavemaker.todotask.service.LoginService;

public class LoginServiceImpl implements LoginService {
    private final LoginRepository loginRepository;
    public LoginServiceImpl(){
        this.loginRepository = new LoginRepositoryImpl();
    }
    @Override
    public int isValidate(UserAuthentication userAuthentication) {
        return loginRepository.isValidate(userAuthentication);
    }

    @Override
    public UserAuthentication addUser(UserAuthentication userAuthentication) {
      return loginRepository.addUser(userAuthentication);
    }
}
