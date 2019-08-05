package com;


import com.shuibiao.jni.JNIInterface;



public class MyApplication {
    static JNIInterface jniInterface;

    public MyApplication() {
        jniInterface = new JNIInterface();
    }

    public static MyApplication getInstance() {
        return SingletonHolder.instance;
    }

    public static JNIInterface getJniInterface() {
        return jniInterface;
    }



    public static void releaseJni() {
        if (jniInterface != null) {
            jniInterface.release(jniInterface.getMhandler());
        }
    }

    private static class SingletonHolder {
        private final static MyApplication instance = new MyApplication();

    }
}
