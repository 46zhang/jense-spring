package com.jense.spring.Demo.Service.impl;

import com.jense.spring.Demo.Service.A;
import com.jense.spring.annotation.JService;

@JService
public class aImpl implements A {

    public void b() {
        System.out.println("bbb");
    }
}
