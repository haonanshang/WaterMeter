package com.example.leonardo.watermeter.entity;


import org.litepal.crud.LitePalSupport;

/**
 * 手机端划分表册 将大数据量表册划分
 */
public class DetailDivideData extends LitePalSupport {
    private String cbyf; //抄表月份
    private String bch; //表册号
    private String divideNumber; //划分序号
    private String startIndex;//开始坐标
    private String endIndex;//截止坐标

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

    public String getDivideNumber() {
        return divideNumber;
    }

    public void setDivideNumber(String divideNumber) {
        this.divideNumber = divideNumber;
    }

    public String getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(String startIndex) {
        this.startIndex = startIndex;
    }

    public String getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(String endIndex) {
        this.endIndex = endIndex;
    }
}
