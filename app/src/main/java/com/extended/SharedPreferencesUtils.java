package com.extended;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class SharedPreferencesUtils {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferencesUtils(Context context) {
        this.context = context;
    }

    /*
     *得到本地存储的年份字符串
     */
    public  String getYearString() {
        sharedPreferences = context.getSharedPreferences("Yearinfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        return sharedPreferences.getString("yearString", Calendar.getInstance().get(Calendar.YEAR) + "");
    }

    /*
     *更新本地存储的年份
     */
    public  void updateYearString(String updateYearStr) {
        sharedPreferences = context.getSharedPreferences("Yearinfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("yearString", updateYearStr);
        editor.commit();
    }
}
