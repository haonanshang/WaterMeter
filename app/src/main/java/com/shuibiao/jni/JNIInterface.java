package com.shuibiao.jni;

public class JNIInterface {
    public long mhandler;

    static {
        System.loadLibrary("watermeter");
    }

    public JNIInterface() {
        mhandler = init();
    }

    public long getMhandler() {
        return mhandler;
    }

    /**
     * init
     *
     * @return 句柄
     */
    public native synchronized long init();

    /**
     * @param handler     句柄
     * @param imgpath     图片路径
     * @param recogNumRes 水表识别结果
     * @param numScore    水表识别结果置信度
     * @param numsLen     水表识别结果的长度 4-6位
     * @return 2：num识别成功ID未检测到  -1：未检测到
     */
    public native synchronized int processImgPathNum(long handler, String imgpath, int[] recogNumRes, float[] numScore, int[] numsLen);

    /**
     * 释放句柄
     *
     * @param handler
     */
    public native synchronized void release(long handler);


}
