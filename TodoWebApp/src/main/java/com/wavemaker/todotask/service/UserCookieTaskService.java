package com.wavemaker.todotask.service;

public interface UserCookieTaskService {
    public void addCookie(String cookieValue,int userId);
    public int getUserIdByCookieValue(String cookieValue);
}
