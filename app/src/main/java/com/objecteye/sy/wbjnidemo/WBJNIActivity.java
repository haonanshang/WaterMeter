package com.objecteye.sy.wbjnidemo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.FireHydrant.ui.FireHydrantDataShowActivity;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.ui.TaskShowActivity;;

import java.util.Map;



public class WBJNIActivity extends AppCompatActivity {
    private com.objecteye.sy.wbjnidemo.SurfaceView mSurfaceView;
    private WBInterfaceJNI wbInterfaceJNI;
    private ICameraOperation mICameraOperation;
    private ImageView takeImage;
    private MediaPlayer mMedia;
    String imagePath;
    boolean isFireHydrant = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_new_device_wifi);
        Bundle bundle = this.getIntent().getExtras();
        imagePath = bundle.getString("imagePath");
        isFireHydrant = bundle.getBoolean("isFireHydrant");
        initViews();
        boolean status = checkBoxStatus();
        Log.i("status", status + "");
        if (status) {
            mICameraOperation.startCapture();
        } else {
            Toast.makeText(this, "请检查摄像头是否正确连接", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 判断盒子的连接状态
     *
     * @return
     */
    public boolean checkBoxStatus() {
        boolean checkresult = false;
        for (int i = 0; i < 3; i++) {
            if (WBJNIUtils.getStatus()) {
                checkresult = true;
                break;
            }
            checkresult = false;
        }
        return checkresult;
    }

    /**
     * 初始化
     */
    private void initViews() {
        mSurfaceView = findViewById(R.id.mSurfaceView);
        takeImage = findViewById(R.id.iv_Main_TakePhoto);
        mICameraOperation = new ICameraOperation();
        wbInterfaceJNI = new WBInterfaceJNI();
        mICameraOperation.setParams(mSurfaceView, wbInterfaceJNI, this);
        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMedia = MediaPlayer.create(WBJNIActivity.this, R.raw.shutter);
                mMedia.start();
                mICameraOperation.takePicture(imagePath);
            }
        });
    }

    /**
     * 返回结果到页面详情界面
     *
     * @param map
     */
    public void rebackShowActivity(Map map) {
        boolean isExist = (boolean) map.get("isExist");
        String imageUrl = null;
        if (isExist) {
            imageUrl = (String) map.get("imageStr");
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
    protected void onDestroy() {
        super.onDestroy();
        if (mMedia != null) {
            mMedia.pause();
            mMedia.release();
        }
    }
}

