package com.prosdk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.ILoginDeviceCallback;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.LoginHelper;
import com.macrovideo.sdk.objects.DeviceInfo;
import com.macrovideo.sdk.objects.LoginParam;


public class LocalDefines {
    public final static int RESULT_SUCCESSFULE = 200;
    public final static int IMAGE_STANDARD_DEFINITION = 0;   //标清
    public final static int IMAGE_HIGH_DEFINITION = 1;       //高清
    public static Fraction SD_Aspect_Ratio=new Fraction(4,3);   // 标清分辨率宽高比
    public static Fraction HD_Aspect_Ratio=new Fraction(16,9);  // 高清分辨率宽高比

    /**
     * 获取设备信息
     *
     * @param context
     * @return
     */
    public static DeviceInfo getDeviceInfoFromSP(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sdk_demo_deviceInfo", context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String deviceInfoJson = sharedPreferences.getString("deviceInfoJson", null);
            if (TextUtils.isEmpty(deviceInfoJson)) {
                return null;
            } else {
                return JSON.parseObject(deviceInfoJson, DeviceInfo.class);
            }
        }
        return null;
    }

    /**
     * 绑定设备信息
     *
     * @param context
     * @param deviceInfo
     */
    public static void setDeviceInfoToSP(Context context, DeviceInfo deviceInfo) {
        String deviceInfoJson;
        if (deviceInfo == null) {
            return;
        } else {
            deviceInfoJson = JSON.toJSONString(deviceInfo);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("sdk_demo_deviceInfo", context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.putString("deviceInfoJson", deviceInfoJson);
                editor.apply();
            }
        }
    }

    /**
     * 获取设备清晰度
     *
     * @param context
     * @return
     */
    public static int getDeviceDefinition(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sdk_device_definition", context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            return sharedPreferences.getInt("definition", IMAGE_HIGH_DEFINITION);
        }
        return IMAGE_HIGH_DEFINITION;
    }

    /**
     * 设置设备清晰度
     *
     * @param context
     * @param definition
     */
    public static void setDeviceDefinition(Context context, int definition) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sdk_device_definition", context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.putInt("definition", definition);
                editor.apply();
            }
        }
    }


}
