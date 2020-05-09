package com.FireHydrant.ui;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.FireHydrant.entity.FireHydrantDetailData;
import com.FireHydrant.utils.FireHydrantDialog;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.global.GlobalData;
import com.example.leonardo.watermeter.ui.CustomCameraActivity;
import com.example.leonardo.watermeter.ui.WifiActivity;
import com.example.leonardo.watermeter.utils.GPSUtils;
import com.example.leonardo.watermeter.utils.LocationTools;
import com.example.leonardo.watermeter.utils.ModifyImage;


import org.litepal.LitePal;

import java.io.File;
import java.text.SimpleDateFormat;

public class FireHydrantDataShowActivity extends Activity {
    private TextView fireHydrantName, fireHydrantAdress, fireHydrantLongitude, fireHydrantLatitude;
    private String fire_hydrant_id;//消防栓ID
    private int ImagePathIndex, isUpload;//图片下标
    private FireHydrantDetailData currentData;
    private String imagePath, imgNameHead, currentImgFilePath, imgNameNoPotfix;
    private ImageView fireHydrantImage;
    private ModifyImage modifyImage = new ModifyImage();
    private TextView manualText, lastValueText, currentValueText, currentMeterWaterSumText;
    private String Fire_IvaWaterMeter_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FireIvaWaterMeter";
    private String Fire_IvaWater_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FireIvaWater";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_firehydrantdatashow);
        Bundle bundle = this.getIntent().getExtras();
        fire_hydrant_id = bundle.getString("fire_hydrant_id");
        ImagePathIndex = bundle.getInt("fire_hydrant_imageindex");
        initViews();
        initDatas();
    }

    /*
     *检查本地是否有图片
     */
    private void CheckPhoto() {
        if (imagePath != null && !imagePath.equals("")) {
            File picFile = new File(imagePath);
            Bitmap bitmap;
            if (picFile.exists()) {
                bitmap = BitmapFactory.decodeFile(imagePath);
                fireHydrantImage.setImageBitmap(bitmap);
            } else {
                fireHydrantImage.setImageResource(R.mipmap.ic_default);
            }
        } else {
            fireHydrantImage.setImageResource(R.drawable.show_img_d);
        }

    }

    /*
     * 初始化UI界面 以及相应数据
     */
    public void initDatas() {
        currentData = LitePal.where("fire_hydrant_id= ?", fire_hydrant_id).findFirst(FireHydrantDetailData.class);
        isUpload = Integer.valueOf(currentData.getIsUpload().trim());
        imgNameHead = currentData.getDetail_date() + "_" + currentData.getFire_hydrant_id() + "_" + currentData.getBooklet_no() + "_";
        fireHydrantName.setText(currentData.getFire_hydrant_name());
        fireHydrantAdress.setText(currentData.getAddress());
        fireHydrantLongitude.setText(currentData.getLongitude());
        fireHydrantLatitude.setText(currentData.getLatitude());
        lastValueText.setText(currentData.getLast_value());
        currentValueText.setText(currentData.getCurrent_value());
        currentMeterWaterSumText.setText(currentData.getCurrent_water_data());
        switch (ImagePathIndex) {//选择对应图片
            case 1:
                imagePath = currentData.getImagePathOne();
                break;
            case 2:
                imagePath = currentData.getImagePathTwo();
                manualText.setVisibility(View.VISIBLE);
                break;
        }
        CheckPhoto();
    }

    /*
     *  手抄数据——点击事件
     */
    public void manualInput(View view) {
        if (isUpload == 0) {
            Toast.makeText(getApplicationContext(), "数据已经上传，不可在手动修改", Toast.LENGTH_SHORT).show();
        } else {
            FireHydrantDialog dialog = new FireHydrantDialog(this, currentData);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    /*
     *初始化参数
     */
    private void initViews() {
        fireHydrantImage = (ImageView) findViewById(R.id.fireHydrantImage);
        fireHydrantName = (TextView) findViewById(R.id.fireHydrantName);
        fireHydrantAdress = (TextView) findViewById(R.id.fireHydrantAdress);
        fireHydrantLongitude = (TextView) findViewById(R.id.fireHydrantLongitude);
        fireHydrantLatitude = (TextView) findViewById(R.id.fireHydrantLatitude);
        manualText = (TextView) findViewById(R.id.manualButton);
        lastValueText = (TextView) findViewById(R.id.lastValue);
        currentValueText = (TextView) findViewById(R.id.currentValue);
        currentMeterWaterSumText = (TextView) findViewById(R.id.currentMeterWaterSum);
    }

    /*内窥镜拍照——点击事件*/
    public void takePhotoUSB(View view) {
        if (isUpload == 0) {
            //TODO 拍照设备可用
            Toast.makeText(getApplicationContext(), "数据已经上传，不可再手动修改", Toast.LENGTH_SHORT).show();
        } else {
            if (GPSUtils.GPSIsOPen(FireHydrantDataShowActivity.this)) {
                String imagePath = imgNameHead + System.currentTimeMillis() + "_1.jpg";
                File appDir = new File(Fire_IvaWater_Path);
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String imgFilePath = Fire_IvaWater_Path + "/" + imagePath; //图片文件的绝对路径
                LocationTools.SetLocation(FireHydrantDataShowActivity.this);
                Intent intent = new Intent(FireHydrantDataShowActivity.this, WifiActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFireHydrant", true);
                bundle.putString("imagePath", imgFilePath);
                intent.putExtras(bundle);
                startActivityForResult(intent, 100);
            } else {
                Toast.makeText(FireHydrantDataShowActivity.this, "GPS定位未打开，请打开GPS确保定位准确", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*手机相机拍照——点击事件*/
    public void takePhotoPhone(View view) {
        if (isUpload == 0) {
            Toast.makeText(getApplicationContext(), "数据已经上传，不可在手动修改", Toast.LENGTH_SHORT).show();
        } else {
            if (GPSUtils.GPSIsOPen(FireHydrantDataShowActivity.this)) {
                String imagePath = imgNameHead + System.currentTimeMillis() + "_0.jpg";
                File appDir = new File(Fire_IvaWater_Path);
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String imgFilePath = Fire_IvaWater_Path + "/" + imagePath; //图片文件的绝对路径
                LocationTools.SetLocation(FireHydrantDataShowActivity.this);
                Intent intent = new Intent(FireHydrantDataShowActivity.this, CustomCameraActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFireHydrant", false);
                bundle.putString("imgFilePath", imgFilePath);
                intent.putExtras(bundle);
                startActivityForResult(intent, 101);
            } else {
                Toast.makeText(FireHydrantDataShowActivity.this, "GPS定位未打开，请打开GPS确保定位准确", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*停止更新定位并获取定位信息*/
    public void getLocationInfo() {
        LocationTools.Stoplocayion();
        /*更新数据库中当前Task信息*/
        ContentValues values = new ContentValues();
        values.put("longitude", GlobalData.currentLatitude);
        values.put("latitude", GlobalData.currentLongitude);
        LitePal.updateAll(FireHydrantDetailData.class, values, "fire_hydrant_id = ?", fire_hydrant_id);
        /*重置*/
        GlobalData.currentLongitude = "0";
        GlobalData.currentLatitude = "0";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100://小盒子拍照
                if (resultCode == 1) {
                    boolean isExist = data.getExtras().getBoolean("isExist");
                    if (isExist) {
                        String localImageStr = data.getExtras().getString("imageStr");
                        updateDataAfterPhoto(localImageStr, 90);
                    }
                }
                break;
            case 101://相机拍照
                if (resultCode == Activity.RESULT_OK) {
                    String localImageStr = data.getExtras().getString("imageStr");
                    updateDataAfterPhoto(localImageStr, 0);
                }
                break;
            default:
                break;
        }
    }

    /*
     *拍照后更新数据库的信息
     * @param imagePath  图片路径
     */
    public void updateDataAfterPhoto(String imagePath, int degree) {
        Log.e("zksy", "消防栓图片路径为： " + imagePath);
        File localFile = new File(imagePath);
        String imageName = localFile.getName();
        Bitmap bmp = modifyImage.getimage( imagePath, "消防栓", degree,22);
        fireHydrantImage.setImageBitmap(bmp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String imgTime = simpleDateFormat.format(new java.util.Date());
        /*定位信息*/
        getLocationInfo();
        /*更新数据库中当前Task信息*/
        ContentValues values = new ContentValues();
        switch (ImagePathIndex) {//选择更新字段
            case 1:
                values.put("imagePathOne", imagePath);
                values.put("fileNameTwo", imageName.substring(0, imageName.lastIndexOf(".")));
                break;
            case 2:
                values.put("imagePathTwo", imagePath);
                values.put("fileNameOne", imageName.substring(0, imageName.lastIndexOf(".")));
                break;
        }
        LitePal.updateAll(FireHydrantDetailData.class, values, "fire_hydrant_id = ?", fire_hydrant_id);
        initDatas();
        updateFireHydrantValue();
    }

    /*
     *更新数据库的值
     */
    public void updateFireHydrantValue() {
        if (currentData.getImagePathOne() != null && !currentData.getImagePathOne().equals("") && currentData.getImagePathTwo() != null && !currentData.getImagePathTwo().equals("") && currentData.getCurrent_value() != null && !currentData.getCurrent_value().equals("")) {
            ContentValues values1 = new ContentValues();
            values1.put("isChecked", "0");
            LitePal.updateAll(FireHydrantDetailData.class, values1, "fire_hydrant_id = ?", fire_hydrant_id);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
