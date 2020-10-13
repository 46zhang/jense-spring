package com.jense.spring.mvc;

import java.io.File;

public class View {
    private File viewFile;

    public View(File templateFile) {
        this.viewFile = templateFile;
    }
}
