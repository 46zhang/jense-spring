package com.jense.spring.context;

import com.jense.spring.beans.BeanWrapper;
import com.jense.spring.beans.config.BeanDefinition;
import com.jense.spring.beans.support.BeanReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JApplicationContext {
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();
    private Map<String, BeanWrapper> beanWrapperMap = new HashMap<String, BeanWrapper>();

    public JApplicationContext(String... config) throws Exception {
        String scanPageName = config[0];
        BeanReader beanReader = new BeanReader(scanPageName);
        List<BeanDefinition> context = new ArrayList<BeanDefinition>();
        context = beanReader.loadBeanDefinition();
        //把bean加入到map容器缓存起来
        for (BeanDefinition beanDefinition : context) {
            if(beanWrapperMap.containsKey(beanDefinition)){
                throw  new Exception("bean名称有冲突");
            }
            beanDefinitionMap.put(beanDefinition.getBeanFactoryClassName(), beanDefinition);
        }

        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            beanWrapperMap.put(beanName, getBean(beanName, beanDefinitionMap.get(beanName)));
        }
    }

    public BeanWrapper getBean(String beanName, BeanDefinition beanDefinition) {
        if (beanWrapperMap.containsKey(beanName)) {
            return beanWrapperMap.get(beanName);
        }
        return new BeanWrapper(beanName, beanDefinition);
    }
}
