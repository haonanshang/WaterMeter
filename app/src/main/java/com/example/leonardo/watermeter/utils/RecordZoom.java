package com.example.leonardo.watermeter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class RecordZoom {
    public static void Setzoom(Context context, String zoom) {
        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences = context.getSharedPreferences("Zoominfo", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("zoomparam", zoom);
        editor.commit();
    }

    public static Double getzoom(Context context) {
        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences = context.getSharedPreferences("Zoominfo", Context.MODE_APPEND);
        return Double.valueOf(sharedPreferences.getString("zoomparam", "1.0")).doubleValue();
    }
}
