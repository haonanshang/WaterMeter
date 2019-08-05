package com.example.leonardo.watermeter.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 工具类
 */
public class CameraUtils {
    /**
     * android 7.0 以上版本
     *
     * @param context
     * @param imagePath
     * @return
     */
    public static Uri getOutputMediaFileUri(Context context, String imagePath) {
        File mediaFile = null;
        try {
            mediaFile = new File(imagePath);//注意这里需要和filepaths.xml中配置的一样
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// sdk >= 24  android7.0以上
            Uri contentUri = FileProvider.getUriForFile(context, "com.example.leonardo.watermeter.fileprovider", mediaFile);//FileProvider方式或者ContentProvider。也可使用VmPolicy方式 //与清单文件中android:authorities的值保持一致
            return contentUri;
        } else {
            return Uri.fromFile(mediaFile);//或者 Uri.isPaise("file://"+file.toString()
        }
    }


}
