package com.jense.spring.beans;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BeanReader {
    public BeanReader(){

    }
    public BeanReader(String packageName, List<String> nameList) {
        try {
            doScan(packageName, nameList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
        String splashPath = basePackage.replaceAll("\\.", "/");
        URL url = getClass().getClassLoader().getResource(splashPath);
        String filePath = url.getPath();
        File file = new File(filePath);
        String[] names = file.list();
        for(String name: names){
            // only get the class file
            if(name.endsWith(".class")){
                nameList.add(basePackage+"."+name);
            }else{
                //is dir
                doScan(basePackage+"."+name,nameList);
            }
        }
        return nameList;
    }

    //for test
    public static void main(String[] args) throws IOException {
        BeanReader beanReader=new BeanReader();
        List<String> list=new ArrayList<String>();
        beanReader.doScan("com.jense.spring",list);
        for (String s : list) {
            System.out.println(s);
        }
    }
}
