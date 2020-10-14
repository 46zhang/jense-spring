package com.jense.spring.beans.support;

import com.jense.spring.annotation.JService;
import com.jense.spring.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 工具类
 * 实现类的扫描
 */
public class BeanReader {
    private List<String> registerBeanNames = new ArrayList<String>();
    private Properties configs = new Properties();
    private String scanPageName = null;

    public BeanReader() {

    }

    public BeanReader(String... config) {

        //1. 获取配置文件需要扫描的包名
        loadConfig(config);
        //2. 扫描包名，获取beanName列表
        //3. 封装成beanDefinition列表
        try {
            doScan(scanPageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfig(String[] config) {
        InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(config[0]);
        try {
            if(inStream==null){
                scanPageName=config[0];
                return;
            }
            configs.load(inStream);
            scanPageName = configs.getProperty("scanPackage");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Properties getConfig() {
        return configs;
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
                registerBeanNames.add((basePackage + "." + name).replaceAll(".class", ""));
            } else {
                //is dir 如果是文件夹就递归处理
                doScan(basePackage + "." + name);
            }
        }
    }

    public List<BeanDefinition> loadBeanDefinition() {
        List<BeanDefinition> beanDefinitionList = new ArrayList<BeanDefinition>();
        for (String registerBeanName : registerBeanNames) {
            try {
                Class<?> clazz = Class.forName(registerBeanName);
                //如果是接口则继续
                if(clazz.isInterface()){
                    continue;
                }
                beanDefinitionList.add(
                        new BeanDefinition(
                                lowerFirstCase(clazz.getSimpleName()), registerBeanName));
                //只对service注解进行接口注入
                if (!clazz.isAnnotationPresent(JService.class)) {
                    continue;
                }
                //接口注入 ,不需要首字符小写
                for (Class<?> interfaceClazz : clazz.getInterfaces()) {
                    beanDefinitionList.add(
                            new BeanDefinition(interfaceClazz.getName(), registerBeanName));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return beanDefinitionList;
    }

    private String lowerFirstCase(String s) {
        char[] charArray = s.toCharArray();
        //lower first case 第一个字母变小写
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

    //for test
    public static void main(String[] args) throws IOException {
        BeanReader beanReader = new BeanReader("com.jense.spring");
        List<BeanDefinition> list = beanReader.loadBeanDefinition();
        for (BeanDefinition s : list) {
            System.out.println("beanName: " + s.getBeanName() + " ||  className: " + s.getBeanFactoryClassName());
        }
    }
}
