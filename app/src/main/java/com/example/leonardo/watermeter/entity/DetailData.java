package com.example.leonardo.watermeter.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Leonardo on 2017/5/2.
 */

public class DetailData extends DataSupport {
    private int id;
    private String t_id;//任务ID
    private String t_phone_imei;//手机IMEI号码
    private String t_cbyf;//抄表月份   //NOTICE  给我的数据是没有的，我自己加，方便本地查找
    private String t_card_num;//卡号
    private String t_ticket_name;//开票名称  //NOTICE 这个就是用户名
    private String t_location; //水表地址
    private String t_volume_num;//册号  //NOTICE 表册号
    private String t_caliber; //口径
    private String t_latest_index;//上期读数 //NOTICE 不确定是否需要改成double类型
    private String t_latest_date; //上期抄表日
    private String t_water_property;//用水性质1
    private String t_water_property_two;//用水性质2
    private String t_meter_num;//表身号
    private String t_tel_phone;//电话
    private String t_mobile_phone;//手机
    private String t_setup_time;//安装日期
    private String t_card_time; //建卡日期
    private String t_average;//平均水量 //NOTICE 不确定是否需要改成double类型
    private String t_volume_order;//册内顺序
    private String t_area;//片区
    private String t_jblx;//建表类型
    private String t_x;//纬度
    private String t_y; //经度
    private String t_eid; //水表箱位置（墙/地）
    private String t_eid_bz;//水表箱位置（自定义内容）
    private String t_cur_read_water_sum; //当期用水量
    private String t_cur_read_data;//抄表日期
    private String t_cur_meter_data;//当期水表读数
    private String t_bz;//备注
    private String t_image_name;//照片名（无后缀）
    private String t_image_path;// 照片地址·
    private String isChecked;//NOTICE 判断是否已经拍过照(本地数据库) 0代表已经拍照  1代表未拍照
    private String isUpload;//NOTICE 判断是否已经上传过（本地数据库） 0代表已经上传  1代表未上传
    private String i_id;//图片ID （用于备注是否已抄） 抄过为1 没有为0
    private String t_longitude;//水表箱经度
    private String t_latitude;//水表箱纬度
    private String t_arrears_flag;//是否欠费（true 为欠费 false为正常）
    private String extend_field_string;//针对每个水司补充的字段json
    private String t_one_prior_used_value;// 上期用水量
    private String t_need_img;//是否允许不拍照直接手抄
    private String t_isManual;// 是否手抄 手抄为0 非手抄为1
    private String t_normal_detect;//是否为正常水表 正常为true 反之为false
    private String t_normal_WaterDivision;//是否为正常水司 正常为true 反之为false
    private String t_new_phonenumber;// 最新的手机号
    private String org_code;//水司编号
    private String customer_no;//客户号
    private String auto_detect_flag;//自动识别标志 0代表自动识别未修改 1代表自动识别参数有误 手动修改 2代表默认参数 不识别 -----后台没有
    private String modify_detect_num;//默认参数为空 自动识别错误后 手动修改后 有读数 ----后台没有
    private String change_meter_flag;//默认参数是false 换表标志位 换表是true 非换表是false
    private String old_meter_value;//老表读数-------换表
    private String new_meter_value;//新表读数------换表
    private String ladder_price;//水表阶梯水价
    private String balance;//账户余额
    private String owner_name;//抄表员 表册绑定的抄表员
    private String detail_date_list;// 当前所有已超月份列表 ，分割
    private String used_value_list;//已超月份水量的使用情况  对应月份列表
    private String water_meter_state; //水表状态列表

    public String getWater_meter_state() {
        return water_meter_state;
    }

    public void setWater_meter_state(String water_meter_state) {
        this.water_meter_state = water_meter_state;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getDetail_date_list() {
        return detail_date_list;
    }

    public void setDetail_date_list(String detail_date_list) {
        this.detail_date_list = detail_date_list;
    }

    public String getUsed_value_list() {
        return used_value_list;
    }

    public void setUsed_value_list(String used_value_list) {
        this.used_value_list = used_value_list;
    }

    public String getLadder_price() {
        return ladder_price;
    }

    public void setLadder_price(String ladder_price) {
        this.ladder_price = ladder_price;
    }

    public String getChange_meter_flag() {
        return change_meter_flag;
    }

    public void setChange_meter_flag(String change_meter_flag) {
        this.change_meter_flag = change_meter_flag;
    }

    public String getOld_meter_value() {
        return old_meter_value;
    }

    public void setOld_meter_value(String old_meter_value) {
        this.old_meter_value = old_meter_value;
    }

    public String getNew_meter_value() {
        return new_meter_value;
    }

    public void setNew_meter_value(String new_meter_value) {
        this.new_meter_value = new_meter_value;
    }

    public String getCustomer_no() {
        return customer_no;
    }

    public void setCustomer_no(String customer_no) {
        this.customer_no = customer_no;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getT_new_phonenumber() {
        return t_new_phonenumber;
    }

    public void setT_new_phonenumber(String t_new_phonenumber) {
        this.t_new_phonenumber = t_new_phonenumber;
    }

    public String getT_normal_WaterDivision() {
        return t_normal_WaterDivision;
    }

    public void setT_normal_WaterDivision(String t_normal_WaterDivision) {
        this.t_normal_WaterDivision = t_normal_WaterDivision;
    }

    public String getT_normal_detect() {
        return t_normal_detect;
    }

    public void setT_normal_detect(String t_normal_detect) {
        this.t_normal_detect = t_normal_detect;
    }

    public String getT_isManual() {
        return t_isManual;
    }

    public void setT_isManual(String t_isManual) {
        this.t_isManual = t_isManual;
    }

    public String getT_need_img() {
        return t_need_img;
    }

    public void setT_need_img(String t_need_img) {
        this.t_need_img = t_need_img;
    }

    public String getT_one_prior_used_value() {
        return t_one_prior_used_value;
    }

    public void setT_one_prior_used_value(String t_one_prior_used_value) {
        this.t_one_prior_used_value = t_one_prior_used_value;
    }

    public String getExtend_field_string() {
        return extend_field_string;
    }

    public void setExtend_field_string(String extend_field_string) {
        this.extend_field_string = extend_field_string;
    }

    public String getT_eid_bz() {
        return t_eid_bz;
    }

    public String getT_longitude() {
        return t_longitude;
    }

    public String getT_arrears_flag() {
        return t_arrears_flag;
    }

    public void setT_arrears_flag(String t_arrears_flag) {
        this.t_arrears_flag = t_arrears_flag;
    }

    public void setT_longitude(String t_longitude) {
        this.t_longitude = t_longitude;
    }

    public String getT_latitude() {
        return t_latitude;
    }

    public void setT_latitude(String t_latitude) {
        this.t_latitude = t_latitude;
    }

    public void setT_eid_bz(String t_eid_bz) {
        this.t_eid_bz = t_eid_bz;
    }

    public String getI_id() {
        return i_id;
    }

    public void setI_id(String i_id) {
        this.i_id = i_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getT_image_name() {
        return t_image_name;
    }

    public void setT_image_name(String t_image_name) {
        this.t_image_name = t_image_name;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public String getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(String isUpload) {
        this.isUpload = isUpload;
    }

    public String getT_image_path() {
        return t_image_path;
    }

    public void setT_image_path(String t_image_path) {
        this.t_image_path = t_image_path;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getT_phone_imei() {
        return t_phone_imei;
    }

    public void setT_phone_imei(String t_phone_imei) {
        this.t_phone_imei = t_phone_imei;
    }

    public String getT_cbyf() {
        return t_cbyf;
    }

    public void setT_cbyf(String t_cbyf) {
        this.t_cbyf = t_cbyf;
    }

    public String getT_card_num() {
        return t_card_num;
    }

    public void setT_card_num(String t_card_num) {
        this.t_card_num = t_card_num;
    }

    public String getT_ticket_name() {
        return t_ticket_name;
    }

    public void setT_ticket_name(String t_ticket_name) {
        this.t_ticket_name = t_ticket_name;
    }

    public String getT_location() {
        return t_location;
    }

    public void setT_location(String t_location) {
        this.t_location = t_location;
    }

    public String getT_volume_num() {
        return t_volume_num;
    }

    public void setT_volume_num(String t_volume_num) {
        this.t_volume_num = t_volume_num;
    }

    public String getT_caliber() {
        return t_caliber;
    }

    public void setT_caliber(String t_caliber) {
        this.t_caliber = t_caliber;
    }

    public String getT_latest_index() {
        return t_latest_index;
    }

    public void setT_latest_index(String t_latest_index) {
        this.t_latest_index = t_latest_index;
    }

    public String getT_latest_date() {
        return t_latest_date;
    }

    public void setT_latest_date(String t_latest_date) {
        this.t_latest_date = t_latest_date;
    }

    public String getT_water_property() {
        return t_water_property;
    }

    public void setT_water_property(String t_water_property) {
        this.t_water_property = t_water_property;
    }

    public String getT_water_property_two() {
        return t_water_property_two;
    }

    public void setT_water_property_two(String t_water_property_two) {
        this.t_water_property_two = t_water_property_two;
    }

    public String getT_meter_num() {
        return t_meter_num;
    }

    public void setT_meter_num(String t_meter_num) {
        this.t_meter_num = t_meter_num;
    }

    public String getT_tel_phone() {
        return t_tel_phone;
    }

    public void setT_tel_phone(String t_tel_phone) {
        this.t_tel_phone = t_tel_phone;
    }

    public String getT_mobile_phone() {
        return t_mobile_phone;
    }

    public void setT_mobile_phone(String t_mobile_phone) {
        this.t_mobile_phone = t_mobile_phone;
    }

    public String getT_setup_time() {
        return t_setup_time;
    }

    public void setT_setup_time(String t_setup_time) {
        this.t_setup_time = t_setup_time;
    }

    public String getT_card_time() {
        return t_card_time;
    }

    public void setT_card_time(String t_card_time) {
        this.t_card_time = t_card_time;
    }

    public String getT_average() {
        return t_average;
    }

    public void setT_average(String t_average) {
        this.t_average = t_average;
    }

    public String getT_volume_order() {
        return t_volume_order;
    }

    public void setT_volume_order(String t_volume_order) {
        this.t_volume_order = t_volume_order;
    }

    public String getT_area() {
        return t_area;
    }

    public void setT_area(String t_area) {
        this.t_area = t_area;
    }

    public String getT_jblx() {
        return t_jblx;
    }

    public void setT_jblx(String t_jblx) {
        this.t_jblx = t_jblx;
    }

    public String getT_x() {
        return t_x;
    }

    public void setT_x(String t_x) {
        this.t_x = t_x;
    }

    public String getT_y() {
        return t_y;
    }

    public void setT_y(String t_y) {
        this.t_y = t_y;
    }

    public String getT_eid() {
        return t_eid;
    }

    public void setT_eid(String t_eid) {
        this.t_eid = t_eid;
    }

    public String getT_cur_read_water_sum() {
        return t_cur_read_water_sum;
    }

    public void setT_cur_read_water_sum(String t_cur_read_water_sum) {
        this.t_cur_read_water_sum = t_cur_read_water_sum;
    }

    public String getT_cur_read_data() {
        return t_cur_read_data;
    }

    public void setT_cur_read_data(String t_cur_read_data) {
        this.t_cur_read_data = t_cur_read_data;
    }

    public String getT_cur_meter_data() {
        return t_cur_meter_data;
    }

    public void setT_cur_meter_data(String t_cur_meter_data) {
        this.t_cur_meter_data = t_cur_meter_data;
    }

    public String getT_bz() {
        return t_bz;
    }

    public void setT_bz(String t_bz) {
        this.t_bz = t_bz;
    }

    public String getAuto_detect_flag() {
        return auto_detect_flag;
    }

    public void setAuto_detect_flag(String auto_detect_flag) {
        this.auto_detect_flag = auto_detect_flag;
    }

    public String getModify_detect_num() {
        return modify_detect_num;
    }

    public void setModify_detect_num(String modify_detect_num) {
        this.modify_detect_num = modify_detect_num;
    }
}
