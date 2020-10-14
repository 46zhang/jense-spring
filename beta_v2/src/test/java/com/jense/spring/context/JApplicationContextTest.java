package com.jense.spring.context;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class JApplicationContextTest extends TestCase {
    //测试循环依赖
    @Test
    public void testAutoWire(){

        JApplicationContext jApplicationContext = new JApplicationContext("com.jense.spring");
        Assert.fail();

    }
}