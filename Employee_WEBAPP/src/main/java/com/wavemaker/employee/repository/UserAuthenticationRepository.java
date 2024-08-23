package com.wavemaker.employee.repository;

import com.wavemaker.employee.model.UserAuthentication;

public interface UserAuthenticationRepository
{

    public boolean getUserByUsername(UserAuthentication userAuthentication);
}
