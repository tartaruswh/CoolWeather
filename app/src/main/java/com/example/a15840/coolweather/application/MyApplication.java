package com.example.a15840.coolweather.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by 15840 on 2018/1/2.
 * 获取全局变量
 */

public class MyApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
    
}
