package com.wavemaker.employee.service;

import com.wavemaker.employee.model.UserAuthentication;

public interface UserAuthenticationService {
    public  boolean getUserByUsername(UserAuthentication userAuthentication);
}
