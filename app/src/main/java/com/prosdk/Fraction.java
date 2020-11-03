package com.prosdk;

/**
 * 分数类
 */
public class Fraction {
    private int num;     //分子
    private int denom;  //分母

    public Fraction(int num, int denom) {
        this.num = num;
        this.denom = denom;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getDenom() {
        return denom;
    }

    public void setDenom(int denom) {
        this.denom = denom;
    }
}
