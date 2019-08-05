package com.example.leonardo.watermeter.entity;

import java.io.Serializable;

/**
 * 阶梯水价
 */
public class LadderPriceBean implements Serializable {

    private static final long serialVersionUID = 9016586646623852231L;
    private String del_date;
    private String del_flag;
    private int id;
    private String ladder_end;
    private int ladder_start;
    private float ladder_price;
    private int ladder_step;
    private String org_code;
    private float total_price;

    public LadderPriceBean() {
    }

    public LadderPriceBean(String del_date, String del_flag, int id, String ladder_end, int ladder_start, float ladder_price, int ladder_step, String org_code, float total_price) {
        this.del_date = del_date;
        this.del_flag = del_flag;
        this.id = id;
        this.ladder_end = ladder_end;
        this.ladder_start = ladder_start;
        this.ladder_price = ladder_price;
        this.ladder_step = ladder_step;
        this.org_code = org_code;
        this.total_price = total_price;
    }

    public String getDel_date() {
        return del_date;
    }

    public void setDel_date(String del_date) {
        this.del_date = del_date;
    }

    public String getDel_flag() {
        return del_flag;
    }

    public void setDel_flag(String del_flag) {
        this.del_flag = del_flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLadder_end() {
        return ladder_end;
    }

    public void setLadder_end(String ladder_end) {
        this.ladder_end = ladder_end;
    }

    public int getLadder_start() {
        return ladder_start;
    }

    public void setLadder_start(int ladder_start) {
        this.ladder_start = ladder_start;
    }

    public float getLadder_price() {
        return ladder_price;
    }

    public void setLadder_price(float ladder_price) {
        this.ladder_price = ladder_price;
    }

    public int getLadder_step() {
        return ladder_step;
    }

    public void setLadder_step(int ladder_step) {
        this.ladder_step = ladder_step;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public float getTotal_price() {
        return total_price;
    }

    public void setTotal_price(float total_price) {
        this.total_price = total_price;
    }
}
