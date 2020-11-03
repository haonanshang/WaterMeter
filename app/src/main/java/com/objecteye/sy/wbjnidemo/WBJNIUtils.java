package com.objecteye.sy.wbjnidemo;

import android.graphics.Bitmap;


import com.objecteye.sy.wifibox.WBUtils;
import com.objecteye.sy.wifibox.WBValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 */
public class WBJNIUtils {

    /**
     * @return
     */
    public static byte[] getFrameData() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Future<byte[]> future = singleThreadExecutor.submit(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                String url = "http://" + WBValues.WBIP + ":" + WBValues.WBSTREAMPORT + "/?" + WBValues.WBSTREAMPARAM;
                byte[] buffByte = null;
                Bitmap bitmap = WBUtils.getBitmap(url);
                if (bitmap != null) {
                    buffByte = WBUtils.Bitmap2Bytes(bitmap);
                }
                return buffByte;
            }
        });
        byte[] data = null;
        try {
            data = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * @return
     */
    public static boolean getStatus() {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = singleThreadExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean socketStatus = false;
                try {
                    Socket socket = new Socket();
                    InetSocketAddress address = new InetSocketAddress(WBValues.WBIP, WBValues.WBSTATUSPORT);
                    socket.connect(address, 500);
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
                    String boxStatus = null;
                    try {
                        JSONObject jsonObject = new JSONObject(buffer.toString());
                        boxStatus = jsonObject.getString("info");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // 4.关闭资源
                    br.close();
                    is.close();
                    pw.close();
                    os.close();
                    socket.close();
                    socketStatus = boxStatus.equals("OK") ? true : false;
                } catch (Exception e) {
                    e.toString();
                    socketStatus = false;
                } finally {
                    return socketStatus;
                }
            }
        });
        boolean result = false;
        try {
            result = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
