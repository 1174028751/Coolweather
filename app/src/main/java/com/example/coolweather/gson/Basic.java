package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 11740 on 2018/7/21.
 */

public class Basic {
    @SerializedName("city")//每一个变量添加 @SerializedName 注解，这样在解析的时候就能转换成注解标示的字段名
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
