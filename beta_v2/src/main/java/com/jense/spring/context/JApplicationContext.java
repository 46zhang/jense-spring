package com.jense.spring.context;

import com.jense.spring.annotation.JAutoWire;
import com.jense.spring.annotation.JController;
import com.jense.spring.annotation.JService;
import com.jense.spring.beans.BeanWrapper;
import com.jense.spring.beans.config.BeanDefinition;
import com.jense.spring.beans.support.BeanReader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JApplicationContext {
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();
    private Map<String, BeanWrapper> beanWrapperMap = new HashMap<String, BeanWrapper>();

    public Map<String, BeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }

    public Map<String, BeanWrapper> getBeanWrapperMap() {
        return beanWrapperMap;
    }

    public JApplicationContext(String... config) {

        //1. 通过委派模式扫描包名，获取 beanDefinition 列表
        //2. 将 beanDefinition 进行缓存到map
        //3. 实例化 beanDefinition ，依赖注入实例，封装成 beanWrapper
        String scanPageName = config[0];
        BeanReader beanReader = new BeanReader(scanPageName);
        List<BeanDefinition> context = new ArrayList<BeanDefinition>();
        context = beanReader.loadBeanDefinition();
        //把bean加入到map容器缓存起来
        try {
            doRegisterBean(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doRegisterBean(List<BeanDefinition> context) throws Exception {
        //把bean加入到map容器缓存起来
        for (BeanDefinition beanDefinition : context) {
            if (beanWrapperMap.containsKey(beanDefinition)) {
                throw new Exception("bean名称有冲突");
            }
            beanDefinitionMap.put(beanDefinition.getBeanFactoryClassName(), beanDefinition);
        }
    }

    public Object getBean(String beanName) {

        if (beanWrapperMap.containsKey(beanName)) {
            return beanWrapperMap.get(beanName).getWrapperInstance();
        }
        BeanWrapper beanWrapper = null;
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        try {
            //利用反射实例化类，将对象封装成beanWrapper
            Class<?> clazz = Class.forName(beanDefinition.getBeanFactoryClassName());
            Object object = clazz.newInstance();
            beanWrapper = new BeanWrapper(beanName, object);
            //保存到容器
            this.beanWrapperMap.put(beanName, beanWrapper);
            //依赖注入
            doAutoWire(beanName, beanDefinition, beanWrapper);

        } catch (Exception e) {
            e.printStackTrace();
        }
        assert beanWrapper != null;
        return beanWrapper.getWrapperInstance();
    }

    private void doAutoWire(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrapperInstance();
        //只对带有service注解跟controller注解的类进行依赖注入
        if (!instance.getClass().isAnnotationPresent(JService.class) &&
                !instance.getClass().isAnnotationPresent(JController.class)) {
            return;
        }
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            //将autowire的内容进行注入
            if (field.isAnnotationPresent(JAutoWire.class)) {
                //获取注解的值，如果注解有加上beanName就用注解的名字
                JAutoWire autowired = field.getAnnotation(JAutoWire.class);
                String name = autowired.value().trim();
                if (name.equals("")) {
                    //否则默认取类型的值
                    name = field.getType().getName();
                }
                try {
                    //依赖注入
                    field.set(instance, this.beanWrapperMap.get(name).getWrapperInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
