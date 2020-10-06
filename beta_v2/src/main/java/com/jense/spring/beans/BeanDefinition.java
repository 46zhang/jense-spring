package com.jense.spring.beans;

/**
 * 抽象化 bean 的定义，通过beanName作为bean的唯一标识
 * 通过beanFactoryClassName 来实例化该beanName对应的类
 */
public class BeanDefinition {
    private String beanName;
    private String beanFactoryClassName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanFactoryClassName() {
        return beanFactoryClassName;
    }

    public void setBeanFactoryClassName(String beanFactoryClassName) {
        this.beanFactoryClassName = beanFactoryClassName;
    }
}
