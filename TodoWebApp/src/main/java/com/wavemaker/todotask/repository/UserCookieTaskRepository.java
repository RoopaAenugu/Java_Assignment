package com.wavemaker.todotask.repository;

public interface UserCookieTaskRepository {
    public void addCookie(String cookieValue,int userId);
    public int getUserIdByCookieValue(String cookieValue);
    public void removeCookie(String cookieValue);
}
