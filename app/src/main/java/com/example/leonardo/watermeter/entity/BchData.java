package com.example.leonardo.watermeter.entity;


import org.litepal.crud.LitePalSupport;

/**
 * Created by Leonardo on 2017/5/2.
 */

public class BchData extends LitePalSupport {
    private String cbyf;
    private String bch;
    private String isDetectByPhone; //是否在手机端识别 0 是手机端识别  无自动跳转选项（可以选择是否自动打开相机） 1 是不识别  允许自动跳转

    public String getCbyf() {
        return cbyf;
    }

    public void setCbyf(String cbyf) {
        this.cbyf = cbyf;
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
        return "BchData{" +
                "cbyf='" + cbyf + '\'' +
                ", bch='" + bch + '\'' +
                ", isDetectByPhone='" + isDetectByPhone + '\'' +
                '}';
    }
}
