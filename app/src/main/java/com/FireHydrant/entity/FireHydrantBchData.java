package com.FireHydrant.entity;

import org.litepal.crud.LitePalSupport;

/*
消防栓的表册
 */
public class FireHydrantBchData extends LitePalSupport {
    private String cbyf;
    private String bch;


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
}
