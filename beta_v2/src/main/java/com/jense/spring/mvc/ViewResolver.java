package com.jense.spring.mvc;

import java.io.File;

public class ViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX=".html";
    private File templateRootDir;

    public ViewResolver(String templateRoot) {
            String templateRootPath=this.getClass().getClassLoader().getResource(templateRoot).getFile();
            templateRootDir=new File(templateRootPath);
        }

        public  View resolverView(String viewName){
            if(null==viewName || "".equals(viewName.trim())){
                return null;
            }
            //视图文件名必须需要使用 DEFAULT_TEMPLATE_SUFFIX 后缀
            viewName=viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)?viewName:(viewName+DEFAULT_TEMPLATE_SUFFIX);
        File template=new File((templateRootDir.getPath()+"/"+ viewName).replaceAll("/+","/"));
        return new View(template);
    }
}
