package com.example.leonardo.watermeter.entity;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

/**
 * 水量bean
 */
public class WaterYieldBean implements Comparable<WaterYieldBean> {
    String waterYield;//用水量
    String waterDate;//水量月份

    public WaterYieldBean(String waterYield, String waterDate) {
        this.waterYield = waterYield;
        this.waterDate = waterDate;
    }

    public String getWaterYield() {
        return waterYield;
    }

    public void setWaterYield(String waterYield) {
        this.waterYield = waterYield;
    }

    public String getWaterDate() {
        return waterDate;
    }

    public void setWaterDate(String waterDate) {
        this.waterDate = waterDate;
    }

    @Override
    public int compareTo(@NonNull WaterYieldBean waterYieldBean) {
        if (StringUtils.isEmpty(waterDate) || StringUtils.isEmpty(waterYieldBean.getWaterDate())) {
            return -1;
        } else {
            return waterDate.compareTo(waterYieldBean.getWaterDate());
        }
    }
}
