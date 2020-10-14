package com.jense.spring.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class View {
    private File viewFile;

    public View(File templateFile) {
        this.viewFile = templateFile;
    }

    public void render(Map<String,?> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        StringBuffer stringBuffer=new StringBuffer();
        RandomAccessFile randomAccessFile=new RandomAccessFile(this.viewFile,"r");

        String line=null;
        while(null!=(line=randomAccessFile.readLine())){
            //编码
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
            //正则匹配出目标值
            Pattern pattern = Pattern.compile("$\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()){
                String paramName = matcher.group();
                //去掉标识字符  ${}
                paramName = paramName.replaceAll("$\\{|\\}","");
                //替换目标值
                Object paramValue = model.get(paramName);
                //处理特殊字符
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher = pattern.matcher(line);
            }
            stringBuffer.append(line);
        }
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(stringBuffer.toString());

    }

    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
