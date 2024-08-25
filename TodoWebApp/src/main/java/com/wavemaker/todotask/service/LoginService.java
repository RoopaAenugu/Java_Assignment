package com.wavemaker.todotask.service;

import com.wavemaker.todotask.model.UserAuthentication;

public interface LoginService {
    public int isValidate(UserAuthentication userAuthentication);
}
