package com.jense.spring.Demo.Controller;

import com.jense.spring.Demo.Service.A;
import com.jense.spring.Demo.Service.LoginService;
import com.jense.spring.annotation.JAutoWire;
import com.jense.spring.annotation.JController;
import com.jense.spring.annotation.JRequestMapping;

@JController
@JRequestMapping("/")
public class UserController {
    @JAutoWire
    public LoginService loginService;
    @JAutoWire
    public A a;

    @JRequestMapping("/login")
    public void login(){
        loginService.login();
    }

    @JRequestMapping("/a")
    public void a(){
        a.b();
    }
}
