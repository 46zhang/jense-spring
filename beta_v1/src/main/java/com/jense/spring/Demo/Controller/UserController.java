package com.jense.spring.Demo.Controller;

import com.jense.spring.Demo.Service.QueryService;
import com.jense.spring.Demo.Service.LoginService;
import com.jense.spring.StringUtil;
import com.jense.spring.annotation.JAutoWire;
import com.jense.spring.annotation.JController;
import com.jense.spring.annotation.JRequestMapping;
import com.jense.spring.annotation.JRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@JController
@JRequestMapping("/user")
public class UserController {
    @JAutoWire
    public LoginService loginService;
    @JAutoWire
    public QueryService queryService;

    @JRequestMapping("///login")
    public void login(HttpServletRequest req, HttpServletResponse resp){

        String  result =loginService.login();

        try {
            resp.getWriter().write( result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @JRequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp,
                      @JRequestParam("name") String name){
        String result = queryService.query(name);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
