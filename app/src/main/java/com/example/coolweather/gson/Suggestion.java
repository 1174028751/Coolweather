package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 11740 on 2018/7/21.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comform;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("sport")
    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
