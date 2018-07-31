package com.example.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.WeatherActivity;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent,int flag,int startId){
        updateWeather();
        updateBingpic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 2 * 60 * 60 * 1000;//小时的毫秒数
        //由此可见，uptimeMillis()返回的是系统从启动到当前处于非休眠期的时间。
        //elapsedRealTime()返回的是系统从启动到现在的时间
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent (this,AutoUpdateService.class);
        //正由于pendingintent中 保存有当前App的Context，使它赋予外部App一种能力，使得外部App可以如同当前App一样的执行pendingintent里的 Intent，
        // 就算在执行时当前App已经不存在了，也能通过存在pendingintent里的Context照样执行
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);//cancel()方法是为了解除PendingIntent和被包装的Intent之间的关联

        /**
         * (1)int type： 闹钟的类型，常用的有5个值：
         ELAPSED_REALTIME、ELAPSED_REALTIME_WAKEUP、RTC、RTC_WAKEUP、POWER_OFF_WAKEUP。
         ELAPSED_REALTIME：表示闹钟在手机睡眠状态下不可用，该状态下闹钟使用相对时间(相对于系统启动开始);
         ELAPSED_REALTIME_WAKEUP：表示闹钟在手机睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间;
         RTC：表示闹钟在手机睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间;
         RTC_WAKEUP：表示闹钟在手机睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用绝对时间;
         POWER_OFF_WAKEUP：表示闹钟在手机关机状态下也能正常进行提示功能，所以是5个状态中用的最多的状态之一，该状态下闹钟也是用绝对时间;不过受SDK版本影响，某些版本并不支持;
         (2)long startTime： 闹钟的第一次执行时间，以毫秒为单位，可以自定义时间，不过一般使用当前时间。
         需要注意的是，本属性与第一个属性(type)密切相关。如果第一个参数对
         应的闹钟使用的是相对时间(ELAPSED_REALTIME和ELAPSED_REALTIME_WAKEUP)，那么本属性就得使用相对时间(相对于
         系统启动时间来说)，比如当前时间就表示为：SystemClock.elapsedRealtime();
         如果第一个参数对应的闹钟使用的是绝对时间
         (RTC、RTC_WAKEUP、POWER_OFF_WAKEUP)，那么本属性就得使用绝对时间，比如当前时间就表示
         为：System.currentTimeMillis()。
         */
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return  super.onStartCommand(intent,flag,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }


    /**
     * 更新天气信息
     */

    public void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather_id",null);

        if(weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                    "&key=5b2f285da18e451e8828608e1c6d5c71";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {//判断weather 数据是否为空并且判断weather.status是否等于ok
                        //获得SharedPreferences.Editor对象。
                        SharedPreferences.Editor editor =
                                PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        //存放数据
                        editor.putString("weather", responseText);
                        //完成提交 editor.commit();
                        editor.apply();
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

            });
        }

    }

    /**
     * 更新必应每日一图
     */

    public void updateBingpic(){

        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor
                        = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }


}

