package com.jense.spring.demo;

public interface QueryService {
    String add(String name);

    String delete(String name);

    String query(Integer id);

    String modify(String name,Integer id);
}
