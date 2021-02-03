package com.so.ddns;

import java.util.ResourceBundle;

public class PropertiesUtil {
    private static final ResourceBundle resourceBundle;

    static{
        resourceBundle = ResourceBundle.getBundle("app");
    }

    public static String getKey(String key){
        return resourceBundle.getString(key);
    }
}
