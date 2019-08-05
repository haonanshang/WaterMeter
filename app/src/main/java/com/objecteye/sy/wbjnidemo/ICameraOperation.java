package com.objecteye.sy.wbjnidemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.example.leonardo.watermeter.utils.ModifyImage;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ICameraOperation {
    private SurfaceView mSurfaceView;
    private boolean isTakePicture = false;
    private String imagePath;
    private WBInterfaceJNI mWBInterfaceJNI;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private boolean isCapture = false;
    private WBJNIActivity mWBJNIActivity;

    public ICameraOperation() {

    }

    public void setParams(SurfaceView mSurfaceView, WBInterfaceJNI wbInterface, WBJNIActivity activity) {
        this.mSurfaceView = mSurfaceView;
        this.mWBInterfaceJNI = wbInterface;
        this.mWBJNIActivity = activity;
    }

    /**
     * 开始预览
     */
    public void startCapture() {
        singleThreadExecutor.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                isCapture = true;
                while (isCapture) {
                    byte[] frameData = mWBInterfaceJNI.getFrameData();
                    if (frameData != null && frameData.length > 0) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(frameData, 0, frameData.length);
                        bmp = ModifyImage.rotateBitmapByDegree(bmp, 90);
                        if (bmp != null) {
                            mSurfaceView.SetBitmap(bmp);
                        }
                        if (isTakePicture) {
                            if (!imagePath.equals("") && imagePath != null) {
                                Map resultMap = new HashMap();
                                resultMap.put("imageStr", imagePath);
                                boolean isExist = savePhoto(imagePath, bmp);
                                isCapture = false;
                                restoreParams();
                                resultMap.put("isExist", isExist);
                                mWBJNIActivity.rebackShowActivity(resultMap);
                            }
                        }
                    }
                }
            }
        });
        singleThreadExecutor.shutdown();
    }

    /**
     * 恢复默认参数
     */
    private void restoreParams() {
        isCapture = false;
        isTakePicture = false;
    }

    /**
     * 保存图片到指定路径
     *
     * @param path
     */
    public void takePicture(String path) {
        isTakePicture = true;
        imagePath = path;
    }


    /**
     * 保存图片到指定路径
     *
     * @param imagePath
     * @param bitmap
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean savePhoto(String imagePath, Bitmap bitmap) {
        try {
            File filePic = new File(imagePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return new File(imagePath).exists();
    }

}
