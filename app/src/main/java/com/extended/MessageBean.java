package com.extended;

public class MessageBean {
    private String month;//抄表月份
    private String statisTicalForms;//表册号

    public MessageBean(String month, String statisTicalForms) {
        this.month = month;
        this.statisTicalForms = statisTicalForms;
    }

    public String getStatisTicalForms() {

        return statisTicalForms;
    }

    public void setStatisTicalForms(String statisTicalForms) {
        this.statisTicalForms = statisTicalForms;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
