package com.example.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;
import java.util.prefs.PreferenceChangeEvent;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    //设置天气刷洗组件
    public SwipeRefreshLayout swipeRefresh;

    private String mWeatherId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            View decorView = getWindow().getDecorView();
            /**获取当前的活动的DecorViewetDecorView:这个方法是获取顶级视图
            注意点1：addView添加入的视图应该是默认在左上角，和group里面原有的视图无关
            注意点2:getDecorView既然是顶级视图，它包含整个屏幕，包括标题栏
            注意点3：根据实际测试发现，标题栏的左上角位置的坐标才是坐标原点位置*/

            /**
             * 1. View.SYSTEM_UI_FLAG_VISIBLE：显示状态栏，Activity不全屏显示(恢复到有状态栏的正常情况)。
             2. View.INVISIBLE：隐藏状态栏，同时Activity会伸展全屏显示。
             3. View.SYSTEM_UI_FLAG_FULLSCREEN：Activity全屏显示，且状态栏被隐藏覆盖掉。
             4. View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，
                 Activity顶端布局部分会被状态遮住。
             5. View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
             6. View.SYSTEM_UI_LAYOUT_FLAGS：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
             7. View.SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键。
             8. View.SYSTEM_UI_FLAG_LOW_PROFILE：状态栏显示处于低能显示状态(low profile模式)，状态栏上一些图标显示会被隐藏。
             9.View.SYSTEM_UI_FLAG_LAYOUT_STABLE 这个标志来帮助你的应用维持一个稳定的布局。*/
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);//状态栏设置选项
        }
        setContentView(R.layout.activity_weather);


        //初始化各控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置进度动画的颜色
        /**
         * SharedPreferences是Android平台上一个轻量级的存储类，用来保存应用的一些常用配置，比如Activity状态，
         * Activity暂停时，将此activity的状态保存到SharedPereferences中；当Activity重载，系统回调方法
         * onSaveInstanceState时，再从SharedPreferences中将值取出。其中的原理是通过Android系统生成一个xml文件保存
         *  到：/data/data/包名/shared_prefs目录下，类似键值对的方式来存储数据。
         */


        //每个应用有一个默认的偏好文件preferences.xml，使用getDefaultSharedPreferences获取

        //获得SharedPreferences对象
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString = prefs.getString("weather", null);

       if (weatherString != null) {
            //有缓存时直接解析天气数据
           Weather weather = Utility.handleWeatherResponse(weatherString);
           mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
       } else {
           Toast.makeText(this,"无缓存",Toast.LENGTH_LONG).show();
            //无缓存时去服务器查询天气,接收ChooseAreaFragment 传过来的weather_id,进行数据查询，
           mWeatherId = getIntent().getStringExtra("weather_id");
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);

       }

       swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               requestWeather(mWeatherId);
           }
       });

       String bingPic = prefs.getString("bing_pic",null);
       if(bingPic != null){
           Glide.with(this).load(bingPic).into(bingPicImg);
       }else{
           loadBingPic();
       }

    }

    /**
     * 根据天气id查询天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                 "&key=5b2f285da18e451e8828608e1c6d5c71";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){//判断weather 数据是否为空并且判断weather.status是否等于ok
                            //获得SharedPreferences.Editor对象。
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //存放数据
                            editor.putString("weather",responseText);
                            //完成提交 editor.commit();
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        }else{
                            /**
                             *  第一个参数：当前的上下文环境。可用getApplicationContext()或this
                             * 第二个参数：要显示的字符串。也可是R.string中字符串ID
                             * 第三个参数：显示的时间长短。Toast默认的有两个LENGTH_LONG(长)和LENGTH_SHORT(短)，也可以使用毫秒如2000ms
                             */
                            Toast.makeText(WeatherActivity.this,"获取天气数据失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
                loadBingPic();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气数据失败！！",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//设置组件的刷洗状态
                    }
                });
            }

        });
    }

    /**
     * 处理并且展示Weather实体类中的数据
     */
    private  void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        //这里split(' ')[1] 是一种缩写形式，把它拆开来看实际就是
        //先用split(' ')方法将字符串以" "开割形成一个字符串数组，然后再通过索引[1]取出所得数组中的第二个元素的值
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temprature+"℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();//从ViewGroup中移除所有子视图

        for(Forecast forecast : weather.forecastList){
            /**
             * inflate(int resource, ViewGroup root, boolean attachToRoot)
             * 1. 如果root为null，attachToRoot将失去作用，设置任何值都没有意义。
             2. 如果root不为null，attachToRoot设为true，则会给加载的布局文件的指定一个父布局，即root。
             3. 如果root不为null，attachToRoot设为false，则会将布局文件最外层的所有layout属性进行设置，当该view被添加到父view当中时，这些layout属性会自动生效。
             4. 在不设置attachToRoot参数的情况下，如果root不为null，attachToRoot参数默认为true。
             */
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度："+weather.suggestion.comform.info;
        String carWash = "洗车指数："+weather.suggestion.carWash.info;
        String sport = "运动建议："+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 加载必应每日一图
     */
    public void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            final String bingPic = response.body().string();
            SharedPreferences.Editor editor
                    = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
            editor.putString("bing_pic",bingPic);
            editor.apply();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                }
            });

            }
        });
    }

}
