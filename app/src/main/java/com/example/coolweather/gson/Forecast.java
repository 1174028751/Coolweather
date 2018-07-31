package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 11740 on 2018/7/21.
 */

public class Forecast {

    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
