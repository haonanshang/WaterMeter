package com.example.leonardo.watermeter.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.FireHydrant.ui.FireHydrantDataShowActivity;
import com.example.leonardo.watermeter.R;
import com.wifi.config.PathConfig;
import com.wifi.icamera.ICameraOperation;
import com.wifi.icamera.SurfaceView;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/12 0012.
 */

public class WifiActivity extends AppCompatActivity {
    private com.wifi.icamera.SurfaceView mSurfaceView;
    private ICameraOperation iCameraOperation;
    private ImageView iv_Main_TakePhoto;
    private PathConfig mPathConfig;
    private ImageView cancelphoto;
    private MediaPlayer mMedia;
    private long mKeyTime;
    private String imagePath;
    private boolean isFireHydrant = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//取消状态栏
        setContentView(R.layout.main_wifi);
        Bundle bundle = this.getIntent().getExtras();
        imagePath = bundle.getString("imagePath");
        isFireHydrant = bundle.getBoolean("isFireHydrant");
        mPathConfig = new PathConfig(WifiActivity.this);
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        iCameraOperation = new ICameraOperation(this);
        iCameraOperation.setParams(mSurfaceView, mPathConfig);
        widgetInit();
    }

    private void widgetInit() {
        mSurfaceView.setOnClickListener(clickListener);
        iv_Main_TakePhoto = (ImageView) findViewById(R.id.iv_Main_TakePhoto);
        cancelphoto = (ImageView) findViewById(R.id.iv_Main_cancel);
        iv_Main_TakePhoto.setOnClickListener(clickListener);
        cancelphoto.setOnClickListener(clickListener);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        iCameraOperation.startCapture();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.iv_Main_TakePhoto:
                    mMedia = MediaPlayer.create(WifiActivity.this, R.raw.shutter);
                    mMedia.start();
                    iCameraOperation.takePhoto(imagePath);
                    iv_Main_TakePhoto.setVisibility(View.INVISIBLE);
                    cancelphoto.setVisibility(View.VISIBLE);
                    break;
                case R.id.iv_Main_cancel:
                    iCameraOperation.startCapture();
                    iv_Main_TakePhoto.setVisibility(View.VISIBLE);
                    cancelphoto.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    /*
     *销毁activity
     * @param 返回的map参数
     */
    public void FinishWifiActivity(Map map) {
        boolean isExist = (boolean) map.get("isExist");
        String imageUrl = null;
        if (isExist) {
            imageUrl = (String) map.get("imageStr");
            Log.e("zksy", "WifiActivity收到的imageUrl为：" + imageUrl);
        }
        if (isFireHydrant) {
            Intent intent = new Intent(this, FireHydrantDataShowActivity.class);
            intent.putExtra("isExist", isExist);
            intent.putExtra("imageStr", imageUrl);
            setResult(1, intent);
        } else {
            Intent intent = new Intent(this, TaskShowActivity.class);
            intent.putExtra("isExist", isExist);
            intent.putExtra("imageStr", imageUrl);
            setResult(1, intent);
        }
        this.finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mKeyTime) > 2000) {
                mKeyTime = System.currentTimeMillis();
                Toast.makeText(this, R.string.txt_select_onemorepress, Toast.LENGTH_SHORT).show();
            } else {
                WifiActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        iCameraOperation.Destroy();
        if (mMedia != null) {
            mMedia.pause();
            mMedia.release();
        }
    }
}
