package com.shivam.wifidirectfinal.utils;

/**
 * Created by Shivam Sharma on 11-09-2019.
 */
public class AppUtil {

    private static AppUtil instance = null;
    private static final String TAG = "AppUtil";

    public static AppUtil getInstance() {
        if (instance == null) {
            instance = new AppUtil();
        }
        return instance;
    }

    private AppUtil() {
    }



}
