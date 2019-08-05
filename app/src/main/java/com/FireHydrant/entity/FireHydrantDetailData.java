package com.FireHydrant.entity;

import org.litepal.crud.DataSupport;

/*
 *消防栓表册具体数据
 */
public class FireHydrantDetailData extends DataSupport {
    private String record_key_detail;//任务记录id,作为唯一的id使用 上传的时候按原图返回
    private String record_key_list;//消防栓记录id,作为唯一的id使用，上传的时候按原图返回
    private String fire_hydrant_id;//消防栓ID
    private String fire_hydrant_name;//消防栓名称
    private String booklet_no;//表册号
    private String org_code;//机构号
    private String detail_date;//巡查月份
    private String address;//消防栓地址
    private String longitude;//经度
    private String latitude;//纬度
    private String state;//消防栓状态
    private String type;//消防栓类型
    private String last_value;//上期读数
    private String last_date;//上期巡查日期
    private String check_date;//拍照日期
    private String current_water_data;//当月用水量
    private String current_value;//当期读数
    private String isChecked;//NOTICE 判断是否已经拍过照(本地数据库) 0 代表已经拍照  1代表未拍照
    private String isUpload;//NOTICE 判断是否已经上传过（本地数据库） 0  代表已经上传 1代表未上传
    private String imagePathOne;
    private String imagePathTwo;
    private String fileNameOne;
    private String fileNameTwo;

    public String getRecord_key_detail() {
        return record_key_detail;
    }

    public String getCurrent_water_data() {
        return current_water_data;
    }

    public void setCurrent_water_data(String current_water_data) {
        this.current_water_data = current_water_data;
    }

    public void setRecord_key_detail(String record_key_detail) {
        this.record_key_detail = record_key_detail;
    }

    public String getRecord_key_list() {
        return record_key_list;
    }

    public void setRecord_key_list(String record_key_list) {
        this.record_key_list = record_key_list;
    }

    public String getCheck_date() {
        return check_date;
    }

    public void setCheck_date(String check_date) {
        this.check_date = check_date;
    }

    public String getFire_hydrant_id() {
        return fire_hydrant_id;
    }

    public void setFire_hydrant_id(String fire_hydrant_id) {
        this.fire_hydrant_id = fire_hydrant_id;
    }

    public String getFire_hydrant_name() {
        return fire_hydrant_name;
    }

    public void setFire_hydrant_name(String fire_hydrant_name) {
        this.fire_hydrant_name = fire_hydrant_name;
    }

    public String getBooklet_no() {
        return booklet_no;
    }

    public void setBooklet_no(String booklet_no) {
        this.booklet_no = booklet_no;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getDetail_date() {
        return detail_date;
    }

    public void setDetail_date(String detail_date) {
        this.detail_date = detail_date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLast_value() {
        return last_value;
    }

    public void setLast_value(String last_value) {
        this.last_value = last_value;
    }

    public String getLast_date() {
        return last_date;
    }

    public void setLast_date(String last_date) {
        this.last_date = last_date;
    }


    public String getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(String current_value) {
        this.current_value = current_value;
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

    public String getImagePathOne() {
        return imagePathOne;
    }

    public void setImagePathOne(String imagePathOne) {
        this.imagePathOne = imagePathOne;
    }

    public String getImagePathTwo() {
        return imagePathTwo;
    }

    public void setImagePathTwo(String imagePathTwo) {
        this.imagePathTwo = imagePathTwo;
    }

    public String getFileNameOne() {
        return fileNameOne;
    }

    public void setFileNameOne(String fileNameOne) {
        this.fileNameOne = fileNameOne;
    }

    public String getFileNameTwo() {
        return fileNameTwo;
    }

    public void setFileNameTwo(String fileNameTwo) {
        this.fileNameTwo = fileNameTwo;
    }

    @Override
    public String toString() {
        return "FireHydrantDetailData{" +
                "record_key_detail='" + record_key_detail + '\'' +
                ", record_key_list='" + record_key_list + '\'' +
                ", fire_hydrant_id='" + fire_hydrant_id + '\'' +
                ", fire_hydrant_name='" + fire_hydrant_name + '\'' +
                ", booklet_no='" + booklet_no + '\'' +
                ", org_code='" + org_code + '\'' +
                ", detail_date='" + detail_date + '\'' +
                ", address='" + address + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                ", last_value='" + last_value + '\'' +
                ", last_date='" + last_date + '\'' +
                ", check_date='" + check_date + '\'' +
                ", current_water_data='" + current_water_data + '\'' +
                ", current_value='" + current_value + '\'' +
                ", isChecked='" + isChecked + '\'' +
                ", isUpload='" + isUpload + '\'' +
                ", imagePathOne='" + imagePathOne + '\'' +
                ", imagePathTwo='" + imagePathTwo + '\'' +
                ", fileNameOne='" + fileNameOne + '\'' +
                ", fileNameTwo='" + fileNameTwo + '\'' +
                '}';
    }
}
