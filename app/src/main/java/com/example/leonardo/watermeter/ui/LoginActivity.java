package com.example.leonardo.watermeter.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.utils.GlobalVariables;
import com.example.leonardo.watermeter.utils.NetWorkUtils;
import com.example.leonardo.watermeter.utils.SharedPreUtils;
import com.example.leonardo.watermeter.utils.PhoneState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class LoginActivity extends Activity {
    private String IMEI_number, responseString;
    private TextView textIMEI;
    private SharedPreferences sp;
    /*handler方法*/
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //Bundle data = msg.getData();
            String responseString = msg.getData().getString("response");
            System.out.println("检测--handler接收到的返回值是：" + responseString);
            if (responseString != null) {
                System.out.println("检测--登陆成功，即将跳转到下一页面");
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("autoLogin", true);
                editor.commit();
                Intent intent = new Intent(LoginActivity.this, MonthListViewActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                Toast.makeText(LoginActivity.this, "请与管理员联系绑定设备", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_login);
        loginInit();
    }

    public void loginInit() {

        IMEI_number = PhoneState.getDeviceId(getApplicationContext());
        textIMEI = (TextView) findViewById(R.id.text_IMEI);
        textIMEI.setText("当前设备串号为：" + IMEI_number);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        /*是否自动登录*/
        final boolean ifAutoLogin = sp.getBoolean("autoLogin", false);
        // 默认是普通数据
        if (ifAutoLogin) {
            System.out.println("检测--开启自动登录了，并进行了跳转");
            Intent intent = new Intent(LoginActivity.this, MonthListViewActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreUtils.setDataType(LoginActivity.this, false);
        }
    }

    /*登录按钮操作*/
    public void loginApp(View v) {
        //get的方式提交就是url拼接的方式
        NetWorkUtils.isNetWorkAvailableOfDNS(GlobalVariables.NETIP, new Comparable<Boolean>() {
            @Override
            public int compareTo(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    //TODO 设备允许访问网络
                    final String path = "http://" + new SharedPreUtils().GetIp(LoginActivity.this) + "/services/phone/login?imei=" + IMEI_number;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(path);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setConnectTimeout(5000);
                                connection.setRequestMethod("GET");
                                //获得结果码
                                int responseCode = connection.getResponseCode();
                                if (responseCode == 200) {
                                    //请求成功 获得返回的流
                                    InputStream is = connection.getInputStream();
                                    BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                                    responseString = buf.readLine();
                                    buf.close();
                                    is.close();
                                    analyseResponse(responseString);
                                } else {
                                    //请求失败
                                    analyseResponse("");
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (ProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    // TODO 设备无法访问Internet
                    Toast.makeText(LoginActivity.this, "当前网络不可用，请连接有效网络", Toast.LENGTH_SHORT).show();
                }
                return 0;
            }
        });

    }

    public void analyseResponse(String responseString) {
        System.out.println("检测-登录的返回结果是：" + responseString);
        //TODO  等待加上Message的方法
        Message message = new Message();
        Bundle temp = new Bundle();
        temp.putString("response", responseString);
        message.setData(temp);
        handler.sendMessage(message);
    }
}
