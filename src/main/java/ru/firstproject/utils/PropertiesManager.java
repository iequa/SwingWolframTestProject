package ru.firstproject.utils;

import java.io.*;
import java.util.Properties;

public class PropertiesManager {

    private static final String CREATE_SAVE_ERROR_TEXT = "can't create/save prop file =(\n check dir settings";

    final private String currentPath;
    Properties properties;
    File propFile;

    public PropertiesManager() {
        currentPath = System.getProperty("user.dir")
                + "%s%s".formatted(System.getProperty("os.name").toLowerCase().contains("windows") ?
                        "\\"
                        :
                        "/"
                ,
                "config.cfg"
        );
        propFile = new File(currentPath);
        properties = new Properties();
        try {
            properties.load(new FileReader(propFile));
        } catch (Exception e) {
            System.out.println("Prop file not found");
            try {
                if (propFile.createNewFile()) {
                    properties.load(new FileReader(propFile));
                }
            } catch (IOException ex) {
                System.out.println(CREATE_SAVE_ERROR_TEXT);
            }
        }
    }

    public boolean savePropertiesToFile() {
        try {
            OutputStream os = new FileOutputStream(propFile);
            properties.store(os, "Paths");
        } catch (Exception ex) {
            System.out.println(CREATE_SAVE_ERROR_TEXT);
            return false;
        }
        return true;
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public String getKernelPath() {
        return properties.getProperty("kernelPath");
    }

    public String getIsPathNeeded() {
        return properties.getProperty("isPathNeeded");
    }

    public PropertiesManager setKernelPath(String value) {
        properties.setProperty("kernelPath", value);
        return this;
    }

    public PropertiesManager setIsPathNeeded(String value) {
        properties.setProperty("isPathNeeded", value);
        return this;
    }
}
