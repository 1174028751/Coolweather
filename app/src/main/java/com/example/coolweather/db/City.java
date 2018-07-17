package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 11740 on 2018/7/17.
 */

public class City extends DataSupport {
    private int id;
    private String cityName;//城市名称
    private int cityCode;//城市编号
    private int proviceId;//所属省份

    public int getId(){
        return id;
    }

    public void setId(){
        this.id = id;
    }

    public String getCityName(){
        return cityName;
    }

    public void setCityName(){
        this.cityName = cityName;
    }

    public int getCityCode(){
        return cityCode;
    }

    public void setCityCode(){
        this.cityCode = cityCode;
    }

    public int getProviceId(){
        return proviceId;
    }

    public void setProviceId(){
        this.proviceId = proviceId;
    }
}

