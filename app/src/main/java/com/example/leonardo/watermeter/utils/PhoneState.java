package com.example.leonardo.watermeter.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by Leonardo on 2017/4/6.
 */

public class PhoneState {
    private static TelephonyManager mTelephonyManager = null;
    private static String mDeviceId = null;

    public static TelephonyManager getTelephonyManager(Context context) {
        // 获取telephony系统服务，用于取得SIM卡和网络相关信息
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }

    /**
     * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
     * 取得手机IMEI
     * available.
     */
    public static String getDeviceId(Context context) {
        mDeviceId = getTelephonyManager(context).getDeviceId();// String
//        mDeviceId = "861423030746343";   //跟配给我的测试账号
//        mDeviceId = "868689021614186";    //小周那边的一个测试账号
//        mDeviceId = "868689022626049";//用来测试备注信息是否为空的账号
        return mDeviceId;
    }

    /**
     * 检查当前网络状态
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
