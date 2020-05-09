package com.prosdk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.hyfisheyepano.GLFisheyeView;
import com.example.leonardo.watermeter.R;
import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.ILoginDeviceCallback;
import com.macrovideo.sdk.media.LibContext;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.LoginHelper;
import com.macrovideo.sdk.media.NVPanoPlayer;
import com.macrovideo.sdk.objects.DeviceInfo;
import com.macrovideo.sdk.objects.LoginParam;
import com.macrovideo.sdk.tools.Functions;
import com.objecteye.author.AuthorApplication;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NVPlayerShowActivity extends Activity {

    private LoginHandle deviceParam = null;
    private LinearLayout coniaterLinearLayout = null;
    private NVPanoPlayer mNVPanoPlayer = null;
    private ImageView ivScreenshot = null;
    private boolean isOnlyShow = false;   //是否仅仅实时预览
    private String saveImagePath;
    private Bundle bundleData = null;     //页面传递数据
    private static final int HANDLE_MSG_CODE_LOGIN_RESULT = 0x10;
    private static final int INTENT_BINDING_RESULT = 0x00;
    private static final int INTENT_TASK_SHOW_RESULT = 0x01;
    private int screenWidth;
    private int screenHeight;
    private LinearLayout linearLayoutScreenshot = null;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == HANDLE_MSG_CODE_LOGIN_RESULT) {
                //progress.setVisibility(View.GONE);
                switch (msg.arg2) {
                    case ResultCode.RESULT_CODE_SUCCESS:
                        Bundle bundle = msg.getData();
                        deviceParam = bundle.getParcelable("device_param");
                        initViews();
                        initListenners();
                        ConnectGLViewToPlayer();
                        InitGLViewPlayer();
                        displayFrames();
                        break;
                    case ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL:
                        ShowAlert(getString(R.string.alert_title_login_failed) + "  (" + getString(R.string.notice_Result_BadResult) + ")", getString(R.string.alert_connect_tips));
                        break;
                    case ResultCode.RESULT_CODE_FAIL_VERIFY_FAILED:
                        ShowAlert(getString(R.string.alert_title_login_failed), getString(R.string.notice_Result_VerifyFailed));
                        break;
                    case ResultCode.RESULT_CODE_FAIL_USER_NOEXIST:
                        //progress.setVisibility(View.GONE);
                        ShowAlert(getString(R.string.alert_title_login_failed), getString(R.string.notice_Result_UserNoExist));
                        break;
                    case ResultCode.RESULT_CODE_FAIL_PWD_ERROR:
                        ShowAlert(getString(R.string.alert_title_login_failed), getString(R.string.notice_Result_PWDError));
                        break;
                    case ResultCode.RESULT_CODE_FAIL_OLD_VERSON:
                        ShowAlert(getString(R.string.alert_title_login_failed), getString(R.string.notice_Result_Old_Version));
                        break;
                    default:
                        ShowAlert(getString(R.string.alert_title_login_failed) + "  (" + getString(R.string.notice_Result_ConnectServerFailed) + ")", "");
                        break;

                }
            } else if (msg.arg1 == INTENT_BINDING_RESULT) {
                deviceParam = bundleData.getParcelable("device_param");
                initViews();
                initListenners();
                ConnectGLViewToPlayer();
                InitGLViewPlayer();
                displayFrames();
            } else if (msg.arg1 == INTENT_TASK_SHOW_RESULT) {
                saveImagePath = bundleData.getString("imagePath");
                loginDevice();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        initScreen();
        initDatas();
        setContentView(R.layout.activity_nvplayer_show);
    }


    /**
     * 读取屏幕尺寸比例
     */
    private void initScreen() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        screenWidth = point.x;
        screenHeight = point.y;
    }

    @Override
    protected void onDestroy() {
        LibContext.ClearContext();
        if (mNVPanoPlayer != null) {
            mNVPanoPlayer.stopPlay();
            mNVPanoPlayer.getGLFisheyeView().clean();
            mNVPanoPlayer.release();
            mNVPanoPlayer = null;
            coniaterLinearLayout.removeAllViews();
            coniaterLinearLayout = null;
        }
        super.onDestroy();
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        //保持屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //接收上一个页面传递的数据
        bundleData = this.getIntent().getExtras();
        isOnlyShow = bundleData.getBoolean("isOnlyShow");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //区分跳转页面
        if (isOnlyShow) {
            Message msg = handler.obtainMessage();
            msg.arg1 = INTENT_BINDING_RESULT;
            handler.sendMessage(msg);
        } else {
            Message msg = handler.obtainMessage();
            msg.arg1 = INTENT_TASK_SHOW_RESULT;
            handler.sendMessage(msg);
        }
    }

    /**
     * 加载控件
     */
    private void initViews() {
        coniaterLinearLayout = findViewById(R.id.activity_nvplayer_show_container);
        linearLayoutScreenshot = findViewById(R.id.LinearLayout_screenshot);
        ivScreenshot = findViewById(R.id.activity_nvplayer_screenshot);
    }

    /**
     * 加载控件监听
     */
    private void initListenners() {
        if (!isOnlyShow) {
            ivScreenshot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap saveBitmap = mNVPanoPlayer.screenShot();
                    if (saveBitmap != null) {
                        stopDisplay();
                        if (saveToSDCard(saveBitmap, saveImagePath)) {                                                                                                                                                                                                                                                                                // 保存到指定文件夹 add by mai 2015-4-9
                            Toast.makeText(AuthorApplication.getContext(), "拍照成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("imageStr", saveImagePath);
                            NVPlayerShowActivity.this.setResult(LocalDefines.RESULT_SUCCESSFULE, intent);
                            NVPlayerShowActivity.this.finish();
                        } else {
                            Toast.makeText(AuthorApplication.getContext(), "拍照失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AuthorApplication.getContext(), "拍照失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            ivScreenshot.setClickable(false);
            ivScreenshot.setVisibility(View.INVISIBLE);
            linearLayoutScreenshot.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 停止预览
     */
    private void stopDisplay() {
        if (mNVPanoPlayer != null) {
            mNVPanoPlayer.getGLFisheyeView().onPause();
        }
        LibContext.stopAll();
    }

    /**
     * 展示视频
     */
    private void displayFrames() {
        LibContext.SetContext(mNVPanoPlayer, null, null, null);
        mNVPanoPlayer.setnServerID(deviceParam.getnDeviceID());
        mNVPanoPlayer.EnableRender();
        mNVPanoPlayer.startPlay(0, 0, LocalDefines.getDeviceDefinition(AuthorApplication.getContext()), false, deviceParam);
        mNVPanoPlayer.setReverse(false);// add by
        mNVPanoPlayer.playAudio();
    }

    /**
     * 添加 OpenGL ES2.0表面到容器
     */
    private void ConnectGLViewToPlayer() {
        if (mNVPanoPlayer == null) {
            mNVPanoPlayer = new NVPanoPlayer(this, false);
            mNVPanoPlayer.setHWDecodeStatus(false, false);
            GLFisheyeView fisheyeView = new GLFisheyeView(this);
            fisheyeView.setMode(NVPanoPlayer.PANO_PLAY_MODE_13);
            mNVPanoPlayer.setGlFishView(fisheyeView);
            mNVPanoPlayer.setCamImageOrientation(Configuration.ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 竖屏设置
     */
    private void orientationPortraitSetting() {
        LinearLayout linearLayout = new LinearLayout(this);
        int layoutParamWidth = screenWidth;
        int layoutParamHeight = 0;
        //区分设备清晰度
        switch (LocalDefines.getDeviceDefinition(AuthorApplication.getContext())) {
            case LocalDefines.IMAGE_STANDARD_DEFINITION: //标清
                layoutParamHeight = new Float(layoutParamWidth * getConsult(LocalDefines.SD_Aspect_Ratio.getDenom(), LocalDefines.SD_Aspect_Ratio.getNum())).intValue();
                break;
            case LocalDefines.IMAGE_HIGH_DEFINITION://高清
                layoutParamHeight = new Float(layoutParamWidth * getConsult(LocalDefines.HD_Aspect_Ratio.getDenom(), LocalDefines.HD_Aspect_Ratio.getNum())).intValue();
                break;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(layoutParamWidth, layoutParamHeight);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(mNVPanoPlayer.getGLFisheyeView(), layoutParams);
        coniaterLinearLayout.addView(linearLayout);
        //coniaterLinearLayout.setGravity(Gravity.CENTER);
    }

    /**
     * 横屏设置
     */
    private void orientationLandscapeSetting() {
        LinearLayout linearLayout = new LinearLayout(this);
        int layoutParamWidth = screenWidth;
        int layoutParamHeight = 0;
        //区分设备清晰度
        switch (LocalDefines.getDeviceDefinition(AuthorApplication.getContext())) {
            case LocalDefines.IMAGE_STANDARD_DEFINITION: //标清
                if (getConsult(layoutParamWidth, screenHeight) <= getConsult(LocalDefines.SD_Aspect_Ratio.getNum(), LocalDefines.SD_Aspect_Ratio.getDenom())) {
                    layoutParamHeight = new Float(layoutParamWidth * getConsult(LocalDefines.SD_Aspect_Ratio.getDenom(), LocalDefines.SD_Aspect_Ratio.getNum())).intValue();
                } else {
                    layoutParamWidth = new Float(screenHeight * getConsult(LocalDefines.SD_Aspect_Ratio.getNum(), LocalDefines.SD_Aspect_Ratio.getDenom())).intValue();
                    layoutParamHeight = screenHeight;
                }
                break;
            case LocalDefines.IMAGE_HIGH_DEFINITION://高清
                if (getConsult(layoutParamWidth, screenHeight) <= getConsult(LocalDefines.HD_Aspect_Ratio.getNum(), LocalDefines.HD_Aspect_Ratio.getDenom())) {
                    layoutParamHeight = new Float(layoutParamWidth * getConsult(LocalDefines.HD_Aspect_Ratio.getDenom(), LocalDefines.HD_Aspect_Ratio.getNum())).intValue();
                } else {
                    layoutParamWidth = new Float(screenHeight * getConsult(LocalDefines.HD_Aspect_Ratio.getNum(), LocalDefines.HD_Aspect_Ratio.getDenom())).intValue();
                    layoutParamHeight = screenHeight;
                }
                break;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(layoutParamWidth, layoutParamHeight);
        linearLayout.setGravity(Gravity.RIGHT);
        linearLayout.addView(mNVPanoPlayer.getGLFisheyeView(), layoutParams);
        coniaterLinearLayout.addView(linearLayout);
    }


    /**
     * 创建OpenGl表面，并设置渲染模式为请求渲染
     */
    private void InitGLViewPlayer() {
        orientationPortraitSetting();
    }

    /**
     * @param num
     * @param demon
     * @return
     */
    public float getConsult(int num, int demon) {
        return (float) num / demon;
    }

    /**
     * @param image
     * @param imagePath
     * @return
     */
    private boolean saveToSDCard(Bitmap image, String imagePath) {
        boolean bResult = false;
        if (image == null)
            return bResult;
        try {
            File imageFile = new File(imagePath);
            FileOutputStream out = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            bResult = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bResult;
    }

    /**
     * 设备登录，绑定wifi设备
     */
    private void loginDevice() {
        DeviceInfo deviceInfo = LocalDefines.getDeviceInfoFromSP(AuthorApplication.getContext());
        LoginParam loginParam = new LoginParam(deviceInfo, Defines.LOGIN_FOR_PLAY);
        int loginResult = LoginHelper.loginDevice(AuthorApplication.getContext(), loginParam, new ILoginDeviceCallback() {
            @Override
            public void onLogin(LoginHandle loginHandle) {
                if (loginHandle != null && loginHandle.getnResult() == ResultCode.RESULT_CODE_SUCCESS) {
                    // 登录成功
                    Message msg = handler.obtainMessage();
                    msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                    msg.arg2 = ResultCode.RESULT_CODE_SUCCESS;
                    Bundle data = new Bundle();
                    data.putParcelable("device_param", loginHandle);
                    msg.setData(data);
                    handler.sendMessage(msg);
                } else if (loginHandle != null) {
                    // 登录失败，可查看具体错误码
                    Message msg = handler.obtainMessage();
                    msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                    msg.arg2 = loginHandle.getnResult();
                    handler.sendMessage(msg);
                } else {
                    // 登录失败
                    Message msg = handler.obtainMessage();
                    msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                    msg.arg2 = ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL;
                    handler.sendMessage(msg);
                }
            }
        });

        if (loginResult != 0) {
            // 登录失败，参数错误等
            Message msg = handler.obtainMessage();
            msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
            msg.arg2 = ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL;
            handler.sendMessage(msg);
        }
    }

    /**
     * 自定义dialog
     *
     * @param title
     * @param msg
     */
    private void ShowAlert(String title, String msg) {
        try {
            new AlertDialog.Builder(NVPlayerShowActivity.this).setTitle(title)
                    .setMessage(msg)
                    .setIcon(R.drawable.ic_watermeter_launcher)
                    .setPositiveButton(getString(R.string.alert_btn_OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(RESULT_OK);
                        }
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
