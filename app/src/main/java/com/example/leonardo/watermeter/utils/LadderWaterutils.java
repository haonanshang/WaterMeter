package com.example.leonardo.watermeter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.blueToothPrinter.BluetoothServiceTest;
import com.blueToothPrinter.BluetoothValues;
import com.blueToothPrinter.Command;
import com.blueToothPrinter.NumberToCH;
import com.blueToothPrinter.PrintPicture;
import com.blueToothPrinter.PrinterCommand;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.entity.LadderPriceBean;
import com.example.leonardo.watermeter.entity.UsedLadderPriceBean;

import org.apache.commons.lang3.StringUtils;


import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * 阶梯水价工具类
 */
public class LadderWaterutils {
    /**
     * 打印水表账单
     */
    public static void Print_WaterMeterBill(Context mContext, BluetoothServiceTest mService, DetailData detailData) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        String date = str + "\n\n";
        try {
            Command.ESC_Align[2] = 0x01;
            SendDataByte(mContext, mService, Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x11;
            SendDataByte(mContext, mService, Command.GS_ExclamationMark);
            SendDataByte(mContext, mService, "水表抄见通知单\n".getBytes("GBK"));
            Command.ESC_Align[2] = 0x02;
            SendDataByte(mContext, mService, Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            SendDataByte(mContext, mService, Command.GS_ExclamationMark);
            SendDataString(mContext, mService, date);
            Command.ESC_Align[2] = 0x00;
            SendDataByte(mContext, mService, Command.ESC_Align);
            SendDataByte(mContext, mService, ("客户号:  " + detailData.getCustomer_no() + "\n卡号:  " + detailData.getT_card_num() + "\n户名:  " + detailData.getT_ticket_name() + "\n地址:  " + detailData.getT_location() + "\n口径:  " + detailData.getT_caliber() + "\n性质： \n").getBytes("GBK"));
            SendDataByte(mContext, mService, ("上期抄码:  " + detailData.getT_latest_index() + "\n本期抄码:  " + detailData.getT_cur_meter_data() + "\n本期水量:  " + detailData.getT_cur_read_water_sum() + "\n阶梯水量使用如下：\n").getBytes("GBK"));
            setLadderPriceAndWaterVolume(mContext, mService, detailData.getLadder_price(), detailData.getT_cur_read_water_sum(), detailData.getBalance());
            SendDataByte(mContext, mService, ("抄表员:  :" + detailData.getOwner_name() + "\n\n\n      以上数据仅供参考，详情请咨询自来水公司。\n\n").getBytes("GBK"));
//            Bitmap mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.qr_code);
//            /**
//             * Parameters:
//             * mBitmap  要打印的图片
//             * nWidth   打印宽度（58和80）
//             * nMode    打印模式
//             * Returns: byte[]
//             */
//            byte[] data = PrintPicture.POS_PrintBMP(mBitmap, 384, 0);
//            SendDataByte(mContext, mService, data);
            //SendDataByte(mContext, mService, "\n\n\n".getBytes("GBK"));
            SendDataByte(mContext, mService, PrinterCommand.POS_Set_PrtAndFeedPaper(48));
            SendDataByte(mContext, mService, Command.GS_V_m_n);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 设置阶梯水价和阶梯用水量
     *
     * @param mContext
     * @param mService
     * @param ladderString
     * @param cur_read_water_sum
     * @param balance
     */
    public static void setLadderPriceAndWaterVolume(Context mContext, BluetoothServiceTest mService, String ladderString, String cur_read_water_sum, String balance) {
        float curReadWaterSum = 0.0f;
        if (!StringUtils.isEmpty(cur_read_water_sum)) {
            curReadWaterSum = Float.valueOf(cur_read_water_sum);
        }
        //System.out.println("******** ladderString ***" + ladderString);
        if (!StringUtils.isEmpty(ladderString) && curReadWaterSum > 0) {
            //TODO 有阶梯水价的
            TreeMap<Integer, LadderPriceBean> ladderMap = new TreeMap<>();
            try {
                JSONArray ladderArray = JSONObject.parseArray(ladderString);
                //先排序
                if (ladderArray.size() > 0) {
                    for (int i = 0; i < ladderArray.size(); i++) {
                        LadderPriceBean ladderPriceBean = JSONObject.parseObject(ladderArray.get(i).toString(), LadderPriceBean.class);
                        ladderMap.put(ladderPriceBean.getLadder_step(), ladderPriceBean);
                    }
                }
                //找到当月用水量 获取用水量梯度
                Set<Integer> ladderStepSet = ladderMap.keySet();
                int ladderStep = -1;
                for (int i = ladderStepSet.size(); i > 0; i--) {
                    LadderPriceBean ladderPriceBean = ladderMap.get(i);
                    if (ladderPriceBean.getLadder_start() < curReadWaterSum) {
                        ladderStep = i;
                        break;
                    }
                }
                // 设置使用阶梯水量 以及当期水费
                LadderPriceBean ladderPriceBean = ladderMap.get(ladderStep);
                System.out.println("***** ladderPriceBean ****" + ladderPriceBean.toString());
                float waterRate = ladderPriceBean.getTotal_price() + ((curReadWaterSum - ladderPriceBean.getLadder_start()) * ladderPriceBean.getLadder_price());
                for (int i = 1; i <= ladderMap.size(); i++) {
                    if (i < ladderStep) {
                        SendDataByte(mContext, mService, ("含第" + NumberToCH.numberToCH(i) + "阶梯水量:  " + (Float.valueOf(ladderMap.get(i).getLadder_end()) - ladderMap.get(i).getLadder_start()) + "\n").getBytes("GBK"));
                    } else if (i == ladderStep) {
                        SendDataByte(mContext, mService, ("含第" + NumberToCH.numberToCH(i) + "阶梯水量:  " + (curReadWaterSum - ladderMap.get(i).getLadder_start()) + "\n").getBytes("GBK"));
                    } else {
                        SendDataByte(mContext, mService, ("含第" + NumberToCH.numberToCH(i) + "阶梯水量:  0\n").getBytes("GBK"));
                    }
                }
                SendDataByte(mContext, mService, ("本期水费:  " + waterRate + "\n").getBytes("GBK"));
                if (!StringUtils.isEmpty(balance)) {
                    SendDataByte(mContext, mService, ("欠费金额:  " + String.valueOf(Float.valueOf(balance) - waterRate) + "\n剩余阶梯水量如下: \n ").getBytes("GBK"));
                } else {
                    SendDataByte(mContext, mService, ("欠费金额:  无数据 \n").getBytes("GBK"));
                }
                for (int i = 1; i <= ladderMap.size(); i++) {
                    if (i > ladderStep) {
                        if (i == ladderMap.size()) {
                            SendDataByte(mContext, mService, ("剩余第" + NumberToCH.numberToCH(i) + "阶梯水量:  无上限\n").getBytes("GBK"));
                        } else {
                            SendDataByte(mContext, mService, ("剩余第" + NumberToCH.numberToCH(i) + "阶梯水量:  " + (Float.valueOf(ladderMap.get(i).getLadder_end()) - ladderMap.get(i).getLadder_start()) + "\n").getBytes("GBK"));
                        }
                    } else if (i == ladderStep) {
                        if (!StringUtils.isEmpty(ladderMap.get(i).getLadder_end())) {
                            SendDataByte(mContext, mService, ("剩余第" + NumberToCH.numberToCH(i) + "阶梯水量:  " + (Float.valueOf(ladderMap.get(i).getLadder_end()) - curReadWaterSum) + "\n").getBytes("GBK"));
                        } else {
                            SendDataByte(mContext, mService, ("剩余第" + NumberToCH.numberToCH(i) + "阶梯水量:  无上限\n").getBytes("GBK"));
                        }
                    } else {
                        SendDataByte(mContext, mService, ("剩余可用第" + NumberToCH.numberToCH(i) + "阶梯水量:  0\n").getBytes("GBK"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            //TODO  无阶梯水价 默认三个阶梯水价  所有值为空
            try {
                SendDataByte(mContext, mService, ("含第一阶梯水量:\n含第二阶梯水量:\n含第三阶梯水量:\n本期水费:\n欠费金额:无\n剩余阶梯水量如下:\n剩余第一阶梯水量:\n剩余第二阶梯水量:\n剩余第三阶梯水量:\n").getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前用水量的各档位的水价
     *
     * @param ladderString
     * @param cur_read_water_sum
     */
    public static List<UsedLadderPriceBean> getUsedLadderPriceBeanList(String ladderString, String cur_read_water_sum) {
        float curReadWaterSum = 0.0f;
        if (!StringUtils.isEmpty(cur_read_water_sum)) {
            curReadWaterSum = Float.valueOf(cur_read_water_sum);
        }
        List<UsedLadderPriceBean> usedLadderPriceBeanList = new ArrayList<>();
        if (!StringUtils.isEmpty(ladderString) && curReadWaterSum > 0) {
            //TODO 有阶梯水价的
            TreeMap<Integer, LadderPriceBean> ladderMap = new TreeMap<>();
            try {
                JSONArray ladderArray = JSONObject.parseArray(ladderString);
                //先排序
                if (ladderArray.size() > 0) {
                    for (int i = 0; i < ladderArray.size(); i++) {
                        LadderPriceBean ladderPriceBean = JSONObject.parseObject(ladderArray.get(i).toString(), LadderPriceBean.class);
                        ladderMap.put(ladderPriceBean.getLadder_step(), ladderPriceBean);
                    }
                }
                //找到当月用水量 获取用水量梯度
                Set<Integer> ladderStepSet = ladderMap.keySet();
                int ladderStep = -1;
                for (int i = ladderStepSet.size(); i > 0; i--) {
                    LadderPriceBean ladderPriceBean = ladderMap.get(i);
                    if (ladderPriceBean.getLadder_start() < curReadWaterSum) {
                        ladderStep = i;
                        break;
                    }
                }
                // 设置档位水费
                LadderPriceBean ladderPriceBean = ladderMap.get(ladderStep);
                for (int i = 1; i <= ladderMap.size(); i++) {
                    if (i < ladderStep) {
                        usedLadderPriceBeanList.add(new UsedLadderPriceBean(i, String.valueOf(ladderMap.get(i + 1).getTotal_price())));
                    } else if (i == ladderStep) {
                        usedLadderPriceBeanList.add(new UsedLadderPriceBean(i, String.valueOf((curReadWaterSum - ladderPriceBean.getLadder_start()) * ladderPriceBean.getLadder_price())));
                    } else {
                        usedLadderPriceBeanList.add(new UsedLadderPriceBean(i, "0"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //TODO  无阶梯水价 默认三个阶梯水价  所有值为空
            for (int i = 1; i < 4; i++) {
                usedLadderPriceBeanList.add(new UsedLadderPriceBean(i, "0"));
            }
        }
        return usedLadderPriceBeanList;
    }


    /*
     *SendDataByte
     */
    public static void SendDataByte(Context mContext, BluetoothServiceTest mService, byte[] data) {

        if (mService.getState() != BluetoothValues.STATE_CONNECTED) {
            Toast.makeText(mContext, "请连接蓝牙打印机", Toast.LENGTH_SHORT).show();
            return;
        }
        mService.write(data);
    }


    /*
     * SendDataString
     */
    public static void SendDataString(Context mContext, BluetoothServiceTest mService, String data) {

        if (mService.getState() != BluetoothValues.STATE_CONNECTED) {
            Toast.makeText(mContext, "请连接蓝牙打印机", Toast.LENGTH_SHORT).show();
            return;
        }
        if (data.length() > 0) {
            try {
                mService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
