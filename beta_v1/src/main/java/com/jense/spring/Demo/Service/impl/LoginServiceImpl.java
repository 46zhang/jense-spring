package com.jense.spring.Demo.Service.impl;

import com.jense.spring.Demo.Service.LoginService;
import com.jense.spring.annotation.JService;

@JService
public class LoginServiceImpl implements LoginService {

    public String login() {
        return "login success";
    }
}
