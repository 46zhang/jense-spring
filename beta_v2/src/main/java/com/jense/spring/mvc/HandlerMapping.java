package com.jense.spring.mvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * url与Method的映射类
 */
public class HandlerMapping {
    public Pattern getPattern;
    //不直接采用string是因为url可以支持正则 例如 /query*
    private Pattern pattern;
    private Method method;
    //method对应的类
    private Object controller;

    public HandlerMapping(Pattern pattern, Method method, Object controller) {
        this.pattern = pattern;
        this.method = method;
        this.controller = controller;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }
}
