package com.jense.spring.demo.impl;

import com.jense.spring.annotation.JService;
import com.jense.spring.demo.QueryService;

@JService
public class QueryServiceImpl implements QueryService {
    private Integer id = 46;
    private String name = "jense";

    @Override
    public String toString() {
        return "QueryServiceImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public String add(String name) {
        return "add " + name + " success";
    }

    @Override
    public String delete(String name) {
        return "delete " + name + " success";
    }

    @Override
    public String query(Integer id) {
        return " id: " + id;
    }

    @Override
    public String modify(String name, Integer id) {
        return "modify name: " + name + " id: " + id + " success";
    }
}
