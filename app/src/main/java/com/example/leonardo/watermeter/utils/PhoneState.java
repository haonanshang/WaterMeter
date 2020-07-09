package com.example.leonardo.watermeter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

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
     * 获取唯一标识符： android 10不支持获取imei号  因为区分系统版本 低于android10版本 继续使用imei号
     * 高版本使用androidID
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        System.out.println("The SDK version of the software currently running -> " + Build.VERSION.SDK_INT);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mDeviceId = getUniqueID(context);
        } else {
            mDeviceId = getTelephonyManager(context).getDeviceId();
        }
        return mDeviceId;
    }

    /**
     * 获取唯一标识符： android 10不支持获取imei号，
     *
     * @param context
     * @return
     */
    public static String getUniqueID(Context context) {
        String id = null;
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        /**
         * 加密android ID
         */
        System.out.println("getUniqueID::androidId::" + androidId);
//        if (!TextUtils.isEmpty(androidId)) {
//            try {
//                UUID uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
//                id = uuid.toString();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println("getUniqueID::encrypt::androidId::" + id);
//        if (TextUtils.isEmpty(id)) {
//            id = getUUID();
//        }
//        return TextUtils.isEmpty(id) ? UUID.randomUUID().toString() : id;
        return androidId;
    }

    @SuppressLint("MissingPermission")
    private static String getUUID() {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = android.os.Build.getSerial(); // TODO crash in Q
            } else {
                serial = Build.SERIAL;
            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial"; // 随便一个初始化
        }

        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }


    /**
     * 检查当前网络状态
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
