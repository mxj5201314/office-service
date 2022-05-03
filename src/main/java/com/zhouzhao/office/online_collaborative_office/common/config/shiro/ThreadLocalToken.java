package com.zhouzhao.office.online_collaborative_office.common.config.shiro;


import org.springframework.stereotype.Component;

@Component
public class ThreadLocalToken {

    private final ThreadLocal<String> local = new ThreadLocal<>();

    public String getToken() {
        return local.get();
    }

    public void setToken(String token) {
        local.set(token);
    }

    public void clearToken() {
        local.remove();
    }

    

}
