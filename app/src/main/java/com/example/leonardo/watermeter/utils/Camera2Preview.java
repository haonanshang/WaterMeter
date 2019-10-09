package com.example.leonardo.watermeter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.example.leonardo.watermeter.ui.CustomCameraActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Administrator on 2018/2/28 0028.
 */
public class Camera2Preview extends TextureView implements TextureView.SurfaceTextureListener {
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private CustomCameraActivity activity;
    private Context context;
    private Size previewSize;
    private boolean isflash = false;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private SurfaceTexture msurface;
    private CameraManager mCameraManager;
    private String mCameraId = "0";//摄像头ID（通常0代表后置摄像头，1代表前置摄像头）
    private CaptureRequest previewRequest;
    private CameraCaptureSession captureSession;  // 定义CameraCaptureSession成员变量
    private ImageReader imageReader;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder previewRequestBuilder;
    private CameraCharacteristics mCameraCharacteristics;
    String imagePath;
    private int lenth, screenWidth, screenHeight;
    double lastzoom = 0.0f;
    //int maxRealRadio;
    float y = 0;
    boolean FingerIsMove = false;
    int AE_COMPENSATION_RANGE = 0;
    Rect maxZoomrect, captureRect;
    Range aeRange;//曝光范围

    public Camera2Preview(Context context, CustomCameraActivity activity, String imagePath) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.imagePath = imagePath;
        this.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        msurface = surface;
        lastzoom = RecordZoom.getzoom(context);
        screenWidth = width;
        screenHeight = height;
        openCamera(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    // 打开摄像头
    @SuppressLint("MissingPermission")
    private void openCamera(int width, int height) {
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            //画面传感器的面积，单位是像素。
            maxZoomrect = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            aeRange = mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
            captureRect = new Rect(CustomCamerautils2.getLocalRect(maxZoomrect, lastzoom));
            //最大的数字缩放
            //maxRealRadio = mCameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM).intValue();
            setUpCameraOutputs(width, height);
            // 打开摄像头
            mCameraManager.openCamera(mCameraId, stateCallback, null); // ①
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCameraOutputs(int width, int height) {
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId); // 获取指定摄像头的特性
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP); // 获取摄像头支持的配置属性
            previewSize = CustomCamerautils2.chooseMaxSize(map.getOutputSizes(SurfaceTexture.class), width, height);
            Size imageviewSize = CustomCamerautils2.chooseMinSize(map.getOutputSizes(ImageFormat.JPEG), width, height);
            imageReader = ImageReader.newInstance(imageviewSize.getWidth(), imageviewSize.getHeight(), ImageFormat.JPEG, 1);
            imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireNextImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    File file = new File(imagePath);
                    buffer.get(bytes);
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            output.flush();
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        image.close();
                        activity.returnTaskShowActivity();
                    }
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("出现错误。");
        }

    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        // 摄像头被打开时激发该方法
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();  // ② 开始预览
        }

        // 摄像头断开连接时激发该方法
        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            if (cameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        // 打开摄像头出现错误时激发该方法
        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            if (cameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            super.onClosed(camera);
        }
    };

    private void createCameraPreviewSession() {
        try {
            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            msurface.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            previewRequestBuilder.addTarget(new Surface(msurface));
            mCameraDevice.createCaptureSession(Arrays.asList(new Surface(msurface), imageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (mCameraDevice == null) {
                        return;
                    }
                    captureSession = cameraCaptureSession;
                    try {
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                        previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, captureRect);
                        previewRequest = previewRequestBuilder.build();
                        captureSession.setRepeatingRequest(previewRequest, null, null);  // ④
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    try {
                        SwitchFlash(SharedPreUtils.getFlashStatus(context));
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(context, "配置失败！", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /*
   拍照
   */
    public void captureStillPicture() {
        try {
            if (mCameraDevice == null) {
                return;
            }
            final CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            if (isflash) {
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            }
            captureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, captureRect);
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            captureSession.stopRepeating();
            captureSession.capture(captureRequestBuilder.build(), new CameraCaptureSession.CaptureCallback()  // ⑤
            {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y = event.getY();
                    FingerIsMove = false;
                    break;
                case MotionEvent.ACTION_UP:
                    if (!FingerIsMove) {
                        HandFocusRect(event);
                    } else {
                        activity.SetIsvisible(true);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float y1 = event.getY();
                    if (Math.abs((y - y1)) > 10) {
                        FingerIsMove = true;
                        if (activity.IsFrameviewExist()) {
                            if ((y - y1) > 0 && AE_COMPENSATION_RANGE <= Integer.valueOf(aeRange.getUpper().toString()) * 10) {
                                activity.ShowExpousePregress(false, AE_COMPENSATION_RANGE++);
                                SetAEDeep((AE_COMPENSATION_RANGE / 10));
                            } else if ((y - y1) < 0 && AE_COMPENSATION_RANGE >= Integer.valueOf(aeRange.getLower().toString()) * 10) {
                                activity.ShowExpousePregress(false, AE_COMPENSATION_RANGE--);
                                SetAEDeep(AE_COMPENSATION_RANGE / 10);
                            }
                        }
                    }
                    break;
            }
        } else if (event.getPointerCount() == 2) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    lenth = CustomCamerautils2.getFingerSpacing(event);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    float newlenth = CustomCamerautils2.getFingerSpacing(event);
                    if (newlenth - lenth > 30) {
                        CustomCamerautils2.getRectZoom(lastzoom, maxZoomrect, this, true);
                    } else if (lenth - newlenth > 30) {
                        CustomCamerautils2.getRectZoom(lastzoom, maxZoomrect, this, false);
                    }
                    break;
            }
        }
        return true;
    }

    /*
     二指缩放
     */
    public void SetPreviewRect(Rect rect, Double zoom) {
        lastzoom = zoom;
        captureRect = rect;
        previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, captureRect);
        activity.SetZoom(lastzoom);
        try {
            captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /*
    控制手电筒
     */
    public void SwitchFlash(boolean bo) throws CameraAccessException {
        isflash = bo;
        if (isflash) {
            previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
        } else {
            previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
        }
        captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null);
    }

    /*
     触摸定焦
   */
    public void HandFocusRect(MotionEvent event) {
        try {
            if (captureSession != null) {
                captureSession.stopRepeating();
                Rect focusRect = CustomCamerautils2.calculateTapArea(event.getX(), event.getY(), 1f, screenWidth, screenHeight);
                activity.SetRectFrame(focusRect);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                MeteringRectangle[] totalMeteringRectangle = new MeteringRectangle[1];
                totalMeteringRectangle[0] = new MeteringRectangle(focusRect, 600);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, totalMeteringRectangle);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, totalMeteringRectangle);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
                captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     /*
     调节曝光值
      */

    public void SetAEDeep(int exposureDeep) {
        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, exposureDeep);
        try {
            captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void closeCamera(){
         if(mCameraDevice!=null){
             mCameraDevice.close();
         }
    }
}

