package com.example.coolweather.gson;

/**
 * Created by 11740 on 2018/7/21.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
