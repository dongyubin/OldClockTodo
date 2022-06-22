package com.dyb.clock;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.graphics.Typeface;

import com.dyb.clock.https.SSLSocketFactoryCompat;
import com.dyb.clock.times.TimeDiff;
import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView textViewDate;
    private TextView textViewWeek;
    private TextView textViewDianliang;
    private TextView tv_weather;
    private TextView tv_lianai;
    private TextView tv_gzh;
    private TextView tv_nextWeather;
    private TextView tv_wp;
    private TextView tv_bl;
    private ImageView iv_icon;
    private ImageView iv_nextIcon;
    private ImageView iv_gzhIcon;
    private ImageView iv_wpIcon;
    private ImageView iv_blIcon;

    public static final MediaType JSON1 = MediaType.parse("application/json; charset=utf-8");
    private static String love_day = "2020-09-09 00:00:00";
    private static String pattern = "yyyy-MM-dd HH:mm:ss";
    TimeDiff timeDiff = new TimeDiff();
    private Typeface tf;
    BatteryReceiver batteryReceiver = new BatteryReceiver();


    private ListView lvFruits;
    private List<Fruit> fruitList = new ArrayList<Fruit>(); //创建集合保存水果信息
    private FruitAdaoper fruitAdapter;


    /**
     * 时间更新handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
                    //获取当前时间
                    Date date = new Date(System.currentTimeMillis());
                    // 更改字体
                    Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LESLIE.TTF");
                    textView.setTypeface(typeface);
                    textView.setText("" + simpleDateFormat.format(date));
                    simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
                    textViewDate.setTypeface(tf);
                    textViewDate.setText("" + simpleDateFormat.format(date));
                    simpleDateFormat = new SimpleDateFormat("EEEE");
                    textViewWeek.setTypeface(tf);
                    textViewWeek.setText("" + simpleDateFormat.format(date));
                    break;
                case 1:
                    lvFruits.setAdapter(fruitAdapter);
//                    fruitAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    private Handler loveHandler = new Handler() {
        @Override
        //重写handleMessage方法,根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg) {
            if (msg.what == 0x121) {
                LoveDateDiff();
            } else if (msg.what == 0x122) {
                testWeather();
            }else if (msg.what == 0x123) {
                testWeather3D();
            }else if (msg.what == 0x124) {
                getTodo();
            }else if (msg.what == 0x125) {
                getWordPress();
            }else if (msg.what == 0x126) {
                getBilibili();
            }
        }
    };

    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断它是否是为电量变化的Broadcast Action
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, ifilter);
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;

                //获取当前电量
                int level = intent.getIntExtra("level", 0);
                //电量的总刻度
                int scale = intent.getIntExtra("scale", 100);


                if (isCharging) {
//                    if (usbCharge) {
//                        Toast.makeText(MainActivity.this, "手机正处于USB连接！", Toast.LENGTH_SHORT).show();

//                        textViewDianliang.setText("充电中："+((level*100)/scale)+"%");
//                    } else if (acCharge) {
//                        Toast.makeText(MainActivity.this, "手机通过电源充电中！", Toast.LENGTH_SHORT).show();
                    //把它转成百分比
                    textViewDianliang.setTypeface(tf);
                    textViewDianliang.setText("⚡" + ((level * 100) / scale) + "%");
//                    }
                } else {
//                    Toast.makeText(MainActivity.this, "手机未连接USB线！", Toast.LENGTH_SHORT).show();
                    textViewDianliang.setTypeface(tf);
                    textViewDianliang.setText("\uD83D\uDD0B " + ((level * 100) / scale) + "%");
                }

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        textView = (TextView) findViewById(R.id.txt);
        textViewDate = (TextView) findViewById(R.id.date);
        textViewWeek = (TextView) findViewById(R.id.week);
        textViewDianliang = (TextView) findViewById(R.id.dianliang);
        tv_lianai = (TextView) findViewById(R.id.lianai);
        tv_weather = (TextView) findViewById(R.id.weather);
        tv_wp = (TextView) findViewById(R.id.wp);
        tv_bl=(TextView)findViewById(R.id.bl);
        iv_icon = (ImageView) findViewById(R.id.icon);
        tv_nextWeather = (TextView) findViewById(R.id.nextWeather);
        iv_nextIcon = (ImageView) findViewById(R.id.nextIcon);
        iv_wpIcon = (ImageView) findViewById(R.id.wpIcon);
        iv_blIcon=(ImageView)findViewById(R.id.blIcon);
        tf= Typeface.createFromAsset(getAssets(), "fonts/Taipei-Sans-TC-Beta-Regular-2.ttf");


//        注册广播接受者java代码
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        创建广播接受者对象

//        注册receiver
        registerReceiver(batteryReceiver, intentFilter);
        refreshLove();
        /**
         *TODO：在和风天气https://dev.qweather.com/申请key
         */
        HeConfig.init("XXX", "XXX");
        HeConfig.switchToDevService();



        lvFruits = (ListView) findViewById(R.id.lvFruits);  //获得子布局
        fruitAdapter = new FruitAdaoper(this,
                R.layout.listview_item, fruitList);
              //绑定数据和适配器
        lvFruits.setAdapter(fruitAdapter);
        lvFruits.setOnItemClickListener(new OnItemClickListener() { //点击每一行的点击事件

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long id) {
                Fruit fruit=fruitList.get(position);     //获取点击的那一行
                Toast.makeText(MainActivity.this,fruit.getTodoText(),Toast.LENGTH_LONG).show();//使用吐司输出点击那行水果的名字
            }
        });

    }


    private void refreshLove() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshTime();
            }
        }).start();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loveHandler.sendEmptyMessage(0x121);
                loveHandler.sendEmptyMessage(0x122);
                loveHandler.sendEmptyMessage(0x123);
                loveHandler.sendEmptyMessage(0x125);
                loveHandler.sendEmptyMessage(0x126);
            }
        }, 0, 3600 * 1000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loveHandler.sendEmptyMessage(0x124);
            }
        }, 0, 10 * 1000);

    }

    private void refreshTime(){
        while (true) {
            Message message=handler.obtainMessage();
            message.what=0;
            handler.sendMessage(message);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 恋爱天数
     */
    public void LoveDateDiff() {
        long love_cha = timeDiff.dateDiff(love_day, pattern);
        Toast.makeText(MainActivity.this,"LOVE已更新！",Toast.LENGTH_SHORT).show();
        tv_lianai.setTypeface(tf);
        tv_lianai.setText("💑 " + love_cha + " 天");
    }

    /**
     * TODO：将下方XXX地址改为自己的wordpress博客地址
     */
    private void getWordPress() {
        OkHttpClient client = SSLSocketFactoryCompat.getClient();
        Request request = new Request.Builder()
                .url("XXX/wp-json/wp/v2/posts")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("kwwl", "getDataAsync error：" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    String soft = response.body().string();
                    JSONArray ja = JSON.parseArray(soft);
                    String nextDay = ja.getString(0);
                    JSONObject jb2 = JSON.parseObject(nextDay);
                    String date = jb2.getString("date");
                    String[] new_date_y = date.split("T");
                    String startTime = new_date_y[0] + " " + new_date_y[1];
                    long WordPressDay = timeDiff.dateDiff(startTime, pattern);
                    Log.d("title", "new_date_y" + WordPressDay);
                    showWordpress(WordPressDay + "");
                }
            }
        });
    }

    /**
     * TODO：在小程序：卖亏本，获取TODO列表API地址，替换下方的XXX
     */
    private void getTodo() {
        OkHttpClient client = SSLSocketFactoryCompat.getClient();
        Request request = new Request.Builder()
                .url("XXX")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("kwwl", "getDataAsync error：" + e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    String todo = response.body().string();
                    JSONObject datas = JSON.parseObject(todo);
                    String todolists = datas.getString("todolists");
                    JSONArray ja = JSON.parseArray(todolists);
                    Log.d("todolists", "todolists111:" + ja);
                    showTodo(ja);
                }
            }
        });
    }

    private void showTodo(JSONArray ja){
        fruitList.clear();
        for(int i=0;i<ja.size();i++){         //将数据添加到集合中
            JSONObject jb = ja.getJSONObject(i);
            String todoText=jb.getString("todo")+'('+jb.getString("miaoshu")+')';
            String todoTimeText=jb.getString("pubtime");
            fruitList.add(new Fruit(todoText,todoTimeText));
        }
        Log.d("showTodo ", "showTodo: "+ja);
        Message message = handler.obtainMessage();
        message.what = 1;
        handler.sendMessage(message);

    }
    /**
     *TODO：将mid=xxx里面的xxx替换为自己的bilibili个人空间id
     */
    private void getBilibili() {
        OkHttpClient client = SSLSocketFactoryCompat.getClient();
        Request request = new Request.Builder()
                .url("https://api.bilibili.com/x/web-interface/card?mid=XXX")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("kwwl", "getBilibili error：" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    String bdate = response.body().string();
                    JSONObject datas = JSON.parseObject(bdate);
                    String data = datas.getString("data");
                    JSONObject fws = JSON.parseObject(data);
                    String follower = fws.getString("follower");
                    showBilibili(follower + "");
                }
            }
        });
    }



    private void showWordpress(final String data) {
//切换回主线程处理，安卓不允许在子线程设置ui界面，所以需要回到主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"WordPress已更新！",Toast.LENGTH_SHORT).show();
                String url = "http://s.w.org/favicon.ico?2";
                Glide.with(MainActivity.this).load(url).into(iv_wpIcon);
                tv_wp.setTypeface(tf);
                tv_wp.setText(" " + data + " 天");
            }
        });

    }

    private void showBilibili(final String data) {
//切换回主线程处理，安卓不允许在子线程设置ui界面，所以需要回到主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"Bilibili已更新！",Toast.LENGTH_SHORT).show();
                String url = "http://www.bilibili.com/favicon.ico";
                Glide.with(MainActivity.this).load(url).into(iv_blIcon);
                tv_bl.setTypeface(tf);
                tv_bl.setText(" " + data);
            }
        });

    }


    /**
     * 天气
     */

    private void testWeather() {
        QWeather.getWeatherNow(MainActivity.this, "101120209", Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable e) {
                Log.d("Weather", "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                String now = new Gson().toJson(weatherBean.getNow());
                JSONObject jb = JSON.parseObject(now);
                String text = jb.getString("text");
                String temp = jb.getString("temp");
                String icon = jb.getString("icon");
                String weather = "市北 " + text + "：" + temp + " ℃";
                tv_weather.setTypeface(tf);
                tv_weather.setText(weather);
                String url = "http://gitee.com/bunagi/WeatherIcon/raw/master/weather-icon-S2/64/" + icon + ".png";
                Glide.with(MainActivity.this).load(url).into(iv_icon);
                Toast.makeText(MainActivity.this, "天气已更新！", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 未来3天的天气
     */
    private void testWeather3D() {
        QWeather.getWeather3D(MainActivity.this, "101120209", new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable e) {
                Log.d("TAG", "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                String daily = new Gson().toJson(weatherDailyBean.getDaily());
                JSONArray ja = JSON.parseArray(daily);
                String nextDay = ja.getString(1);
                JSONObject jb = JSON.parseObject(nextDay);
                String fxDate = jb.getString("fxDate");
                String textDay = jb.getString("textDay");
                String tempMax = jb.getString("tempMax");
                String tempMin = jb.getString("tempMin");
                String iconDay = jb.getString("iconDay");
                String weather = "明天 " + textDay + "：" + tempMin + "~" + tempMax + "℃";
                tv_nextWeather.setTypeface(tf);
                tv_nextWeather.setText(weather);
                String url = "http://gitee.com/bunagi/WeatherIcon/raw/master/weather-icon-S2/64/" + iconDay + ".png";
                Glide.with(MainActivity.this).load(url).into(iv_nextIcon);
                Toast.makeText(MainActivity.this, "明日天气已更新！", Toast.LENGTH_SHORT).show();

            }
        });
    }


  
    @Override
    protected void onDestroy() {
        unregisterReceiver(batteryReceiver);
        super.onDestroy();
    }
}



