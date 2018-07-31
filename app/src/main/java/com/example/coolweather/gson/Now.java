package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 11740 on 2018/7/21.
 */

public class Now {
    @SerializedName("tmp")
    public String temprature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
