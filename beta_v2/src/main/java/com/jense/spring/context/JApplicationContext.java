package com.jense.spring.context;

import com.jense.spring.beans.BeanDefinition;
import com.jense.spring.beans.BeanReader;

import java.util.ArrayList;
import java.util.List;

public class JApplicationContext {
    private List<BeanDefinition> context = new ArrayList<BeanDefinition>() ;

    public void init(String...config){
        String scanPageName=config[0];
        List<String> nameList=new ArrayList<String>();
        new BeanReader(scanPageName, nameList);

    }
}
