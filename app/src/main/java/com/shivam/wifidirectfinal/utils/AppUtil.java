package com.shivam.wifidirectfinal.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Shivam Sharma on 16-09-2019.
 */
public class AppUtil {
    private static AppUtil instance;

    public static AppUtil getInstance() {
        if(instance == null){
            instance = new AppUtil();
        }
        return instance;
    }

    private AppUtil() {
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getCurrentTime(){
        /*Formatter fmt = new Formatter();
        Calendar cal = Calendar.getInstance();
        fmt = new Formatter();
        return String.valueOf(fmt.format("%tl:%tM", cal, cal));*/

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormatter.format(calendar.getTime());
    }


}
