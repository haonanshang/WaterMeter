package com.example.leonardo.watermeter.entity;

/**
 * 水量bean
 */
public class WaterYieldBean {
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
}
