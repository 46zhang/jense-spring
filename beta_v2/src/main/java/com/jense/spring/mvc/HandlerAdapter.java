package com.jense.spring.mvc;


import com.jense.spring.annotation.JRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HandlerAdapter {

    public ModelAndView handler(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, HandlerMapping handlerMapping) throws Exception {
        //1.先获取形参列表
        //保存形参列表 key:参数名称   value:参数位置
        Map<String, Integer> paramIndexMapping = new HashMap<String, Integer>();
        Method method = handlerMapping.getMethod();
        Annotation[][] pa = method.getParameterAnnotations();

        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof JRequestParam) {
                    String paramName = ((JRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        //2. 特殊化处理HttpServletRequest与HttpServletResponse参数
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            //如果是HttpServletRequest 或者 HttpServletResponse 为特殊情况
            //那么key直接为该class的name
            if (paramTypes[i] == HttpServletRequest.class || paramTypes[i] == HttpServletResponse.class) {
                paramIndexMapping.put(paramTypes[i].getName(), i);
            }
        }

        //3.获取实参列表,赋值给对应形参

        //value为String[]类型是因为value可能为数组，例如
        // http://localhost/query?name=Jense&Tony 那么value就是Tony跟Jense俩个值
        Map<String, String[]> params = httpServletRequest.getParameterMap();

        Object[] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(params.get(param.getKey()))
                    //替换括号 '['  ']'
                    .replaceAll("\\[|\\]", "")
                    //替换掉空格
                    .replaceAll("\\s+", ",");
            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());

            //需要进行类型转换，将String转为目标类型
            try {
                paramValues[index] = castStringValue(value, paramTypes[index]);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //赋值HttpServletRequest给形参
        //其参数由Tomcat进行值传递，不重新new
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = httpServletRequest;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = httpServletResponse;
        }

        //4. 返回 ModelAndView 类型的值
        Object result = method.invoke(handlerMapping.getClass(), paramValues);

        if (result == null || result == Void.class) {
            return null;
        }

        boolean isModelAndView = method.getReturnType()==ModelAndView.class;

        if(isModelAndView){
            return (ModelAndView) result;
        }


        return null;
    }

    private Object castStringValue(String value, Class<?> paramType) throws Exception {
        //暂时先硬编码，后面需要写一个转换器，支持自定义类型
        if (String.class == paramType) {
            return value;
        } else if (Integer.class == paramType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramType) {
            return Double.valueOf(value);
        } else {
            throw new Exception("目前暂不支持该类型");
        }
    }
}
