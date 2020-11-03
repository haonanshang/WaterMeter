package com.example.leonardo.watermeter.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import com.FireHydrant.entity.FireHydrantDetailData;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.entity.UploadTaskBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 上传json组装类-上传字段都在这里
 * 修改 CC
 */
public class JsonDataUtils {
    public UploadTaskBean GetJsonString(List<DetailData> data, Context mContext) {
        UploadTaskBean uploadTaskBean = new UploadTaskBean();
        String imgBase64String = "";
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < data.size(); i++) {
            DetailData detailData = data.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("imei", detailData.getT_phone_imei());//手机串号
                jsonObject.put("t_id", detailData.getT_id());// 任务ID-表册明细ID
                jsonObject.put("detail_date", detailData.getT_cbyf());//表册日期
                jsonObject.put("t_card_num", detailData.getT_card_num());//卡号
                jsonObject.put("w_jblx", detailData.getT_jblx());//水表状态
                jsonObject.put("w_x", detailData.getT_x());//经度
                jsonObject.put("w_y", detailData.getT_y());//纬度
                jsonObject.put("w_eid", detailData.getT_eid());//水箱位置
                jsonObject.put("w_eid_bz", detailData.getT_eid_bz());//水表位置
                jsonObject.put("t_cur_read_water_sum", detailData.getT_cur_read_water_sum());//抄表吨位
                jsonObject.put("t_cur_read_date", detailData.getT_cur_read_data());//抄表日期
                jsonObject.put("t_cur_meter_data", detailData.getT_cur_meter_data());//当期值
                jsonObject.put("normal_detect", detailData.getT_normal_detect());//是否正常水表
                jsonObject.put("new_phonenumber", detailData.getT_new_phonenumber());//修改手机号
                jsonObject.put("new_phonenumber2", detailData.getT_new_phonenumber2());//修改手机号2
                jsonObject.put("t_isManual", detailData.getT_isManual());//是否手抄的标志
                jsonObject.put("w_bz", detailData.getT_bz());//客户备注
                jsonObject.put("org_code", detailData.getOrg_code());//水司编号
                jsonObject.put("auto_detect_flag", detailData.getAuto_detect_flag());//自动识别标志
                jsonObject.put("modify_detect_num", detailData.getModify_detect_num());//自动识别参数 默认为空 手动修改后值为识别参数
                jsonObject.put("version_name", mContext.getPackageManager().getPackageInfo(mContext.getApplicationContext().getPackageName(), 0).versionName);//版本号
                jsonObject.put("old_meter_value", detailData.getOld_meter_value());//旧表读数
                jsonObject.put("new_meter_value", detailData.getNew_meter_value());//新表读数
                jsonObject.put("change_meter_flag", detailData.getChange_meter_flag());//换表标志位
                jsonObject.put("t_meter_num", detailData.getT_meter_num());//表身号，cc增加字段
                jsonObject.put("seal_no", detailData.getSealNo());//铅封号，cc增加字段
                if (detailData.getT_image_path() != null && !detailData.equals("")) {
                    jsonObject.put("t_file_name", detailData.getT_image_name() + ".jpg");//文件名
                    if (i != data.size() - 1) {
                        imgBase64String += ModifyImage.getImageBinary(detailData.getT_image_path()) + ",";
                    } else {
                        imgBase64String += ModifyImage.getImageBinary(detailData.getT_image_path());
                    }
                    System.out.println("上传的json串为：" + jsonObject.toString());
                } else {
                    jsonObject.put("t_file_name", "no img");//无照片
                    if (i != data.size() - 1) {
                        imgBase64String += "无图,";
                    } else {
                        imgBase64String += "无图";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        uploadTaskBean.setTaskList(jsonArray.toString());
        uploadTaskBean.setImgBase64String(imgBase64String);
        return uploadTaskBean;
    }

    public String GetFireHydrantJsonString(List<FireHydrantDetailData> data) {
        JSONArray jsonArray = new JSONArray();
        for (FireHydrantDetailData detailData : data) {
            JSONObject jsonObject = new JSONObject();
            String imgBase64String;
            try {
                jsonObject.put("record_key_detail", detailData.getRecord_key_detail());//任务记录id,作为唯一的id使用 上传的时候按原图返回
                jsonObject.put("record_key_list", detailData.getRecord_key_list());//消防栓记录id,作为唯一的id使用，上传的时候按原图返回
                jsonObject.put("fire_hydrant_id", detailData.getFire_hydrant_id());//消防栓id
                jsonObject.put("booklet_no", detailData.getBooklet_no());// 表册号
                jsonObject.put("detail_date", detailData.getDetail_date());//巡查日期
                jsonObject.put("org_code", detailData.getOrg_code());//机构号
                jsonObject.put("Longitude", detailData.getLongitude());//经度
                jsonObject.put("latitude", detailData.getLatitude());//纬度
                jsonObject.put("current_value", detailData.getCurrent_value());//当期读数
                jsonObject.put("check_date", detailData.getCheck_date());//拍照时间
                jsonObject.put("t_file_names", detailData.getFileNameOne() + ".jpg" + "," + detailData.getFileNameTwo() + ".jpg");//文件名
                ModifyImage modifyImage = new ModifyImage();
                imgBase64String = modifyImage.getImageBinary(detailData.getImagePathOne()) + "," + modifyImage.getImageBinary(detailData.getImagePathTwo());
                jsonObject.put("imgBase64Strings", imgBase64String);//图片数据
                System.out.println("上传的json串为：" + jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }
}
