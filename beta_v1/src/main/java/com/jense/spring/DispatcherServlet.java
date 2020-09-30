package com.jense.spring;

import com.jense.spring.annotation.JAutoWire;
import com.jense.spring.annotation.JController;
import com.jense.spring.annotation.JService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class DispatcherServlet extends HttpServlet {

    private HashMap<String, Object> iocMap = new HashMap<String, Object>();

    private HashMap<String, Method> handleMap=new HashMap<String, Method>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        //five step
        String basePackage = config.getInitParameter("scanPackage");
        // 1. scan package
        scanPackage(basePackage);
        // 2. init ioc
        initIocMap();
        // 3. init url-handle map

        // 4. aop

        // 5. DI
    }


    @Override
    public void init() throws ServletException {
        //five step
        String basePackage = "com.jense.spring";
        // 1. scan package
        scanPackage(basePackage);
        // 2. init ioc
        initIocMap();
        // 3. DI
        doAutoWire();
        // 4. aop

        // 5. init url-handle map
        initHandleMap();

    }

    private void scanPackage(String basePackage){
        ClasspathPackageScanner scan = new ClasspathPackageScanner(basePackage);
        try {
            ArrayList<String> arrays= (ArrayList<String>) scan.getFullyQualifiedClassNameList();
            //firstly set object null
            for (String array : arrays) {
                iocMap.put(array,null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initIocMap(){

        for (String s : iocMap.keySet()) {
            try {
                Class<?> clazz = Class.forName(s);
                if(clazz.isAnnotationPresent(JController.class)){
                    iocMap.put(s,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(JService.class)){
                    JService service = clazz.getAnnotation(JService.class);

                    //get the annotation name
                    String beanName = service.value();

                    if("".equals(beanName)){beanName = clazz.getName();}
                    Object instance = clazz.newInstance();
                    iocMap.put(beanName,instance);
                    //set service bean to serviceImpl instance
                    for (Class<?> i : clazz.getInterfaces()) {
                        iocMap.put(i.getName(),instance);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void doAutoWire(){
        for (String s : iocMap.keySet()) {
            try{
                //获取public属性
                Field[] fields = iocMap.get(s).getClass().getDeclaredFields();
                for (Field field : fields) {
                    if(field.isAnnotationPresent(JAutoWire.class)){
                        JAutoWire autowired = field.getAnnotation(JAutoWire.class);

                        //如果用户没有自定义beanName，默认就根据类型注入
                        //这个地方省去了对类名首字母小写的情况的判断，这个作为课后作业
                        //小伙伴们自己去完善
                        String beanName = autowired.value().trim();
                        if("".equals(beanName)){
                            //获得接口的类型，作为key待会拿这个key到ioc容器中去取值
                            beanName = field.getType().getName();
                        }

                        //如果是public以外的修饰符，只要加了@Autowired注解，都要强制赋值
                        //反射中叫做暴力访问， 强吻
                        field.setAccessible(true);

                        try {
                            //用反射机制，动态给字段赋值
                            field.set(iocMap.get(s),iocMap.get(beanName));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initHandleMap(){
        
    }
}
