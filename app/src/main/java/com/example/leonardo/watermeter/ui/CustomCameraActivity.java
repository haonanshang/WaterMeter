package com.example.leonardo.watermeter.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.FireHydrant.ui.FireHydrantDataShowActivity;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.utils.Camera2Preview;
import com.example.leonardo.watermeter.utils.RecordZoom;
import com.example.leonardo.watermeter.utils.RectFrameView;
import com.example.leonardo.watermeter.utils.SharedPreUtils;
import com.example.leonardo.watermeter.utils.VolumeManage;

import org.apache.log4j.varia.FallbackErrorHandler;

/**
 * Created by Administrator on 2018/1/20 0020.
 */

public class CustomCameraActivity extends Activity implements View.OnClickListener {
    private ImageView takephoto;
    private String imgFilePath = "";
    private Camera2Preview mPreview;
    private MediaPlayer mMedia;
    boolean isTakephoto = false;
    FrameLayout preview;
    private TextView zoomultiple, textSwitchon, textSwitchoff;
    private ImageView flashSwitch;
    private Handler handler = new Handler();
    private boolean IsVisible = true;
    private boolean IsExecute = false;
    boolean isFireHydrant = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        setContentView(R.layout.customcamera);
        InitBundle();
        InitViews();
        InitListenner();
        InitDatas();
    }

    private void InitDatas() {
        SetZoom(RecordZoom.getzoom(this));
        if (SharedPreUtils.getFlashStatus(this)) {
            textSwitchoff.setTextColor(Color.WHITE);
            textSwitchon.setTextColor(getResources().getColor(R.color.yellow));
            flashSwitch.setImageResource(R.drawable.flashon);
        } else {
            textSwitchon.setTextColor(Color.WHITE);
            textSwitchoff.setTextColor(getResources().getColor(R.color.yellow));
            flashSwitch.setImageResource(R.drawable.flashoff);
        }
    }

    private void InitBundle() {
        Bundle bundle = getIntent().getExtras();
        imgFilePath = bundle.getString("imgFilePath");
        isFireHydrant = bundle.getBoolean("isFireHydrant");

    }


    private void InitListenner() {
        takephoto.setOnClickListener(this);
        textSwitchon.setOnClickListener(this);
        textSwitchoff.setOnClickListener(this);
    }

    private void InitViews() {
        zoomultiple = (TextView) findViewById(R.id.zoommultiple);
        mPreview = new Camera2Preview(CustomCameraActivity.this, CustomCameraActivity.this, imgFilePath);
        preview = (FrameLayout) findViewById(R.id.customframelayout);
        preview.addView(mPreview, 0);
        takephoto = (ImageView) findViewById(R.id.takephoto);
        flashSwitch = (ImageView) findViewById(R.id.switchflash);
        textSwitchon = (TextView) findViewById(R.id.textSwitchOn);
        textSwitchoff = (TextView) findViewById(R.id.textSwitchOff);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takephoto:
                if (!isTakephoto) {
                    isTakephoto = true;
                    mMedia = MediaPlayer.create(CustomCameraActivity.this, R.raw.shutter);
                    mMedia.start();
                    mPreview.captureStillPicture();
                    takephoto.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.textSwitchOn:
                try {
                    textSwitchoff.setTextColor(Color.WHITE);
                    textSwitchon.setTextColor(getResources().getColor(R.color.yellow));
                    flashSwitch.setImageResource(R.drawable.flashon);
                    mPreview.SwitchFlash(true);
                    SharedPreUtils.setFlashStatus(true, CustomCameraActivity.this);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.textSwitchOff:
                try {
                    textSwitchon.setTextColor(Color.WHITE);
                    textSwitchoff.setTextColor(getResources().getColor(R.color.yellow));
                    flashSwitch.setImageResource(R.drawable.flashoff);
                    mPreview.SwitchFlash(false);
                    SharedPreUtils.setFlashStatus(false, CustomCameraActivity.this);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /*
     * 返回显示详情Activity
     */
    public void returnTaskShowActivity() {
        if (isFireHydrant) {
            Intent intent = new Intent(CustomCameraActivity.this, FireHydrantDataShowActivity.class);
            intent.putExtra("imageStr", imgFilePath);
            setResult(Activity.RESULT_OK, intent);
        } else {
            Intent intent = new Intent(CustomCameraActivity.this, TaskShowActivity.class);
            intent.putExtra("imageStr", imgFilePath);
            setResult(Activity.RESULT_OK, intent);
        }
        try {
            mPreview.SwitchFlash(false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        CustomCameraActivity.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (!isTakephoto) {
                    isTakephoto = true;
                    VolumeManage.SetVolume(this);
                    mMedia = MediaPlayer.create(CustomCameraActivity.this, R.raw.shutter);
                    mMedia.start();
                    mPreview.captureStillPicture();
                    takephoto.setVisibility(View.INVISIBLE);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void SetZoom(double zoom) {
        String strtext = String.format("%.1f", zoom);
        RecordZoom.Setzoom(this, strtext);
        zoomultiple.setText("x " + strtext);
    }

    /*
    在页面上显示矩形框
   */
    @SuppressLint("Range")
    public void SetRectFrame(Rect focusRect) {
        if (preview.getChildAt(2) != null) {
            preview.removeViewAt(2);
        }
        preview.addView(new RectFrameView(this, focusRect));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                IsExecute = true;
                if (preview.getChildAt(2) != null && IsVisible) {
                    preview.getChildAt(2).setVisibility(View.INVISIBLE);
                }
            }
        }, 2500);

    }

    /*
     显示曝光进度条
     */
    public void ShowExpousePregress(boolean bo, final int progress) {
        IsVisible = bo;
        final RectFrameView frameView = (RectFrameView) preview.getChildAt(2);
        switch (frameView.getVisibility()) {
            case View.VISIBLE:
                frameView.updateProgress(progress);
                break;
            case View.INVISIBLE:
                frameView.setVisibility(View.VISIBLE);
                break;
        }
    }

    //矩形框是否可见
    public void SetIsvisible(boolean bo) {
        IsVisible = bo;
        if (IsExecute) {
            IsExecute = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    IsExecute = true;
                    if (preview.getChildAt(2) != null && IsVisible) {
                        preview.getChildAt(2).setVisibility(View.INVISIBLE);
                    }
                }
            }, 2500);
        }
    }

    //矩形框是否存在
    public boolean IsFrameviewExist() {
        if (preview.getChildAt(2) != null) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMedia != null) {
            mMedia.release();
        }
    }
}
