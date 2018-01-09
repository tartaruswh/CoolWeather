package com.example.a15840.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 15840 on 2018/1/8.
 */

public class Forecast {
    public String date;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
    @SerializedName("tmp")
    public Temperature temperature;
    public class Temperature{
        public String max;
        public String min;
    }
}
