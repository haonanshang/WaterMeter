package com.example.leonardo.watermeter.utils;

import android.util.Log;
import android.util.Xml;

import com.FireHydrant.entity.FireHydrantBchData;
import com.FireHydrant.entity.FireHydrantDetailData;
import com.example.leonardo.watermeter.entity.DetailData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonardo on 2017/5/3.
 */

public class ParseXml {


    /**
     * 用Pull的方式解析Xml数据
     *
     * @param is          表册信息读流
     * @param cbyf        抄表月份
     * @param ladderPrice 水表阶梯水价字符串
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static List getDetailData(InputStream is, String cbyf, String ladderPrice,String waterMeterStateStr) throws IOException, XmlPullParserException {
        XmlPullParser xp = Xml.newPullParser();
        List<DetailData> detailDataList = new ArrayList<>();
        DetailData detailData = null;
        xp.setInput(is, "utf-8");
        int type = xp.getEventType();
        try {
            while (type != XmlPullParser.END_DOCUMENT) {
                System.out.println("测试-重要-标签为：" + xp.getName());
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if (xp.getName().equals("Table")) {
                            detailData = new DetailData();
                        } else if (xp.getName().equals("t_id")) {
                            System.out.println("测试-新建了detailData对象啊！！！");
                            detailData.setT_id(xp.nextText());
                            System.out.println("重要！-要写入的id为：" + detailData.getT_id());
                            detailData.setT_cbyf(cbyf);//NOTICE 原数据没有这一项，添加方便查找
                            detailData.setT_isManual("1");
                            detailData.setAuto_detect_flag("2");
                            //写入水表状态列表
                             detailData.setWater_meter_state(waterMeterStateStr);
                            //写入水表阶梯水价
                            detailData.setLadder_price(ladderPrice);
                        } else if (xp.getName().equals("t_phone_imei")) {
                            detailData.setT_phone_imei(xp.nextText());
                        } else if (xp.getName().equals("t_card_num")) {
                            detailData.setT_card_num(xp.nextText());
                        } else if (xp.getName().equals("t_ticket_name")) {
                            detailData.setT_ticket_name(xp.nextText());
                        } else if (xp.getName().equals("t_location")) {
                            detailData.setT_location(xp.nextText());
                        } else if (xp.getName().equals("t_volume_num")) {
                            detailData.setT_volume_num(xp.nextText());
                        } else if (xp.getName().equals("t_caliber")) {
                            detailData.setT_caliber(xp.nextText());
                        } else if (xp.getName().equals("t_latest_index")) {
                            detailData.setT_latest_index(xp.nextText());
                        } else if (xp.getName().equals("t_latest_date")) {
                            detailData.setT_latest_date(xp.nextText());
                        } else if (xp.getName().equals("t_water_property")) {
                            detailData.setT_water_property(xp.nextText());
                        } else if (xp.getName().equals("t_meter_num")) {
                            detailData.setT_meter_num(xp.nextText());
                        } else if (xp.getName().equals("t_tel_phone")) {
                            detailData.setT_tel_phone(xp.nextText());
                        } else if (xp.getName().equals("t_mobile_phone")) {
                            detailData.setT_mobile_phone(xp.nextText());
                        } else if (xp.getName().equals("t_setup_time")) {
                            detailData.setT_setup_time(xp.nextText());
                        } else if (xp.getName().equals("t_card_time")) {
                            detailData.setT_card_time(xp.nextText());
                        } else if (xp.getName().equals("t_average")) {
                            detailData.setT_average(xp.nextText());
                        } else if (xp.getName().equals("t_volume_order")) {
                            detailData.setT_volume_order(xp.nextText());
                        } else if (xp.getName().equals("t_area")) {
                            detailData.setT_area(xp.nextText());
                        } else if (xp.getName().equals("w_jblx")) {
                            detailData.setT_jblx(xp.nextText());
                        } else if (xp.getName().equals("w_x")) {
                            String latitude = xp.nextText();
                            detailData.setT_x(latitude);
                            detailData.setT_latitude(latitude);
                        } else if (xp.getName().equals("w_y")) {
                            String longitude = xp.nextText();
                            detailData.setT_y(longitude);
                            detailData.setT_longitude(longitude);
                        } else if (xp.getName().equals("w_eid")) {
                            detailData.setT_eid(xp.nextText());
                        } else if (xp.getName().equals("t_cur_meter_data")) {
                            detailData.setT_cur_meter_data(xp.nextText());
                        } else if (xp.getName().equals("t_cur_read_water_sum")) {
                            detailData.setT_cur_read_water_sum(xp.nextText());
                        } else if (xp.getName().equals("t_read_data")) {
                            detailData.setT_cur_read_data(xp.nextText());
                        } else if (xp.getName().equals("w_bz")) {
                            detailData.setT_bz(xp.nextText());
                        } else if (xp.getName().equals("w_eid_bz")) {
                            detailData.setT_eid_bz(xp.nextText());
                        } else if (xp.getName().equals("i_id")) {
                            String str = xp.nextText();
                            //NOTICE  这里做了一个新获取数据的对于我本地数据的一个操作，上传过是否拍照和是否上传分别置为0和0，没有上传过则都置为1；
                            if (str.equals("1")) {
                                detailData.setIsChecked("0");
                                detailData.setIsUpload("0");
                            } else {
                                detailData.setIsChecked("1");
                                detailData.setIsUpload("1");
                            }
                        } else if (xp.getName().equals("arrears_flag")) {
                            detailData.setT_arrears_flag(xp.nextText());
                        } else if (xp.getName().equals("extend_field")) {
                            detailData.setExtend_field_string(xp.nextText());
                        } else if (xp.getName().equals("one_prior_used_value")) {
                            detailData.setT_one_prior_used_value(xp.nextText());
                        } else if (xp.getName().equals("need_img")) {
                            detailData.setT_need_img(xp.nextText());
                        } else if (xp.getName().equals("normal_detect")) {
                            detailData.setT_normal_detect(xp.nextText());
                        } else if (xp.getName().equals("normal_WaterDivision")) {
                            detailData.setT_normal_WaterDivision(xp.nextText());
                        } else if (xp.getName().equals("new_phonenumber")) {
                            detailData.setT_new_phonenumber(xp.nextText());
                        } else if (xp.getName().equals("org_code")) {
                            detailData.setOrg_code(xp.nextText());
                        } else if (xp.getName().equals("customer_no")) {
                            detailData.setCustomer_no(xp.nextText());
                        } else if (xp.getName().equals("change_meter_flag")) {
                            detailData.setChange_meter_flag(xp.nextText());
                        } else if (xp.getName().equals("old_meter_value")) {
                            detailData.setOld_meter_value(xp.nextText());
                        } else if (xp.getName().equals("new_meter_value")) {
                            detailData.setNew_meter_value(xp.nextText());
                        } else if (xp.getName().equals("balance")) {
                            detailData.setBalance(xp.nextText());
                        } else if (xp.getName().equals("owner_name")) {
                            detailData.setOwner_name(xp.nextText());
                        } else if (xp.getName().equals("detail_date_list")) {
                            detailData.setDetail_date_list(xp.nextText());
                        } else if (xp.getName().equals("used_value_list")) {
                            detailData.setUsed_value_list(xp.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xp.getName().equals("Table")) {
                            detailDataList.add(detailData);
                            System.out.println("测试-给list写入了一个对象");
                        }
                        break;
                    default:
                        break;
                }
                type = xp.next();//解析完当前节点后，把指针移动至下一个节点，并返回它的事件类型
                Log.e("sy", "返回的节点类型为：" + type);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();

        }
        System.out.println("重要！-解析的时候，detailDataList第一条数据的id为：" + detailDataList.get(0).getT_id());
        return detailDataList;
    }

    /*
     * 通过解析xml解析得到消防栓数据
     */
    public static List getFireHydrantDetailData(InputStream is) {
        XmlPullParser xp = Xml.newPullParser();
        List<FireHydrantDetailData> detailDataList = new ArrayList<>();
        FireHydrantDetailData detailData = null;
        try {
            xp.setInput(is, "utf-8");
            int type = xp.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if (xp.getName().equals("Table")) {
                            detailData = new FireHydrantDetailData();
                            detailData.setIsChecked("1");//设成默认值 1代表未拍照
                            detailData.setIsUpload("1");// 设成默认值 1代表未上传
                        } else if (xp.getName().equals("record_key_detail")) {
                            detailData.setRecord_key_detail(xp.nextText());
                        } else if (xp.getName().equals("record_key_list")) {
                            detailData.setRecord_key_list(xp.nextText());
                        } else if (xp.getName().equals("fire_hydrant_id")) {
                            detailData.setFire_hydrant_id(xp.nextText());
                        } else if (xp.getName().equals("fire_hydrant_name")) {
                            detailData.setFire_hydrant_name(xp.nextText());
                        } else if (xp.getName().equals("booklet_no")) {
                            detailData.setBooklet_no(xp.nextText());
                        } else if (xp.getName().equals("org_code")) {
                            detailData.setOrg_code(xp.nextText());
                        } else if (xp.getName().equals("detail_date")) {
                            detailData.setDetail_date(xp.nextText());
                        } else if (xp.getName().equals("address")) {
                            detailData.setAddress(xp.nextText());
                        } else if (xp.getName().equals("longitude")) {
                            detailData.setLongitude(xp.nextText());
                        } else if (xp.getName().equals("latitude")) {
                            detailData.setLatitude(xp.nextText());
                        } else if (xp.getName().equals("state")) {
                            detailData.setState(xp.nextText());
                        } else if (xp.getName().equals("type")) {
                            detailData.setType(xp.nextText());
                        } else if (xp.getName().equals("last_value")) {
                            detailData.setLast_value(xp.nextText());
                        } else if (xp.getName().equals("last_date")) {
                            detailData.setLast_date(xp.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xp.getName().equals("Table")) {
                            detailDataList.add(detailData);
                        }
                        break;
                    default:
                        break;
                }
                type = xp.next();//解析完当前节点后，把指针移动至下一个节点，并返回它的事件类型
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("重要！-解析的时候，detailDataList第一条数据的id为：" + detailDataList.get(0).getFire_hydrant_id());
        return detailDataList;
    }
}
