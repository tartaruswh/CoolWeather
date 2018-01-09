package com.example.a15840.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 15840 on 2018/1/8.
 */

public class AQI {
    @SerializedName("city")
    public AQICity aqiCity;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
