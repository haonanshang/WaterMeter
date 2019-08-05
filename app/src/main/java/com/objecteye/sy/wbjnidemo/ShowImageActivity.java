package com.objecteye.sy.wbjnidemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.leonardo.watermeter.R;

public class ShowImageActivity extends AppCompatActivity {
    private ImageView showImage;
    private Button takePhotoBT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showimage_activity);
        showImage = findViewById(R.id.showImage);
        takePhotoBT = findViewById(R.id.takePhoto);
        takePhotoBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowImageActivity.this, WBJNIActivity.class);
                startActivityForResult(intent, 101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 101:
                Log.e("WBdemo", "返回的resultCode为：" + resultCode);
                if (resultCode == 1) {
                    String localImageStr = data.getExtras().getString("imageStr");
                    Log.e("WBdemo", "返回图片路径为：" + localImageStr);
                    if (localImageStr != null && !localImageStr.equals("")) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localImageStr);
                        showImage.setImageBitmap(bitmap);
                    }
                }
                break;
            default:
                break;
        }
    }


}
