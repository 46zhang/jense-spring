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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {

    List<HandlerMapping> handlerMappingList = new ArrayList<HandlerMapping>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        JApplicationContext applicationContext = new JApplicationContext(config.getInitParameter("contextConfigLocation"));
        //初始化各种mvc组件
        initStrategies(applicationContext);

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HandlerMapping handlerMapping = getHandlerMapping(req);
        if (handlerMapping == null) {
            resp.getWriter().write("404 not found");
            return;
        }
        // 1.根据handlerMapping 获取一个适配器
        HandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);

        // 2. 从适配器中获取modelAndView
        ModelAndView modelAndView = handlerAdapter.handler(req,resp,handlerMapping);

        // 3.解析modelAndView,结果可能是个页面、字符串或者对象
        processDispatcherResult(req,resp,modelAndView);


    }

    private void processDispatcherResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView modelAndView) {
        if(null==modelAndView){
            return ;
        }

    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handlerMapping) {
        return null;
    }



    private void initStrategies(JApplicationContext context) {
        //初始化url-handler map
        initHandleMapping(context);
        //初始化handler适配器
        initHandlerAdapter(context);
        //初始化视图解析器
        initViewResolvers(context);
    }

    private void initViewResolvers(JApplicationContext context) {

    }

    private void initHandlerAdapter(JApplicationContext context) {

    }


    private void initHandleMapping(JApplicationContext applicationContext) {

        Map<String, BeanWrapper> beanWrapperMap= applicationContext.getBeanWrapperMap();

        for (Map.Entry<String, BeanWrapper> entry : beanWrapperMap.entrySet()) {

            Object instance = entry.getValue().getWrapperInstance();
            Class<?> clazz = instance.getClass();

            if (clazz.isAnnotationPresent(JRequestMapping.class)) {
                //写在类上的url
                String classUrl = clazz.getAnnotation(JRequestMapping.class).value();
                Method[] methods = clazz.getMethods();

                //遍历获取写在方法注解上的url
                for (Method method : methods) {
                    if (method.isAnnotationPresent(JRequestMapping.class)) {
                        //写在方法上的url注解
                        String methodUrl = method.getAnnotation(JRequestMapping.class).value();
                        //需要用正则替换写多了的'/'
                        String url = (classUrl + "/" + methodUrl).replaceAll("//", "/");
                        handlerMappingList.add(new HandlerMapping(Pattern.compile(url), method, instance));
                    }
                }
            }
        }
    }

    private HandlerMapping getHandlerMapping(HttpServletRequest req) {
        //获取url
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        for (HandlerMapping handlerMapping : handlerMappingList) {
            //通过正则判断url的模式是否匹配
            Matcher matcher = handlerMapping.getPattern.matcher(url);
            if (matcher.find()) {
                return handlerMapping;
            }
        }
        return null;
    }

}


