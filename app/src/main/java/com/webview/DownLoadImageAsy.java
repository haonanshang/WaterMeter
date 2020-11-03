package com.webview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.VideoView;

import org.apache.axis.components.image.ImageIO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DownLoadImageAsy extends AsyncTask<Void, Void, Map> {
    private WebviewActivity activity;
    private String snapShotUrlStr = "http://192.168.10.123:7060/?action=snapshot";
    private String imagePath;

    public DownLoadImageAsy(WebviewActivity activity, String imagePath) {
        this.activity = activity;
        this.imagePath = imagePath;
    }

    @Override
    protected Map doInBackground(Void... voids) {
        return downLoadImageUrl(imagePath);
    }

    /*
     *下载图片
     */
    private Map downLoadImageUrl(String imagePath) {
        Map resultMap = new HashMap();
        Bitmap bitmap = GetImageInputStream(snapShotUrlStr);
        resultMap = SavaImage(bitmap, imagePath);
        if (!(Boolean) resultMap.get("isExist")) {
            resultMap = downLoadImageUrl(imagePath);
        }
        return resultMap;
    }

    @Override
    protected void onPostExecute(Map map) {
        super.onPostExecute(map);
        Log.e("zksy", "是否下载图片成功 " + map.get("isExist"));
        activity.finishMainActivity(map);
    }

    /**
     * 获取网络图片
     *
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = new URL(imageurl).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存位图到本地
     *
     * @param bitmap
     * @param imagePath 本地路径
     * @return void
     */
    public Map SavaImage(Bitmap bitmap, String imagePath) {
        Map resultMap = new HashMap();
        boolean isExit = false;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            File localFile = new File(imagePath);
            if (localFile.exists() && localFile.length() > 0) {
                isExit = true;
            } else {
                localFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resultMap.put("isExist", isExit);
        resultMap.put("imageStr", imagePath);
        return resultMap;
    }

}
