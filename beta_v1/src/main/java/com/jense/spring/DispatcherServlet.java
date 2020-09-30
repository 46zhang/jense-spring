package com.jense.spring;

import com.jense.spring.annotation.*;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    private Properties contextConfig = new Properties();

    private HashMap<String, Object> iocMap = new HashMap<String, Object>();

    private HashMap<String, Method> handleMap=new HashMap<String, Method>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        if(!this.handleMap.containsKey(url)){
            resp.getWriter().write("404 Not Found!!!");
            return;
        }

        Map<String,String[]> params = req.getParameterMap();

        Method method = this.handleMap.get(url);


        //获取形参列表
        Class<?> [] parameterTypes = method.getParameterTypes();
        Object [] paramValues = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class paramterType = parameterTypes[i];
            if(paramterType == HttpServletRequest.class){
                paramValues[i] = req;
            }else if(paramterType == HttpServletResponse.class){
                paramValues[i] = resp;
            }else if(paramterType == String.class){

                Annotation[] [] pa = method.getParameterAnnotations();
                for (int j = 0; j < pa.length ; j ++) {
                    for(Annotation a : pa[i]){
                        if(a instanceof JRequestParam){
                            String paramName = ((JRequestParam) a).value();
                            if(!"".equals(paramName.trim())){
                                String value = Arrays.toString(params.get(paramName))
                                        .replaceAll("\\[|\\]","")
                                        .replaceAll("\\s+",",");
                                paramValues[i] = value;
                            }
                        }
                    }
                }

            }
        }
        //暂时硬编码
        String beanName = (method.getDeclaringClass().getName());
        //赋值实参列表
        method.invoke(iocMap.get(beanName),paramValues);

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //five step
        String basePackage = contextConfig.getProperty("scanPackage");
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

                        String beanName = autowired.value().trim();
                        if("".equals(beanName)){
                            //获得接口的类型，作为key待会拿这个key到ioc容器中去取值
                            beanName = field.getType().getName();
                        }

                        //if filed use @Autowired，but is not public
                        //should access
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
        if(iocMap.isEmpty()){
            return ;
        }
        for (String s : iocMap.keySet()) {
            String baseUrl="";
            try{
                Class<?> clazz=iocMap.get(s).getClass();

                if(clazz.isAnnotationPresent(JController.class)){
                    JRequestMapping requestMapping = clazz.getAnnotation(JRequestMapping.class);
                    baseUrl = requestMapping.value();
                    //only find the public method
                    for(Method m:clazz.getMethods()){
                        if(!m.isAnnotationPresent(JRequestMapping.class)){
                            continue;
                        }
                        JRequestMapping methodRequestMapping=m.getAnnotation(JRequestMapping.class);
                        String url=(baseUrl +"/"+ methodRequestMapping.value()).replaceAll("/+","/");
                        handleMap.put(url,m);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
