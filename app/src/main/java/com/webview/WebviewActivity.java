package com.webview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.FireHydrant.ui.FireHydrantDataShowActivity;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.ui.TaskShowActivity;

import java.util.Map;


public class WebviewActivity extends AppCompatActivity {
    private MyWebView mWebview;
    private WebSettings mWebSettings;
    private String streamUrlStr = "http://192.168.10.123:7060/?action=stream";
    private ImageView mImageView;
    private SoundPool pool;
    String imagePath;
    boolean isFireHydrant = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_webview);
        Bundle bundle = this.getIntent().getExtras();
        imagePath = bundle.getString("imagePath");
        isFireHydrant = bundle.getBoolean("isFireHydrant");
        initViews();
        InitWebViewSetting();
        mWebview.loadUrl(streamUrlStr);
        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new MyWebViewClient());
    }

    /*
     *初始化组件
     */
    private void initViews() {
        mWebview = findViewById(R.id.webView1);
        mImageView = findViewById(R.id.stopLoad);
        //指定声音池的最大音频流数目为10，声音品质为5
        pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        //载入音频流，返回在池中的id
        final int sourceid = pool.load(this, R.raw.shutter, 0);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //播放音频，第二个参数为左声道音量;第三个参数为右声道音量;第四个参数为优先级；第五个参数为循环次数，0不循环，-1循环;第六个参数为速率，速率    最低0.5最高为2，1代表正常速度
                pool.play(sourceid, 10, 10, 0, 0, 1);
                mWebview.stopLoading();
                mImageView.setVisibility(View.GONE);
                new DownLoadImageAsy(WebviewActivity.this, imagePath).execute();
            }
        });
    }

    /*
     *设置websetting
     */
    private void InitWebViewSetting() {
        mWebSettings = mWebview.getSettings();
        //不缓存
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //支持javascript
        mWebSettings.setJavaScriptEnabled(true);
        //扩大比例的缩放
        mWebSettings.setUseWideViewPort(true);
        // 全屏显示
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebSettings.setLoadWithOverviewMode(true);
    }

    /*
     *线程调用 销毁activity
     */
    public void finishMainActivity(Map map) {
        boolean isExist = (boolean) map.get("isExist");
        String imageUrl = null;
        if (isExist) {
            imageUrl = (String) map.get("imageStr");
            Log.e("zksy", "WebviewActivity收到的imageUrl为：" + imageUrl);
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
        WebviewActivity.this.finish();
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        super.onDestroy();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();
            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        if (pool != null) {
            pool.release();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("zksy", "+++++++ webview  configchange");
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}
