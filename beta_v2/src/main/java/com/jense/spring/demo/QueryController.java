package com.jense.spring.demo;

import com.jense.spring.annotation.JAutoWire;
import com.jense.spring.annotation.JController;
import com.jense.spring.annotation.JRequestMapping;
import com.jense.spring.annotation.JRequestParam;
import com.jense.spring.mvc.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JController
@JRequestMapping("/user")
public class QueryController {
    private @JAutoWire
    QueryService queryService;

    @JRequestMapping("add*")
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response,
                            @JRequestParam("name") String name) {
        String result = queryService.add(name);
        return out(response,result);
    }

    @JRequestMapping("query.html")
    public ModelAndView query(HttpServletRequest request, HttpServletResponse response,
                              @JRequestParam("id") String id)
    {
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("user", "Jense");
        model.put("id", id);
        model.put("token", "123456");
        return new ModelAndView("query.html",model);
    }

    @JRequestMapping("queryPage.html")
    public ModelAndView queryHtml(HttpServletRequest request, HttpServletResponse response)
    {
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("user", "Jense");
        model.put("id", 100);
        model.put("token", "123456");
        return new ModelAndView("query.html",model);
    }


    private ModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
