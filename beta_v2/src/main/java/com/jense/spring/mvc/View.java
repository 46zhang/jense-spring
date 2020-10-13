package com.jense.spring.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
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
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
        }

    }
}
