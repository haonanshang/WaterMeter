package com.example.leonardo.watermeter.entity;


/**
 * 排序
 */
public class SortDataBean {
    String bch;
    String isDetectByPhone;

    public SortDataBean(String bch, String isDetectByPhone) {
        this.bch = bch;
        this.isDetectByPhone = isDetectByPhone;
    }

    public String getBch() {
        return bch;
    }

    public void setBch(String bch) {
        this.bch = bch;
    }

    public String getIsDetectByPhone() {
        return isDetectByPhone;
    }

    public void setIsDetectByPhone(String isDetectByPhone) {
        this.isDetectByPhone = isDetectByPhone;
    }

    @Override
    public String toString() {
        return "SortDataBean{" +
                "bch='" + bch + '\'' +
                ", isDetectByPhone='" + isDetectByPhone + '\'' +
                '}';
    }
}
