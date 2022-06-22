package com.dyb.clock.times;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDiff {

    /**
     * @param startTime 开始时间
     * @param pattern 时间格式
     */
    public long dateDiff(String startTime, String pattern) {
        long day = 0;
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(pattern);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数long diff
        // 获得两个时间的毫秒时间差异
        Date curDate = new Date(System.currentTimeMillis());
        String str = sd.format(curDate);
        try {
            long diff = sd.parse(str).getTime()
                    - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            long hour = diff % nd / nh;// 计算差多少小时
            long min = diff % nd % nh / nm;// 计算差多少分钟
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒//输出结果
//            Log.d("dateDiff", "dateDiff" + "时间相差：" + day + "天" + hour + "小时" + min + "分钟"
//                    + sec + "秒。");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return day;
    }

    /**
     * 时间戳转换为String
     *
     * @param milSecond 时间戳
     * @param pattern   时间格式
     */
    public String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

}
