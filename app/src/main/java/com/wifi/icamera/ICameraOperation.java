package com.wifi.icamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.leonardo.watermeter.ui.WifiActivity;
import com.itgoyo.logtofilelibrary.LogToFileUtils;
import com.wifi.config.PathConfig;

import java.io.File;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;


public class ICameraOperation {
    private PathConfig mPathConfig;
    private SurfaceView mSurfaceView;
    private Thread captureThread;
    private boolean isStop = false;
    private boolean need_take_photo = false;
    private String imagePath;
    private boolean is_first_enter = true;
    private WifiActivity mActivity;
    private DatagramSocket socket = null;
    private String BSSID = "192.168.10.123";

    static {
        System.loadLibrary("openal");
        System.loadLibrary("icamera");
    }

    public ICameraOperation(WifiActivity activity) {
        this.mActivity = activity;
        iCameraInitSocket();
        createSocket(54098);
    }

    public void setParams(SurfaceView mSurfaceView, PathConfig pathConfig) {
        this.mSurfaceView = mSurfaceView;
        this.mPathConfig = pathConfig;
    }

    public void startCapture() {
        if (captureThread == null || !captureThread.isAlive()) {
            captureThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    iCameraServerStart();
                    isStop = false;
                    while (!isStop) {
                        byte[] data = iCameraGetFrame();
                        if (data == null || data.length <= 0) {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            continue;
                        }
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bmp != null) {
                            if (is_first_enter) {
                                is_first_enter = false;
                            }
                            mSurfaceView.SetBitmap(bmp);
                            if (need_take_photo) {
                                need_take_photo = false;
                                mPathConfig.savePhoto(imagePath, data);
                                stopCapture();
                            }
                        }
                    }
                    isStop = true;
                }
            });
            captureThread.start();
        }
    }


    public void createSocket(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void takePhoto(String imageStr) {
        if (mPathConfig.getSdcardAvilibleSize() > 100) {
            imagePath = imageStr;
            need_take_photo = true;
        }
    }

    /*
     *销毁类
     */
    public void Destroy() {
        iCameraServerStop();
        need_take_photo = false;
        isStop = true;
        if (socket != null) {
            if (!socket.isClosed()) {
                socket.close();
            }
        }
    }

    /*
     *停止预览
     */
    public void stopCapture() {
        Map resultMap = new HashMap();
        boolean isExit = false;
        File localFile = new File(imagePath);
        if (localFile.exists() && localFile.length() > 0) {
            isExit = true;
        } else {
            localFile.delete();
        }
        resultMap.put("isExist", isExit);
        resultMap.put("imageStr", imagePath);
        mActivity.FinishWifiActivity(resultMap);
    }


    // video stream functions
    public native byte[] iCameraGetFrame();

    //public native void iCameraServerStart();
    public native int iCameraServerStart();

    public native void iCameraServerStop();

    // record functions
    public native void iCameraRecStart(String fileAbsPath);

    public native void iCameraRecStop();

    public native void iCameraRecSetParams(int width, int height, int frameRate);

    public native void iCameraRecInsertData(byte[] data, int size);

    // video player functions
    public native void iCameraOpenFile(String path);

    public native void iCameraCloseFile();

    public native void iCameraOpenVoice();

    public native void iCameraCloseVoice();

    public native void iCameraWriteData(byte[] b);

    public native double iCameraGetTotalTime();

    public native int iCameraGetTotalFrame();

    public native byte[] iCameraGetOneFrame(int frame);

    public native byte[] iCameraGetOneSecond(double time);

    public native byte[] iCameraGetVoice(double time);

    // command only for woddon
    public native void iCameraInsertCmdData(byte[] data, int length, int times, byte cmd);

    public native byte[] iCameraGetCmdData();

    // normal command

    public native int iCameraInitSocket();

    public native int iCameraCloseSocket();

    public native int sendChangeName(String wifiName);

    public native int sendChangePassword(String password);

    public native int sendClearPassword();

    public native int sendReboot();

    public native int sendChangeResolution(int width, int height, int frameRate);

    public native byte[] getResolution();

    public static native int getTakePhotoVideo();


}
