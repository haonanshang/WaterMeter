package com.blueToothPrinter;

/**
 * 蓝牙打印模块的字段值
 */
public class BluetoothValues {
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // 无任何连接状态
    public static final int STATE_CONNECTING = 2; // 正在连接设备
    public static final int STATE_CONNECTED = 3;  // 连接到远程设备
    public static final int STATE_CONNECTED_FAILURED = 4; //连接设备失败
    public static final int STATE_CONNECTED_INTERRUPT = 5;//设备连接中断
    public static final int STATE_CONNECTED_FINISH = 6;//设备正常退出

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 6;
    public static final int REQUEST_ENABLE_BT = 7;
    public static final String CHINESE = "GBK";


}
