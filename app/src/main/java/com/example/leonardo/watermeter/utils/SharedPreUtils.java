package com.example.leonardo.watermeter.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.FireHydrant.entity.FireHydrantDetailData;
import com.example.leonardo.watermeter.global.GlobalData;

import java.util.List;

/**
 * Created by 尚浩楠 on 2018/5/11.
 */

public class SharedPreUtils {
    final static String SPTAG = "WaterMeter";


    /**
     * 设置ip
     *
     * @param ip
     * @param context
     */
    public static void SetIp(String ip, Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ipString", ip);
        editor.commit();
    }

    /**
     * 获取ip
     *
     * @param context
     * @return
     */

    public static String GetIp(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sp.getString("ipString", "");
    }

    /**
     * 设置表册分库目标数量
     *
     * @param bcNumber
     * @param context
     */
    public static void SetBcNumber(int bcNumber, Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("bcNumber", bcNumber);
        editor.commit();
    }

    /**
     * 获取表册分库目标数量
     *
     * @param context
     * @return
     */

    public static int GetBcNumber(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sp.getInt("bcNumber", GlobalData.DetailDivideNum);
    }


    /**
     * 设置闪光灯之前的状态 true 开闪光灯 false 关闭闪光灯
     *
     * @param isFlash
     * @param context
     */
    public static void setFlashStatus(boolean isFlash, Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("FlashStatus", isFlash);
        editor.commit();
    }

    /**
     * 获取闪光灯的状态
     *
     * @param context
     * @return
     */
    public static boolean getFlashStatus(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sp.getBoolean("FlashStatus", false);
    }

    /**
     * 设置 外接设备类型   0:外接设备-1 代表旧式盒子  1:外接设备-2 代表新式盒子   2：连接设备-3 proSDk
     *
     * @param context
     * @param deviceType
     */
    public static void setDeviceType(Context context, int deviceType) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("deviceType", deviceType);
        editor.commit();
    }

    /**
     * 获取外接设备类型
     * （最新版）默认是  设备盒子2 proSDk,  3.2.3 版本之前是1
     *
     * @param context
     * @return
     */
    public static int getDeviceType(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sp.getInt("deviceType", 2);
    }

    /**
     * 设置数据类型  消防栓数据还是普通数据
     *
     * @param context
     * @param bo
     */
    public static void setDataType(Context context, boolean bo) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("IsFireHydrantData", bo);
        editor.commit();
    }

    /**
     * 获取数据类型 true 代表消防栓数据 false 代表正常数据
     *
     * @param context
     * @return
     */
    public static boolean getDataType(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sp.getBoolean("IsFireHydrantData", false);
    }

    /**
     * 获取消防栓数据的位置
     *
     * @param datas
     * @param currentData
     * @return
     */
    public static int GetFHDataPostion(List<FireHydrantDetailData> datas, FireHydrantDetailData currentData) {
        int postion = 0;
        for (int i = 0; i < datas.size(); i++) {
            if (currentData.getFire_hydrant_id().equals(datas.get(i).getFire_hydrant_id())) {
                postion = i;
                break;
            }
        }
        return postion;
    }

    /**
     * 设置是否打开提示dialog
     *
     * @param context
     * @param bo
     */
    public static void setReminderDialogStatus(Context context, boolean bo) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("IsShowReminderDialog", bo);
        editor.commit();
    }

    /**
     * 获取提示框显示的状态
     *
     * @param context
     * @return
     */
    public static boolean getReminderDialogStatus(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
        return sp.getBoolean("IsShowReminderDialog", true);
    }


}
