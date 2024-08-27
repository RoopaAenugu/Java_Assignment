package com.wavemaker.todotask.service.impl;

import com.wavemaker.todotask.repository.UserCookieTaskRepository;
import com.wavemaker.todotask.repository.impl.UserCookieTaskRepositoryImpl;
import com.wavemaker.todotask.service.UserCookieTaskService;

public class UserCookieTaskServiceImpl implements UserCookieTaskService {
    private final UserCookieTaskRepository userCookieTaskRepository;

    // Constructor to inject UserCookieTaskRepository
    public UserCookieTaskServiceImpl() {
        this.userCookieTaskRepository = new UserCookieTaskRepositoryImpl();
    }

    @Override
    public void addCookie(String cookieValue, int userId) {
        userCookieTaskRepository.addCookie(cookieValue, userId);
    }

    @Override
    public int getUserIdByCookieValue(String cookieValue) {
        return userCookieTaskRepository.getUserIdByCookieValue(cookieValue);
    }

    @Override
    public void removeCookie(String cookieValue) {
         userCookieTaskRepository.removeCookie(cookieValue);

    }
}
