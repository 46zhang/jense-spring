package com.jense.spring;

import org.junit.Test;

import javax.servlet.ServletException;

import static org.junit.Assert.*;

public class DispatcherServletTest {

    @Test
    public void init() throws ServletException {
        DispatcherServlet dispatcherServlet=new DispatcherServlet();
        dispatcherServlet.init();
    }
}