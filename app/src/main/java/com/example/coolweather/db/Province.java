package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 11740 on 2018/7/17.
 **/
 public class Province extends DataSupport {
 private int id;
 private String provinceName;//省份名称
 private int provinceCode;//省份编号


 public int getId(){
 return id;
 }

 public void setId(){
  this.id = id;
 }

 public String getProvinceName(){
  return provinceName;
 }

 public void setProvinceName(){
  this.provinceName = provinceName;
 }

 public int getProvinceCode(){
  return provinceCode;
 }

 public void setProvinceCode(){
  this.provinceCode = provinceCode;
 }

 }
