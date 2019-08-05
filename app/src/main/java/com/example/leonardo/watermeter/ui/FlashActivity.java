package com.example.leonardo.watermeter.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.MyApplication;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.global.GlobalData;
import com.example.leonardo.watermeter.utils.CustomCamerautils2;
import com.example.leonardo.watermeter.utils.DeleteImage;
import com.example.leonardo.watermeter.utils.SharedPreUtils;
import com.example.leonardo.watermeter.utils.PermissionsChecker;
import com.itgoyo.logtofilelibrary.LogToFileUtils;
import com.objecteye.author.AuthorCustomUtils;

/**
 * Created by Administrator on 2017/8/2 0002.
 */

public class FlashActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_flash);
        /**
         * 初始化JNI
         */
        if (AuthorCustomUtils.CheckLicence()) {
            MyApplication.getInstance().getJniInterface();
        }
        /**
         * 获取设备信息
         */
        GlobalData.Brand = android.os.Build.BRAND;
        LogToFileUtils.init(this);
        new SharedPreUtils().SetIp("106.14.33.55:8080", FlashActivity.this);
        mPermissionsChecker = new PermissionsChecker(this);
        if (!mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            DeleteImage.Delete();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(FlashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            FlashActivity.this.finish();
                        }
                    });
                }
            }.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }


    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            Toast.makeText(FlashActivity.this, "请到设置页面开启相关权限", Toast.LENGTH_SHORT).show();
            FlashActivity.this.finish();
        } else {
            Intent intent = new Intent(FlashActivity.this, LoginActivity.class);
            startActivity(intent);
            FlashActivity.this.finish();
        }
    }
}
