package com.example.leonardo.watermeter.utils;


import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 特殊品牌工具类
 */
public class SpecialBrandUtil {

    /**
     * 存储特殊机型 自定义相机时的旋转角度
     **/
    public static ConcurrentHashMap<String, Integer> customCameraDegreeHashMap = new ConcurrentHashMap<>();


    static {
        customCameraDegreeHashMap.put("default", 0);
        customCameraDegreeHashMap.put("MI 9 SE",90);
    }


    /**
     * 获取相应品牌的自定义相机的角度
     *
     * @param brand
     * @return
     */
    public static Integer getCustomCameraDegree(String brand) {
        if (customCameraDegreeHashMap.containsKey(brand)) {
            return customCameraDegreeHashMap.get(brand);
        }
        return customCameraDegreeHashMap.get("default");
    }


}
