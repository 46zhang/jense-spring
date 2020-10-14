package com.jense.spring.context.demo.impl;

import com.jense.spring.annotation.JAutoWire;
import com.jense.spring.annotation.JService;
import com.jense.spring.context.demo.AService;
import com.jense.spring.context.demo.BService;
import com.jense.spring.context.demo.impl.AServiceImpl;


//循环依赖测试
@JService
public class BServiceImpl implements BService {
    @JAutoWire
    AService aService;

    @Override
    public String hello() {
        return aService.hello();
    }
}
