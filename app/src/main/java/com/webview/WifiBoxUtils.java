package com.webview;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/*
 *检查盒子是否处于连接状态
 */
public class WifiBoxUtils {

    /**
     * 检查小盒子是否属于连接状态
     *
     * @param callback 检查结果回调（是否可以解析成功）{@see java.lang.Comparable<T>}
     */
    public static void isWifiBoxAvailableOfSocket(final Comparable<Boolean> callback) {
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (callback != null) {
                    System.out.println("收到的指令为2：" + msg.arg1);
                    callback.compareTo(msg.arg1 == 0);
                }
            }

        };
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                try {
                    // 1.创建客户端Socket，指定服务器地址和端口
                    Socket socket = new Socket("192.168.10.123", 8899);
                    // 2.获取输出流，向服务器端发送信息
                    OutputStream os = socket.getOutputStream();// 字节输出
                    PrintWriter pw = new PrintWriter(os);// 将输出流包装为打印流
                    String jsonStr = "{\"status\":\"0\",\"command\":\"0\"}";
                    pw.write(jsonStr);
                    pw.flush();
                    socket.shutdownOutput();// 关闭输出流
                    // 3.获取输入流，并读取服务器端的响应信息
                    InputStream is = socket.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String info = null;
                    StringBuffer buffer = new StringBuffer();
                    while ((info = br.readLine()) != null) {
                        buffer.append(info);
                    }
                    System.out.println("我是客户端，服务器说：" + buffer.toString());
                    String boxStatus = null;
                    try {
                        JSONObject jsonObject = new JSONObject(buffer.toString());
                        boxStatus = jsonObject.getString("info");
                        System.out.println("服务器的状态为：" + boxStatus);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // 4.关闭资源
                    br.close();
                    is.close();
                    pw.close();
                    os.close();
                    socket.close();
                    msg.arg1 = boxStatus.equals("OK") ? 0 : -1;
                    System.out.println("收到的指令为1：" + msg.arg1);
                } catch (Exception e) {
                    msg.arg1 = -1;
                    e.printStackTrace();
                } finally {
                    handler.sendMessage(msg);
                }
            }

        }).start();
    }


}
