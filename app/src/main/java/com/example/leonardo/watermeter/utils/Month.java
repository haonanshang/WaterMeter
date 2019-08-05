package com.example.leonardo.watermeter.utils;

import android.content.Context;

import com.extended.SharedPreferencesUtils;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/8/24 0024.
 */

public class Month {
    public static String Monthtransform(Context context, String str) {
        HashMap<String, String> monthmap = new HashMap<>();
        final String yearString = new SharedPreferencesUtils(context).getYearString();
        monthmap.put("一月", yearString + "01");
        monthmap.put("二月", yearString + "02");
        monthmap.put("三月", yearString + "03");
        monthmap.put("四月", yearString + "04");
        monthmap.put("五月", yearString + "05");
        monthmap.put("六月", yearString + "06");
        monthmap.put("七月", yearString + "07");
        monthmap.put("八月", yearString + "08");
        monthmap.put("九月", yearString + "09");
        monthmap.put("十月", yearString + "10");
        monthmap.put("十一月", yearString + "11");
        monthmap.put("十二月", yearString + "12");
        return monthmap.get(str);
    }

    public static String GetMonthStr(String Str) {
        HashMap<String, String> monthmap = new HashMap<>();
        monthmap.put("01", "一月");
        monthmap.put("02", "二月");
        monthmap.put("03", "三月");
        monthmap.put("04", "四月");
        monthmap.put("05", "五月");
        monthmap.put("06", "六月");
        monthmap.put("07", "七月");
        monthmap.put("08", "八月");
        monthmap.put("09", "九月");
        monthmap.put("10", "十月");
        monthmap.put("11", "十一月");
        monthmap.put("12", "十二月");
        return monthmap.get(Str);
    }
}
