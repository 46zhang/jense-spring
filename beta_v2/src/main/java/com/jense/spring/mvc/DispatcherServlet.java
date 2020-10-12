package com.jense.spring.mvc;

import com.jense.spring.annotation.JRequestMapping;
import com.jense.spring.beans.BeanWrapper;
import com.jense.spring.context.JApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {

    List<HandlerMapping> handlerMappingList=new ArrayList<HandlerMapping>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");
        
        if(findUrlInHandlerMappingList(url)==null){
            resp.getWriter().write("404 not found");
            return;
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        JApplicationContext applicationContext = new JApplicationContext(config.getInitParameter("contextConfigLocation"));
        initHandleMapping(applicationContext.getBeanWrapperMap());

    }


    private void initHandleMapping(Map<String, BeanWrapper> beanWrapperMap) {
        for (Map.Entry<String, BeanWrapper> entry : beanWrapperMap.entrySet()) {
            Object instance= entry.getValue().getWrapperInstance();
            Class<?> clazz = instance.getClass();
            if(clazz.isAnnotationPresent(JRequestMapping.class)){
                //写在类上的url
                String classUrl=clazz.getAnnotation(JRequestMapping.class).value();
                Method[] methods= clazz.getMethods();
                for(Method method:methods){
                    if(method.isAnnotationPresent(JRequestMapping.class)){
                        //写在方法上的url
                        String methodUrl=method.getAnnotation(JRequestMapping.class).value();
                        String url= (classUrl+"/"+methodUrl).replaceAll("//","/");
                        handlerMappingList.add(new HandlerMapping( Pattern.compile(url),method,instance));
                    }
                }
            }

        }
    }

    private HandlerMapping findUrlInHandlerMappingList(String url){
        for(HandlerMapping handlerMapping: handlerMappingList){
            Matcher matcher=handlerMapping.getPattern.matcher(url);
            if(matcher.find()){
                return handlerMapping;
            }
        }
        return null;
    }

}
