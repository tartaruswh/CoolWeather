package com.example.a15840.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 15840 on 2018/1/8.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("tex")
        public String info;
    }
}
