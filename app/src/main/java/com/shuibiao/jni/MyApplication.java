package com.shuibiao.jni;


public class MyApplication {

    static JNIInterface jniInterface;


    public MyApplication() {
        jniInterface = new JNIInterface();
    }

    public JNIInterface getJniInterface() {
        return jniInterface;
    }


    private static class SingletonHolder {
        private final static MyApplication instance = new MyApplication();
    }

    public static MyApplication getInstance() {
        return SingletonHolder.instance;
    }

}
