package com.example.leonardo.watermeter.utils;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.leonardo.watermeter.global.GlobalData;

/**
 * Created by Administrator on 2017/11/27 0027.
 */

public class LocationTools {
    public static LocationClient mLocationClient = null;


    public static void SetLocation(Context context) {

        GlobalData.currentLatitude = "0";
        GlobalData.currentLongitude = "0";
        mLocationClient = new LocationClient(context.getApplicationContext());
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式，默认高精度
        option.setCoorType("bd09ll");//设置返回经纬度坐标类型，默认gcj02
        option.setScanSpan(1000);//设置发起定位请求的间隔 如果设置为0，则代表单次定位，即仅定位一次，默认为0  如果设置非0，需设置1000ms以上才有效
        option.setOpenGps(true);//设置是否使用gps，默认false  使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setLocationNotify(true);//设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setIgnoreKillProcess(false);// 定位SDK内部是一个service，并放到了独立进程。 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setEnableSimulateGps(false);//设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
               // System.out.println("被响应，已经执行");
                GlobalData.currentLatitude = bdLocation.getLatitude() + "";    //获取纬度信息
                GlobalData.currentLongitude = bdLocation.getLongitude() + "";    //获取经度
               // System.out.println("得到的经度为：" + GlobalData.currentLongitude + "得到的纬度为：" + GlobalData.currentLatitude + "...");
            }
        });
    }


    public static void Stoplocayion() {
        mLocationClient.stop();
    }
}
