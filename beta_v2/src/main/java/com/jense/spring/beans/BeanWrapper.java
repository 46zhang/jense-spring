package com.jense.spring.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * bean的修饰类
 */
public class BeanWrapper {
    private String beanName;
    private Object wrapperInstance;
    //二级缓存，用来判断是否存在循环依赖
    private Set<Object> autoWireNameList=new HashSet<Object>();

    public BeanWrapper(String beanName,Object wrapperInstance){
        this.beanName=beanName;
        this.wrapperInstance=wrapperInstance;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public void setWrapperInstance(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Set<Object> getAutoWireNameList() {
        return autoWireNameList;
    }

    public void setAutoWireNameList(Set<Object> autoWireNameList) {
        this.autoWireNameList = autoWireNameList;
    }
}
