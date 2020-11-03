package com.example.leonardo.watermeter.entity;

import java.io.Serializable;

/**
 * 用户用水量阶梯水价
 */
public class UsedLadderPriceBean implements Serializable {
    private static final long serialVersionUID = -7543979400158111192L;
    private int ladderStep;//水价档位
    private String ladderPrice;//档位总水价

    public UsedLadderPriceBean(int ladderStep, String ladderPrice) {
        this.ladderStep = ladderStep;
        this.ladderPrice = ladderPrice;
    }

    public int getLadderStep() {
        return ladderStep;
    }

    public void setLadderStep(int ladderStep) {
        this.ladderStep = ladderStep;
    }

    public String getLadderPrice() {
        return ladderPrice;
    }

    public void setLadderPrice(String ladderPrice) {
        this.ladderPrice = ladderPrice;
    }
}
