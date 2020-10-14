package com.jense.spring.context.demo.impl;

import com.jense.spring.annotation.JAutoWire;
import com.jense.spring.annotation.JService;
import com.jense.spring.context.demo.AService;
import com.jense.spring.context.demo.BService;

@JService
public class AServiceImpl implements AService {
    @JAutoWire
    BService bService;
    @Override
    public String hello() {
        return bService.hello();
    }
}
