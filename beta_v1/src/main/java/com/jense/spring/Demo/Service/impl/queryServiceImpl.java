package com.jense.spring.Demo.Service.impl;

import com.jense.spring.Demo.Service.QueryService;
import com.jense.spring.annotation.JService;

@JService
public class queryServiceImpl implements QueryService {

    public String query(String name) {
        return "this is jense spring service "+ "welcome "+name;
    }
}
