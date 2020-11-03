package com.example.leonardo.watermeter.global;

/**
 * Created by Leonardo on 2017/4/25.
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * 在这里存储一些全局变量
 */
public class GlobalData {

    public final String[] months = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
    public final String[] monthsInt = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

    public static boolean setAutoJump = false;//是否对自动跳转进行了设置
    public static boolean isAutoCamera = false;  //在打开手机端识别的前提下，是否在跳转下一户时自动打开相机
    public static boolean isFromManual = false;//判断是否是从手抄页面返回的

    public static String currentLongitude = "0";//当前任务的经度
    public static String currentLatitude = "0";//当前任务的纬度
    public static String[] UniQueBrandArray = {"MANN"};
    public static String Brand = null;
    public static int DetailDivideNum = 500;     //表册划分默认数量
    public static int EXTERNAL_DEVICE_ONE = 0;    // 外接设备1
    public static int EXTERNAL_DEVICE_TWO = 1;    // 外接设备2
    public static int EXTERNAL_DEVICE_THREE = 2;  // 外接设备3

    public static String IVAWATERMETER_PATH = "IvaWaterMeter";
    public static String IVAWATER_PATH = "IvaWater";


    /**
     * 获取文件夹地址 如果没有地址新建地址
     *
     * @param mContext
     * @param dirName
     * @return
     */
    public  static String getFilePath(Context mContext, String dirName) {
        File file;
        if (Environment.getExternalStorageState().equals("mounted")) {
            file = new File(mContext.getExternalFilesDir(dirName).getPath());
        } else {
            file = new File(mContext.getFilesDir().getPath() + File.separator + dirName);
        }

        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("GlobalData::get" + dirName + "File -> " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }

}
