package com.jense.spring.context;

import com.jense.spring.beans.config.BeanDefinition;
import com.jense.spring.beans.support.BeanReader;

import java.util.ArrayList;
import java.util.List;

public class JApplicationContext {
    private List<BeanDefinition> context = new ArrayList<BeanDefinition>() ;
    public JApplicationContext(String... config){
        String scanPageName=config[0];
        new BeanReader(scanPageName);
    }
}
