package com.example.leonardo.watermeter.utils;

import org.apache.axis.utils.StringUtils;
import org.litepal.crud.DataSupport;

/**
 * 计算工具类
 */
public class QuantityCalculationUtils {
    /**
     * 0  ？
     * 1-10  ±50%
     * 10-30 ±30%
     * 30-60 ±25%
     * 60-100 ±20%
     * 100-   ±10%
     * 判断水量是否合法
     *
     * @param averageWater 平均水量
     * @param currentWater 当前水量
     * @return
     */
    public static boolean WaterEstimat(String averageWater, String currentWater) {
        double percent = getWaterPercent(averageWater);
        if (Double.valueOf(currentWater) >= Double.valueOf(averageWater) * percent) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取水量判断的百分比
     *
     * @param averageWater
     * @return
     */
    public static double getWaterPercent(String averageWater) {
        double data_value = Double.valueOf(averageWater);
        double percent = 0.0f;
        if (data_value > 0 && data_value < 1) {
            percent = 1;
        } else if (data_value >= 1 && data_value < 10) {
            percent = 0.5;
        } else if (data_value > 10 && data_value < 30) {
            percent = 0.3;
        } else if (data_value >= 30 && data_value < 60) {
            percent = 0.25;
        } else if (data_value >= 60 && data_value < 100) {
            percent = 0.2;
        } else if (data_value >= 100) {
            percent = 0.1;
        }
        return percent + 1;
    }
}
