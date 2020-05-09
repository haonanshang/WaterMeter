package com.example.leonardo.watermeter.utils;

import com.itgoyo.logtofilelibrary.LogToFileUtils;

import org.apache.axis.utils.StringUtils;

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
        boolean result = false;
        double percent = 0.0;
        try {
            if (!StringUtils.isEmpty(averageWater)) {
                percent = getWaterPercent(averageWater);
            }
            if (percent == 0)
                return result;
            if (Double.valueOf(currentWater) >= Double.valueOf(averageWater) * percent) {
                result = false;
            } else {
                result = true;
            }
        } catch (Exception e) {
            LogToFileUtils.write(e.toString());
        }
        return result;
    }

    /**
     * 获取水量判断的百分比
     *
     * @param averageWater 平均水量
     * @return
     */
    public static double getWaterPercent(String averageWater) {
        double percent = 0.0;
        try {
            double data_value = Double.valueOf(averageWater);
            if (data_value >= 0 && data_value < 1) {
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
        } catch (NumberFormatException e) {
            LogToFileUtils.write("getWaterPercent -> averageWater :" + averageWater);
        }
        return percent + 1;
    }
}
