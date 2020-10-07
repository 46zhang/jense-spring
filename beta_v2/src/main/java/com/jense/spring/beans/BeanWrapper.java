package com.jense.spring.beans;

/**
 * bean的修饰类
 */
public class BeanWrapper {
    private String beanName;
    private Object wrapperInstance;

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
}
