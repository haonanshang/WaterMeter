package com.objecteye.author;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.MyApplication;
import com.example.leonardo.watermeter.ui.MonthListViewActivity;
import com.syteco.android.hardwareinfo.service.HeartBeatService;

import org.litepal.LitePalApplication;

import java.util.Timer;
import java.util.TimerTask;


public class AuthorApplication extends LitePalApplication {
    private static Context context;
    private static String tag = "waterMeter";
    private final static Timer timer = new Timer();
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    /**
                     * 初始化JNI
                     */
                    MyApplication.getInstance().getJniInterface();
                    AuthorCustomUtils.dissmisAuthorProcess();
                    timer.schedule(task, 1000, 1000 * 60 * 5);
                    break;
                case 1:
                    AuthorCustomUtils.dissmisAuthorProcess();
                    Activity mActivity = (Activity) msg.obj;
                    Log.i(tag, AuthorCustomUtils.getCurrentDate() + " Authorization timeout");
                    AuthorCustomUtils.showReminderDialog(mActivity, "授权失败，请检查网络连接和手机权限，确认手机权限打开和网络连接正常，请联系开发者");
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 定时检测授权服务
     */
    private static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.i(tag, AuthorCustomUtils.getCurrentDate() + "check service running");
            com.syteco.android.hardwareinfo.service.HeartBeatService.getHearBeatService().HeartBeatFunc();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        HeartBeatService.getHearBeatService().setContext(context);
    }

    /**
     * 启动服务
     */
    public static void startService(final Activity mActivity) {
        if (!AuthorCustomUtils.CheckLicence()) {
            AuthorCustomUtils.showAuthorProcess(mActivity);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    int authorCount = 0;
                    while (true) {
                        if (authorCount < 10) {
                            if (AuthorCustomUtils.CheckLicence()) {
                                Log.i(tag, AuthorCustomUtils.getCurrentDate() + " Authorization success");
                                handler.sendEmptyMessage(0);
                                break;
                            } else {
                                Log.i(tag, AuthorCustomUtils.getCurrentDate() + " Authorization is running");
                                com.syteco.android.hardwareinfo.service.HeartBeatService.getHearBeatService().HeartBeatFunc();
                                authorCount++;
                                try {
                                    sleep(1000 * 5);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.obj = mActivity;
                            handler.sendMessage(msg);
                            break;
                        }
                    }
                }
            }.start();
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    /**
     * 关闭服务
     */
    public static void stopService() {
        timer.cancel();
    }

}
