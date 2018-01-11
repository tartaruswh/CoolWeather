package com.example.a15840.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 15840 on 2018/1/8.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
