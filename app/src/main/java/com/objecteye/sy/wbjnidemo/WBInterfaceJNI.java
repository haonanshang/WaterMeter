package com.objecteye.sy.wbjnidemo;

public class WBInterfaceJNI {

    static {
        System.loadLibrary("native-wb");
    }

    /**
     * 获取单帧数据
     *
     * @return
     */
    public synchronized native byte[] getFrameData();

    /**
     * 获取小盒子状态
     *
     * @return
     */
    public synchronized native boolean getStatus();
}
