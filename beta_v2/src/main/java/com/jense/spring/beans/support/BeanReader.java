package com.jense.spring.beans.support;

import com.jense.spring.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeanReader {
    private List<String> registerBeanNames = new ArrayList<String>();

    public BeanReader() {

    }

    public BeanReader(String packageName) {
        try {
            doScan(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doScan(String basePackage) throws IOException {
        String splashPath = basePackage.replaceAll("\\.", "/");
        URL url = getClass().getClassLoader().getResource(splashPath);
        String filePath = url.getPath();
        File file = new File(filePath);
        String[] names = file.list();
        for (String name : names) {
            // only get the class file
            if (name.endsWith(".class")) {
                registerBeanNames.add((basePackage + "." + name).replaceAll(".class",""));
            } else {
                //is dir 如果是文件夹就递归处理
                doScan(basePackage + "." + name);
            }
        }
    }

    public List<BeanDefinition> loadBeanDefinition() {
        List<BeanDefinition> beanDefinitionList=new ArrayList<BeanDefinition>();
        for (String registerBeanName : registerBeanNames) {
            try {
                Class<?> clazz = Class.forName(registerBeanName);
                beanDefinitionList.add(
                        new BeanDefinition(
                                lowerFirstCase(clazz.getSimpleName()),registerBeanName));
                //接口注入
                for(Class<?> interfaceClazz :clazz.getInterfaces()){
                    beanDefinitionList.add(
                            new BeanDefinition(
                                    lowerFirstCase(interfaceClazz.getSimpleName()),registerBeanName));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return beanDefinitionList;
    }

    private String lowerFirstCase(String s){
        char[] charArray =s.toCharArray();
        //lower first case 第一个字母变小写
        charArray[0]+=32;
        return String.valueOf(charArray);
    }

    //for test
    public static void main(String[] args) throws IOException {
        BeanReader beanReader = new BeanReader("com.jense.spring");
        List<BeanDefinition> list = beanReader.loadBeanDefinition();
        for (BeanDefinition s : list) {
            System.out.println("beanName: "+s.getBeanName()+" ||  className: "+s.getBeanFactoryClassName());
        }
    }
}
