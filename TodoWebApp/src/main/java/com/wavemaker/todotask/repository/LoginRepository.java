package com.wavemaker.todotask.repository;

import com.wavemaker.todotask.model.UserAuthentication;

public interface LoginRepository {
    public int isValidate(UserAuthentication userAuthentication);
}
