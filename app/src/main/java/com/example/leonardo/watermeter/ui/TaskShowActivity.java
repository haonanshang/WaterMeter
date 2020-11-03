package com.example.leonardo.watermeter.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blueToothPrinter.BTCallbackInterface;
import com.example.leonardo.watermeter.utils.CameraUtils;
import com.example.leonardo.watermeter.utils.CommanUtils;
import com.example.leonardo.watermeter.utils.FirstModel;
import com.example.leonardo.watermeter.utils.LadderWaterutils;
import com.blueToothPrinter.BluetoothServiceTest;
import com.blueToothPrinter.BluetoothValues;
import com.blueToothPrinter.DeviceListActivity;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.adapter.RecyLadderPriceAdapter;
import com.example.leonardo.watermeter.adapter.RecyWaterYieldAdapter;
import com.example.leonardo.watermeter.adapter.SpaceItemDecoration;
import com.example.leonardo.watermeter.entity.BchData;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.global.GlobalData;
import com.example.leonardo.watermeter.utils.CustomDialog;
import com.example.leonardo.watermeter.utils.FaceThread;
import com.example.leonardo.watermeter.utils.GPSUtils;
import com.example.leonardo.watermeter.utils.LocationTools;
import com.example.leonardo.watermeter.utils.ModifyImage;
import com.example.leonardo.watermeter.utils.PhoneState;
import com.example.leonardo.watermeter.utils.QuantityCalculationUtils;
import com.example.leonardo.watermeter.utils.SecondModel;
import com.example.leonardo.watermeter.utils.SharedPreUtils;
import com.example.leonardo.watermeter.utils.SpecialBrandUtil;
import com.example.leonardo.watermeter.utils.VolumeManage;
import com.example.leonardo.watermeter.utils.WaterBudgetUtils;
import com.example.leonardo.watermeter.utils.WaterStatusDialog;
import com.itgoyo.logtofilelibrary.LogToFileUtils;
import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.ILoginDeviceCallback;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.LoginHelper;
import com.macrovideo.sdk.objects.DeviceInfo;
import com.macrovideo.sdk.objects.LoginParam;
import com.objecteye.sy.wbjnidemo.WBJNIActivity;
import com.prosdk.BindingDeviceActivity;
import com.prosdk.LocalDefines;
import com.prosdk.NVPlayerShowActivity;
import com.zbarScan.CaptureActivity;

import org.apache.commons.lang3.StringUtils;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 抄表界面类
 * 修改CC
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class TaskShowActivity extends Activity implements View.OnClickListener {

    private int taskPosition = -1, isupload;
    private String imgNameHead = "";
    private String cbyf, bch, tid, whichPage, divideNumber;
    private List<DetailData> detailDataListOriginal;
    private DetailData currentData;
    private List<Map<String, Object>> dataList;
    AlertDialog boxBuilder;
    ImageView showImageView, eidLocation, bluetoothPrinter, gpsButton;
    ScrollView scrollView;
    TextView currentRead, currentWaterSum, lastRead, ticketName, mobilePhone, cardNum, meterNum, address, jblxStatus, bzInfo, averageMeterWaterSum, arrears_flag;
    TextView manualButton, moveLastButton, moveNextButton, takePhotoButtonPhone, LastWaterRead, caliber, updatemobilePhone, water_property, customerNo;
    TextView oldRead, newRead, sealNo;
    private int boxwhich;
    private BchData bchData;
    private TextView showDetectNum;
    //蓝牙打印模块
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothServiceTest mService = null;
    //水量列表
    private RecyclerView recyclerView;
    private RecyWaterYieldAdapter recyWaterYieldAdapter;
    //水价列表
    private RecyclerView priceRecyclerView;
    private RecyLadderPriceAdapter recyLadderPriceAdapter;
    //调用相机 指定的Uri
    private Uri imageUri = null;
    //调用相机 保存文件路径
    private String imgTempFilePath = null;

    private int[] boxLocationImgs = {R.drawable.dibiao, R.drawable.qiangbiao};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        /*接收上个页面传来的t_id信息*/
        Bundle bundle = this.getIntent().getExtras();
        cbyf = bundle.getString("cbyf");
        bch = bundle.getString("bch");
        tid = bundle.getString("tid");
        whichPage = bundle.getString("whichPage");
        divideNumber = bundle.getString("divideNumber");
        getDBData(); //根据上个页面传来的值来判断，获取初始化数据
        SetLayoutView();
        initView();
        setisupload();
        initListenner();
        setValues();//设置显示的值
        checkPhoto();//检查图片
        bchData = LitePal.where("cbyf= ? and bch= ?", cbyf, bch).findFirst(BchData.class);
        selectSkipMode(bchData.getIsDetectByPhone());
        moveScrollEnd();//滚动布局到最底部
        setGestureListener();
    }

    /**
     * 设置左滑右滑监听
     */
    private void setGestureListener() {
        final float[] mPosX = new float[1];
        final float[] mPosY = new float[1];
        final float[] mCurPosX = new float[1];
        final float[] mCurPosY = new float[1];
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosX[0] = event.getX();
                        mPosY[0] = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX[0] = event.getX();
                        mCurPosY[0] = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mPosX[0] - mCurPosX[0] > 0 && (Math.abs(mPosX[0] - mCurPosX[0]) > 70) && (Math.abs(mPosY[0] - mCurPosY[0]) < 50)) {
                            //左滑->下一户
                            moveToMethod(9);
                        } else if (mPosX[0] - mCurPosX[0] < 0 && (Math.abs(mPosX[0] - mCurPosX[0]) > 70) && (Math.abs(mPosY[0] - mCurPosY[0]) < 50)) {
                            //右滑->上一户
                            moveToMethod(1);
                        }
                        break;
                }
                return false;
            }
        });

    }

    /**
     * 选择跳转方式
     * 判断表册为手机识别还是后台识别 手机识别下 不允许自动跳转（但是允许设置跳转下一户的时候自动打开相机）  后台识别 允许选择自动跳转
     *
     * @param isDetectByPhone 0 是手机端识别  无自动跳转选项（可以选择是否自动打开相机） 1 是不识别  允许自动跳转
     */
    public void selectSkipMode(String isDetectByPhone) {
        switch (isDetectByPhone) {
            case "0":
                if (!GlobalData.isAutoCamera) {
                    //弹出自动打开相机选择框
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("跳转到下一户时自动打开相机")
                            .setPositiveButton("自动打开", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GlobalData.isAutoCamera = true;
                                }
                            }).setNegativeButton("手动打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalData.isAutoCamera = false;
                        }
                    });
                    builder.show();
                }
                break;
            case "1":
                if (!GlobalData.setAutoJump) {
                    //弹出跳转方式选择框
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("请选择跳转方式")
                            .setPositiveButton("自动跳转", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GlobalData.setAutoJump = true;
                                }
                            }).setNegativeButton("手动跳转", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalData.setAutoJump = false;
                        }
                    });
                    builder.show();
                }
                break;
        }
    }


    /*
     *设置监听
     */
    private void initListenner() {
        ticketName.setOnClickListener(this);
        address.setOnClickListener(this);
        lastRead.setOnClickListener(this);
        LastWaterRead.setOnClickListener(this);
        averageMeterWaterSum.setOnClickListener(this);
        currentRead.setOnClickListener(this);
        bluetoothPrinter.setOnClickListener(this);
        cardNum.setOnClickListener(this);
        meterNum.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ticketName:
                showAllMessage("户名", ticketName.getText().toString());
                break;
            case R.id.address:
                showAllMessage("地址", address.getText().toString());
                break;
            case R.id.lastRead:
                showAllMessage("上期读数", lastRead.getText().toString());
                break;
            case R.id.LastWaterRead:
                showAllMessage("上期水量", LastWaterRead.getText().toString());
                break;
            case R.id.averageMeterWaterSum:
                showAllMessage("平均水量", averageMeterWaterSum.getText().toString());
                break;
            case R.id.currentMeterData:
                showAllMessage("最新读数", currentRead.getText().toString());
                break;
            case R.id.cardNum:
                showAllMessage("卡号", cardNum.getText().toString());
                break;
            case R.id.meterNum:
                meterNumInput(view);
                break;
            case R.id.blue_tooth_printer:
                //先判断当期用水量是否有值
                if (!StringUtils.isEmpty(currentData.getT_cur_read_water_sum())) {
                    //判断是否支持蓝牙
                    if (mBluetoothAdapter != null) {
                        //打开蓝牙
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, BluetoothValues.REQUEST_ENABLE_BT);
                        }
                        if (mService == null) {
                            mService = new BluetoothServiceTest(TaskShowActivity.this, new BTCallbackInterface() {
                                @Override
                                public void setBtStatus(int status) {
                                    switch (status) {
                                        case BluetoothValues.STATE_CONNECTED:
                                            Toast.makeText(TaskShowActivity.this, "打印机已经连接", Toast.LENGTH_SHORT).show();
                                            LadderWaterutils.Print_WaterMeterBill(TaskShowActivity.this, mService, currentData);
                                            break;
                                        case BluetoothValues.STATE_CONNECTED_FAILURED:
                                            Toast.makeText(TaskShowActivity.this, "打印机连接失败", Toast.LENGTH_SHORT).show();
                                            break;
                                        case BluetoothValues.STATE_CONNECTED_INTERRUPT:
                                            Toast.makeText(TaskShowActivity.this, "打印机连接断开", Toast.LENGTH_SHORT).show();
                                            Intent serverIntent = new Intent(TaskShowActivity.this, DeviceListActivity.class);
                                            startActivityForResult(serverIntent, BluetoothValues.REQUEST_CONNECT_DEVICE);
                                            break;
                                        case BluetoothValues.STATE_CONNECTED_FINISH:
                                            Toast.makeText(TaskShowActivity.this, "打印机退出连接", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            });
                        }
                        if (mService.getState() != BluetoothValues.STATE_CONNECTED) {
                            Intent serverIntent = new Intent(TaskShowActivity.this, DeviceListActivity.class);
                            startActivityForResult(serverIntent, BluetoothValues.REQUEST_CONNECT_DEVICE);
                        }
                        if (mService.getState() == BluetoothValues.STATE_CONNECTED) {
                            LadderWaterutils.Print_WaterMeterBill(TaskShowActivity.this, mService, currentData);
                        }
                    } else {
                        Toast.makeText(TaskShowActivity.this, "蓝牙功能不支持", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TaskShowActivity.this, "当期用水量为空，请选择自动识别模式或者手抄", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    /*
     *显示控件的全部数据
     */
    public void showAllMessage(String title, String message) {
        final TextView showTv = new TextView(this);
        showTv.setTextSize(18);
        showTv.setGravity(Gravity.CENTER);
        showTv.setPadding(10, 5, 10, 30);
        showTv.setText(message);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setView(showTv);
        builder.show();
    }

    private void SetLayoutView() {
        setContentView(R.layout.act_tasklist_view);
    }

    private void setisupload() {
        isupload = Integer.valueOf(currentData.getIsUpload());
    }

    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.contentScroll);
        lastRead = (TextView) findViewById(R.id.lastRead);
        LastWaterRead = (TextView) findViewById(R.id.LastWaterRead);
        currentRead = (TextView) findViewById(R.id.currentMeterData);
        currentWaterSum = (TextView) findViewById(R.id.currentMeterWaterSum);
        ticketName = (TextView) findViewById(R.id.ticketName);
        mobilePhone = (TextView) findViewById(R.id.mobilePhone);
        cardNum = (TextView) findViewById(R.id.cardNum);
        meterNum = (TextView) findViewById(R.id.meterNum);
        address = (TextView) findViewById(R.id.address);
        caliber = (TextView) findViewById(R.id.caliber);
        jblxStatus = (TextView) findViewById(R.id.jblxStatus);
        bzInfo = (TextView) findViewById(R.id.bzInfo);
        updatemobilePhone = (TextView) findViewById(R.id.updatemobilePhone);
        manualButton = (TextView) findViewById(R.id.manualButton);
        takePhotoButtonPhone = (TextView) findViewById(R.id.takePhotoButtonPhone);
        moveLastButton = (TextView) findViewById(R.id.moveLastButton);
        moveNextButton = (TextView) findViewById(R.id.moveNextButton);
        showImageView = (ImageView) findViewById(R.id.showImageView);
        eidLocation = (ImageView) findViewById(R.id.eidLocation);
        arrears_flag = (TextView) findViewById(R.id.arrears_flag);
        averageMeterWaterSum = (TextView) findViewById(R.id.averageMeterWaterSum);
        water_property = (TextView) findViewById(R.id.water_property);
        customerNo = findViewById(R.id.customer_no);
        showDetectNum = findViewById(R.id.showDetectNum);
        showDetectNum.setVisibility(View.INVISIBLE);
        gpsButton = findViewById(R.id.GPSButton);
        oldRead = findViewById(R.id.oldRead);
        newRead = findViewById(R.id.newRead);
        sealNo = findViewById(R.id.sealNo);
        //蓝牙打印模块
        bluetoothPrinter = findViewById(R.id.blue_tooth_printer);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //初始化表位置图片资源
        dataList = new ArrayList<Map<String, Object>>();
        recyclerView = findViewById(R.id.water_yield_recyclerview);
        priceRecyclerView = findViewById(R.id.ladder_price_recyclerview);
        for (int i = 0; i < boxLocationImgs.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", boxLocationImgs[i]);
            dataList.add(map);
        }
        initRecylerView();
    }

    /**
     * 初始化recylerView
     */
    private void initRecylerView() {
        //用水量显示
        LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        recyclerView.setLayoutManager(ms); //给RecyClerView 添加设置好的布局样式
        recyclerView.addItemDecoration(new SpaceItemDecoration(25, 0));
        //水价显示
        LinearLayoutManager priceMs = new LinearLayoutManager(this);
        priceMs.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        priceRecyclerView.setLayoutManager(priceMs); //给RecyClerView 添加设置好的布局样式
        priceRecyclerView.addItemDecoration(new SpaceItemDecoration(25, 0));
    }

    /*
     *扫描条形码
     */
    public void scanEvent(View view) {
        Intent intent = new Intent(TaskShowActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("t_card_num", currentData.getT_card_num());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /*
    获取数据库的值
     */
    private void getDBData() {
        if (whichPage.equals("001")) {//总列表
            detailDataListOriginal = LitePal.where("t_cbyf = ? and t_volume_num = ? and divideNumber = ? ", cbyf, bch, divideNumber).find(DetailData.class);
        } else if (whichPage.equals("002")) {//待抄表数据
            detailDataListOriginal = LitePal.where("t_cbyf = ? and t_volume_num = ? and isChecked = ? and divideNumber = ? ", cbyf, bch, "1", divideNumber).find(DetailData.class);
        } else {
            detailDataListOriginal = LitePal.where("t_cbyf = ? and t_volume_num = ? and isChecked = ? and divideNumber = ?  ", cbyf, bch, "0", divideNumber).find(DetailData.class);
        }
        currentData = LitePal.where("t_id = ?", tid).findFirst(DetailData.class);
        /*
        判断当前任务所在任务列表的位置
         */
        for (int i = 0; i < detailDataListOriginal.size(); i++) {
            if (detailDataListOriginal.get(i).getT_volume_order().equals(currentData.getT_volume_order())) {
                taskPosition = i;
            }
        }
    }

    /*
    检测原本是否有图片
     */
    private void checkPhoto() {
        /*检查是否有拍照图片*/
        if (!StringUtils.isEmpty(currentData.getT_image_path())) {
            File picFile = new File(currentData.getT_image_path());
            Bitmap bitmap;
            if (picFile.exists()) {
                try {
                    bitmap = BitmapFactory.decodeFile(currentData.getT_image_path());
                    showImageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    showImageView.setImageResource(R.mipmap.ic_default);
                }
            } else {
                showImageView.setImageResource(R.mipmap.ic_default);
            }
        } else {
            showImageView.setImageResource(R.drawable.show_img_d);
        }
        /*检查是否有水表位置图片*/
        if (!currentData.getT_eid().isEmpty() && !currentData.getT_eid().equals("")) {
            eidLocation.setImageResource(boxLocationImgs[Integer.parseInt(currentData.getT_eid())]);
        } else {
            eidLocation.setImageResource(R.drawable.tanklocation);
        }


    }

    /*
    为了让滚动页面显示最底部
     */
    private void moveScrollEnd() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                TaskShowActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        }, 100);
    }

    /*
      跳转到补充字段界面
     */
    public void Intent_extendField(View v) {
        if (!StringUtils.isEmpty(currentData.getExtend_field_string()) || !StringUtils.isEmpty(currentData.getT_normal_detect())) {
            Intent intent = new Intent(TaskShowActivity.this, ExtendsFieldActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("t_id", currentData.getT_id());
            intent.putExtras(bundle);
            startActivity(intent);
        }


    }

    /*
    设置要显示的值
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setValues() {
        setLocation();
        lastRead.setText((currentData.getT_latest_index()));
        LastWaterRead.setText(currentData.getT_one_prior_used_value());
        currentRead.setText(currentData.getT_cur_meter_data());
        currentWaterSum.setText(currentData.getT_cur_read_water_sum());
        if (currentData.getT_isManual().equals("0")) {
            currentRead.setTextColor(getResources().getColor(R.color.red));
            currentWaterSum.setTextColor(getResources().getColor(R.color.red));
        } else {
            currentRead.setTextColor(getResources().getColor(R.color.cpb_green_dark));
            currentWaterSum.setTextColor(getResources().getColor(R.color.cpb_green_dark));
        }
        ticketName.setText(currentData.getT_ticket_name());
        mobilePhone.setText(currentData.getT_mobile_phone());
        updatemobilePhone.setText(currentData.getT_tel_phone());
        cardNum.setText(currentData.getT_card_num());
        if (currentData.getWaterType() != null) {
            if (currentData.getWaterType().equals("30")) {
                cardNum.setText("(总表)" + currentData.getT_card_num());
            }
        }
        meterNum.setText(currentData.getT_meter_num());
        address.setText(currentData.getT_location());
        caliber.setText(currentData.getT_caliber());
        averageMeterWaterSum.setText(currentData.getT_average());
        arrears_flag.setText(GetarrearsText());
        water_property.setText(currentData.getT_water_property());
//        updatemobilePhone.setText(currentData.getT_new_phonenumber());
        arrears_flag.setTextColor(getResources().getColor(SetarrearsColor()));
        customerNo.setText(currentData.getCustomer_no());
        imgNameHead = currentData.getT_cbyf() + "_" + currentData.getT_id() + "_" + currentData.getT_volume_num() + "_" + currentData.getT_card_num() + "_";
        if (currentData.getT_bz().isEmpty() || currentData.getT_bz().equals("") || currentData.getT_bz().toString().length() < 1) {
            bzInfo.setText("请输入备注信息");
        } else {
            bzInfo.setText(currentData.getT_bz());
        }
        if (currentData.getT_jblx().isEmpty()) {
            jblxStatus.setText("请选择表状态");
        } else {
            String statusStr = convertWaterStatus();
            if (statusStr != null) {
                jblxStatus.setText(statusStr);
            } else {
                jblxStatus.setText("状态无效");
            }
        }
        oldRead.setText(currentData.getOld_meter_value());
        newRead.setText(currentData.getNew_meter_value());
        sealNo.setText(currentData.getSealNo());
        //水量RecylerView以及水价RecylerView数据加载
        initRecyDatas();
    }

    /**
     * 将水表状态转化为字符串显示
     *
     * @return
     */
    private String convertWaterStatus() {
        List<FirstModel> waterStatusList = parseWaterMeterState();
        boolean isMultilevel = judgeMultilevelWaterStatus(currentData.getT_jblx());
        AtomicReference<String> statusStr = new AtomicReference<>();
        for (FirstModel firstModel : waterStatusList) {
            if (isMultilevel) {
                if (firstModel.getId().equals(currentData.getT_jblx().split("_")[0])) {
                    for (SecondModel secondModel : firstModel.getChild()) {
                        if (secondModel.getId().equals(currentData.getT_jblx().split("_")[1])) {
                            statusStr.set(secondModel.getName());
                            break;
                        }
                    }
                }
            } else {
                if (firstModel.getId().equals(currentData.getT_jblx())) {
                    statusStr.set(firstModel.getName());
                    break;
                }
            }
        }
        return statusStr.get();
    }

    /**
     * 判断水表状态是否是多级的水表状态
     *
     * @param jblx
     * @return
     */
    private boolean judgeMultilevelWaterStatus(String jblx) {
        if (jblx.contains("_")) {
            return true;
        }
        return false;
    }

    /**
     * 设置用水量显示  倒序 以及水价显示
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initRecyDatas() {
        //水量
        recyWaterYieldAdapter = new RecyWaterYieldAdapter(this, WaterBudgetUtils.getWaterYieldList(currentData.getDetail_date_list(), currentData.getUsed_value_list()));//初始化适配器
        recyclerView.setAdapter(recyWaterYieldAdapter); // 对 recyclerview 添加数据内容
        //水价
        recyLadderPriceAdapter = new RecyLadderPriceAdapter(this, LadderWaterutils.getUsedLadderPriceBeanList(currentData.getLadder_price(), currentData.getT_cur_read_water_sum()));
        priceRecyclerView.setAdapter(recyLadderPriceAdapter); // 对 recyclerview 添加数据内容
    }

    /**
     * 设置平均水量字段颜色
     *
     * @return
     */
    private int SetarrearsColor() {
        return currentData.getT_arrears_flag().equals("0") ? R.color.black : R.color.red;
    }

    /**
     * 获取平均水量字段
     *
     * @return
     */
    private String GetarrearsText() {
        return currentData.getT_arrears_flag().equals("false") ? "正常" : (currentData.getT_arrears_flag().equals("true") ? "欠费" : "无数据");
    }


    /*
    修改、查看备注信息
     */
    public void editNote(View view) {
        final EditText editText = new EditText(this);
        editText.setText(currentData.getT_bz());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("备注信息").setView(editText)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText() != null) {
                            /*更新数据库中当前Task信息*/
                            ContentValues values = new ContentValues();
                            values.put("t_bz", editText.getText().toString());
                            LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
                            currentData.setT_bz(editText.getText().toString());
                            bzInfo.setText(currentData.getT_bz());
                            Toast.makeText(TaskShowActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TaskShowActivity.this, "没有输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("取消", null);
        builder.show();
    }

    /*
    打开地图
     */
    public void getLocation(View view) {
        if (PhoneState.isNetworkAvailable(this)) {
            Intent intent = new Intent(TaskShowActivity.this, MapActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("tid", currentData.getT_id());
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(TaskShowActivity.this, "当前无可用网络", Toast.LENGTH_SHORT).show();
        }
    }

    public void manualOldInput(View view) {
        System.out.println("触发1");
        if (isupload == 0) {
            System.out.println("触发2");
            Toast.makeText(getApplicationContext(), "数据已经上传，不可再手动修改", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("触发3");
            CustomDialog dialog = new CustomDialog(this, currentData, 1);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //LocationTools.Stoplocayion();
                }
            });
            dialog.show();
        }
    }

    public void manualNewInput(View view) {
        System.out.println("触发1");
        if (isupload == 0) {
            System.out.println("触发2");
            Toast.makeText(getApplicationContext(), "数据已经上传，不可再手动修改", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("触发3");
            CustomDialog dialog = new CustomDialog(this, currentData, 2);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //LocationTools.Stoplocayion();
                }
            });
            dialog.show();
        }
    }

    public void meterNumInput(View view) {
        System.out.println("触发1");
        if (isupload == 0) {
            System.out.println("触发2");
            Toast.makeText(getApplicationContext(), "数据已经上传，不可再手动修改", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("触发3");
            CustomDialog dialog = new CustomDialog(this, currentData, 3);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //LocationTools.Stoplocayion();
                }
            });
            dialog.show();
        }
    }

    public void sealNoInput(View view) {
        System.out.println("触发1");
        if (isupload == 0) {
            System.out.println("触发2");
            Toast.makeText(getApplicationContext(), "数据已经上传，不可再手动修改", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("触发3");
            CustomDialog dialog = new CustomDialog(this, currentData, 4);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //LocationTools.Stoplocayion();
                }
            });
            dialog.show();
        }
    }

    /*手抄数据——点击事件*/
    public void manualInput(View view) {
        if (isupload == 0) {
            Toast.makeText(getApplicationContext(), "数据已经上传，不可再手动修改", Toast.LENGTH_SHORT).show();
        } else {
            //首先判断是否允许直接手抄
            if (!currentData.getT_need_img().equals("true")) {
                LocationTools.SetLocation(TaskShowActivity.this);
                CustomDialog dialog = new CustomDialog(this, currentData, 0);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        LocationTools.Stoplocayion();
                    }
                });
                dialog.show();
            } else {
                /**
                 * 判断是否允许手抄包括两种方式
                 *  - 拍照之后,手动修改识别读数
                 *  - 水表状态异常，将水表状态更改为异常状态也可以支持手抄
                 */
                if (currentData.getIsChecked().equals("0")) {
                    LocationTools.SetLocation(TaskShowActivity.this);
                    CustomDialog dialog = new CustomDialog(this, currentData, 0);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            LocationTools.Stoplocayion();
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(TaskShowActivity.this, "请先拍照", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 移动到上一户
     */
    public void moveToLast(View view) {
        moveToMethod(1);
    }

    /**
     * 移动到下一户
     */
    public void moveToNext(View view) {
        moveToMethod(9);
    }

    @SuppressLint("NewApi")
    public void moveToMethod(int direction) {//1往前走  9往后走
        /**
         * 切换上下户的时候 隐藏自动识别的读数
         */
        showDetectNum.setVisibility(View.INVISIBLE);
        if (direction == 1) {//上一户
            if (taskPosition == 0) {
                Toast.makeText(TaskShowActivity.this, "已经是第一户了", Toast.LENGTH_SHORT).show();
            } else {
                taskPosition = taskPosition - 1;
                currentData = detailDataListOriginal.get(taskPosition);
                changeTask(direction);
            }
        } else {//下一户
            if (taskPosition == detailDataListOriginal.size() - 1) {
                Toast.makeText(TaskShowActivity.this, "已经是最后一户了", Toast.LENGTH_SHORT).show();
            } else {
                taskPosition = taskPosition + 1;
                currentData = detailDataListOriginal.get(taskPosition);
                changeTask(direction);
            }
        }

    }

    /**
     * @param direction 1-下一户  9-下一户 0-其他
     */
    public void changeTask(int direction) {
        currentData = LitePal.where("t_id = ?", currentData.getT_id()).findFirst(DetailData.class);
        tid = currentData.getT_id();
        setValues();
        setisupload();
        checkPhoto();
        //只有跳转下一户的时候才选择是否自动打开相机
        if (direction == 9 && GlobalData.isAutoCamera) {
            intentPhoto();
        }
    }


    /**
     * 改变水表的状态位——点击事件
     * * @param view
     */
    public void changeStatus(View view) {
        new WaterStatusDialog(this, new WaterStatusDialog.StatusPickedListener() {
            @Override
            public void onStatusPicked(FirstModel firstModel, SecondModel secondModel) {
                String waterStatusCode = null;
                String jblxCode = null;
                if (secondModel != null) {
                    jblxStatus.setText(secondModel.getName());
                    waterStatusCode = secondModel.getId();
                    jblxCode = firstModel.getId() + "_" + secondModel.getId();
                } else {
                    jblxStatus.setText(firstModel.getName());
                    waterStatusCode = firstModel.getId();
                    jblxCode = firstModel.getId();
                }
                /**
                 * 判断水表状态是不是异常状态
                 * 在更改水表状态时默认水表状态为正常的
                 * 更新数据库中当前Task信息
                 */
                ContentValues values = new ContentValues();
                String water_data_exception_status = "0";
                if (!currentData.getNormal_value().equals(waterStatusCode)) {
                    values.put("isChecked", "0");
                    water_data_exception_status = "1";
                    currentData.setIsChecked("0");
                }
                values.put("t_jblx", jblxCode);
                values.put("water_data_exception_status", water_data_exception_status);
                LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
            }
        }, parseWaterMeterState());
    }


    /**
     * 解析水表状态字符串
     */
    public List<FirstModel> parseWaterMeterState() {
        List<FirstModel> mDataList = new ArrayList<>();
        JSONArray waterStateArray = JSONObject.parseArray(currentData.getWater_meter_state());
        waterStateArray.stream().forEach(object -> {
            JSONObject waterStatus = JSONObject.parseObject(JSONObject.toJSONString(object));
            FirstModel firstModel = new FirstModel();
            if (waterStatus.containsKey("children")) {
                JSONArray childrenArray = waterStatus.getJSONArray("children");
                List<SecondModel> childrenList = new ArrayList<>();
                childrenArray.stream().forEach(children -> {
                    JSONObject childrenStatus = JSONObject.parseObject(JSONObject.toJSONString(children));
                    SecondModel secondModel = new SecondModel();
                    secondModel.setId(childrenStatus.getString("value"));
                    secondModel.setName(childrenStatus.getString("name"));
                    childrenList.add(secondModel);
                });
                firstModel.setChild(childrenList);
            }
            firstModel.setId(waterStatus.getString("value"));
            firstModel.setName(waterStatus.getString("name"));
            mDataList.add(firstModel);
        });
        return mDataList;
    }


    /**
     * 选择水表箱的位置-点击事件
     *
     * @param view
     */
    public void setBoxLocation(View view) {
        boxBuilder = new AlertDialog.Builder(TaskShowActivity.this)
                .setTitle("请选择表箱所在的位置")
                .setSingleChoiceItems(new String[]{"地表", "墙表"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boxwhich = which;
                    }
                })
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boxBuilder.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eidLocation.setImageResource(boxLocationImgs[boxwhich]);
                        currentData.setT_eid((boxwhich) + "");
                        /*更新数据库中当前Task信息*/
                        ContentValues values = new ContentValues();
                        values.put("t_eid", boxwhich);
                        LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
                        boxBuilder.dismiss();
                    }
                })
                .create();
        boxBuilder.show();
    }

    /**
     * 停止更新定位 并获取定位信息
     */
    public void getLocationInfo() {
        LocationTools.Stoplocayion();
        /*更新数据库中当前Task信息*/
        ContentValues values = new ContentValues();
        values.put("t_x", GlobalData.currentLatitude);
        values.put("t_y", GlobalData.currentLongitude);
        LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
        /*更新当前页面使用的currentData*/
        currentData.setT_x(GlobalData.currentLatitude);
        currentData.setT_y(GlobalData.currentLongitude);
        setLocation();
        /*重置*/
        GlobalData.currentLongitude = "0";
        GlobalData.currentLatitude = "0";
    }

    /**
     * 设置经纬度
     */
    private void setLocation() {
        String latitude = currentData.getT_x();
        String longitude = currentData.getT_y();
        if (latitude.equals("4.9E-324") || longitude.equals("4.9E-324")) {
            gpsButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_failure));
        } else {
            gpsButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_success));
        }
    }

    /**
     * 内窥镜拍照
     *
     * @param view
     */
    public void takePhotoUSB(View view) {
        String IvaWater_Path = GlobalData.getFilePath(this, GlobalData.IVAWATER_PATH);
        if (isupload == 0) {
            Toast.makeText(getApplicationContext(), "数据已经上传，不可再手动修改", Toast.LENGTH_SHORT).show();
        } else {
            if (GPSUtils.GPSIsOPen(TaskShowActivity.this)) {
                int requestCode = -1;
                String imagePath = imgNameHead + System.currentTimeMillis() + "_1.jpg";
                File appDir = new File(IvaWater_Path);
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                final String imgFilePath = IvaWater_Path + "/" + imagePath; //图片文件的绝对路径
                LocationTools.SetLocation(TaskShowActivity.this);
                int deviceType = SharedPreUtils.getDeviceType(TaskShowActivity.this);
                Intent intent = null;
                Bundle bundle = new Bundle();
                if (deviceType == GlobalData.EXTERNAL_DEVICE_ONE) {
                    intent = new Intent(TaskShowActivity.this, WifiActivity.class);
                    requestCode = 101;
                } else if (deviceType == GlobalData.EXTERNAL_DEVICE_TWO) {
                    intent = new Intent(TaskShowActivity.this, WBJNIActivity.class);
                    requestCode = 100;
                } else if (deviceType == GlobalData.EXTERNAL_DEVICE_THREE) {
                    intent = new Intent(TaskShowActivity.this, NVPlayerShowActivity.class);
                    requestCode = 104;
                    bundle.putBoolean("isOnlyShow", false);
                }
                bundle.putBoolean("isFireHydrant", false);
                bundle.putString("imagePath", imgFilePath);
                intent.putExtras(bundle);
                startActivityForResult(intent, requestCode);
            } else {
                Toast.makeText(TaskShowActivity.this, "GPS定位未打开，请打开GPS确保定位准确", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 删除当前显示照片
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deletephoto(View view) {
        if (currentData.getT_image_path() != null && !currentData.getT_image_path().equals("")) {
            File picFile = new File(currentData.getT_image_path());
            if (picFile.exists()) {
                picFile.delete();
                ContentValues values = new ContentValues();
                values.put("t_image_path", "");
                LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
                changeTask(0);
            } else {
                Toast.makeText(TaskShowActivity.this, "本地图片已经删除", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(TaskShowActivity.this, "当前任务未拍照", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 修改手机号
     */
    public void updatemobilePhone1(View view) {
        final EditText editText = new EditText(this);
        editText.setText(currentData.getT_mobile_phone());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改手机号").setView(editText)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText() != null) {
                            /*更新数据库中当前Task信息*/
                            ContentValues values = new ContentValues();
                            values.put("t_new_phonenumber", editText.getText().toString());
                            values.put("t_mobile_phone", editText.getText().toString());
                            LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
                            currentData.setT_new_phonenumber(editText.getText().toString());
                            mobilePhone.setText(currentData.getT_new_phonenumber());
                            Toast.makeText(TaskShowActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TaskShowActivity.this, "没有输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("取消", null);
        builder.show();

    }

    /**
     * 修改手机号2
     */
    public void updatemobilePhone2(View view) {
        final EditText editText = new EditText(this);
        editText.setText(currentData.getT_mobile_phone2());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改手机号").setView(editText)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText() != null) {
                            /*更新数据库中当前Task信息*/
                            ContentValues values = new ContentValues();
                            values.put("t_new_phonenumber2", editText.getText().toString());
                            values.put("t_tel_phone", editText.getText().toString());
                            LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
                            currentData.setT_new_phonenumber2(editText.getText().toString());
                            updatemobilePhone.setText(currentData.getT_new_phonenumber2());
                            Toast.makeText(TaskShowActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TaskShowActivity.this, "没有输入信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("取消", null);
        builder.show();

    }

    /**
     * 相机拍照
     *
     * @param view
     */
    public void takePhotoPhone(View view) {
        intentPhoto();
    }

    /**
     * 相机拍照
     */
    public void intentPhoto() {
        String IvaWaterMeter_Path = GlobalData.getFilePath(this, GlobalData.IVAWATERMETER_PATH);
        if (isupload == 0) {
            Toast.makeText(getApplicationContext(), "数据已经上传，不可在手动修改", Toast.LENGTH_SHORT).show();
        } else {
            if (GPSUtils.GPSIsOPen(this)) {
                String imagePath = imgNameHead + System.currentTimeMillis() + "_0.jpg";
                File appDir = new File(IvaWaterMeter_Path);
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                imgTempFilePath = IvaWaterMeter_Path + "/" + imagePath; //图片文件的绝对路径
                LocationTools.SetLocation(TaskShowActivity.this);
                if (CommanUtils.isUniqueBrand()) {
                    //调用系统照相机
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageUri = CameraUtils.getOutputMediaFileUri(this, imgTempFilePath);
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    openCameraIntent.putExtra("android.intent.extra.quickCapture", true);//启用快捷拍照
                    //Android7.0添加临时权限标记，此步千万别忘了
                    openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(openCameraIntent, 103);
                } else {
                    //打开自定义照相机
                    Intent intent = new Intent(TaskShowActivity.this, CustomCameraActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isFireHydrant", false);
                    bundle.putString("imgFilePath", imgTempFilePath);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 102);
                }
            } else {
                Toast.makeText(this, "GPS定位未打开，请打开GPS确保定位准确", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Activity回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100://新的硬件盒子拍照
                if (resultCode == 1) {
                    boolean isExist = data.getExtras().getBoolean("isExist");
                    if (isExist) {
                        String localImageStr = data.getExtras().getString("imageStr");
                        /**
                         * 显示照片
                         */
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM-dd HH:mm:ss");
                        String waterMarkTime = simpleDateFormat1.format(new java.util.Date());
                        Bitmap bmp = ModifyImage.getimage(localImageStr, currentData.getT_card_num() + "     " + waterMarkTime, 0, 22);
                        showImageView.setImageBitmap(bmp);
                        updateDataAfterPhoto(localImageStr);
                    }
                }
                break;
            case 101://老的硬件盒子拍照
                if (resultCode == 1) {
                    boolean isExist = data.getExtras().getBoolean("isExist");
                    if (isExist) {
                        String localImageStr = data.getExtras().getString("imageStr");
                        /**
                         * 显示照片
                         */
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM-dd HH:mm:ss");
                        String waterMarkTime = simpleDateFormat1.format(new java.util.Date());
                        Bitmap bmp = ModifyImage.getimage(localImageStr, currentData.getT_card_num() + "     " + waterMarkTime, 0, 22);
                        showImageView.setImageBitmap(bmp);
                        updateDataAfterPhoto(localImageStr);
                    }
                }
                break;
            case 102://自定义相机拍照
                if (resultCode == Activity.RESULT_OK) {
                    String localImageStr = data.getExtras().getString("imageStr");
                    /**
                     * 显示照片
                     */
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM-dd HH:mm:ss");
                    String waterMarkTime = simpleDateFormat1.format(new java.util.Date());
                    System.out.println("localImageStr::" + localImageStr);
                    System.out.println("customCamera::brand::" + Build.MODEL);
                    Bitmap bmp = ModifyImage.getimage(localImageStr, currentData.getT_card_num() + "     " + waterMarkTime, SpecialBrandUtil.getCustomCameraDegree(Build.MODEL), 22);
                    showImageView.setImageBitmap(bmp);
                    updateDataAfterPhoto(localImageStr);
                }
                break;
            case 103://调用系统相机拍照
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (imageUri != null && !StringUtils.isEmpty(imgTempFilePath)) {
                            /**
                             * 显示照片
                             */
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM-dd HH:mm:ss");
                            String waterMarkTime = simpleDateFormat1.format(new java.util.Date());
                            Bitmap bitmap = ModifyImage.getBitmapFormUri(TaskShowActivity.this, imgTempFilePath, imageUri, currentData.getT_card_num() + "   " + waterMarkTime);
                            showImageView.setImageBitmap(bitmap);
                            updateDataAfterPhoto(imgTempFilePath);
                            imageUri = null;
                            imgTempFilePath = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 104://调用proV380
                if (resultCode == LocalDefines.RESULT_SUCCESSFULE) {
                    String localImageStr = data.getExtras().getString("imageStr");
                    //显示照片
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM-dd HH:mm:ss");
                    String waterMarkTime = simpleDateFormat1.format(new java.util.Date());
                    Bitmap bmp = ModifyImage.getimage(localImageStr, currentData.getT_card_num() + "     " + waterMarkTime, 90, 36);
                    showImageView.setImageBitmap(bmp);
                    updateDataAfterPhoto(localImageStr);
                }
                break;
            case BluetoothValues.REQUEST_CONNECT_DEVICE://蓝牙连接
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    if (BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothDevice device = mBluetoothAdapter
                                .getRemoteDevice(address);
                        // Attempt to connect to the device
                        mService.connect(device);
                    }
                }
                break;
            case BluetoothValues.REQUEST_ENABLE_BT://开启蓝牙
                // When the request to enable Bluetooth returns
                if (resultCode != Activity.RESULT_OK) {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "蓝牙功能未打开，请手动打开蓝牙", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 拍照后更新数据库的信息
     *
     * @param imagePath 图片路径
     */
    public void updateDataAfterPhoto(String imagePath) {
        try {
            /**
             * 判断是否支持手机端识别 0代表支持手机端识别 1代表不支持手机端识别
             */
            if (bchData.getIsDetectByPhone().equals("0")) {
                new FaceThread(TaskShowActivity.this, imagePath);
            }
            /**
             * 写入数据库
             */
            File localFile = new File(imagePath);
            String imageName = localFile.getName();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String imgTime = simpleDateFormat.format(new java.util.Date());
            /*定位信息*/
            getLocationInfo();
            /*更新当前Task信息*/
            currentData.setT_image_path(imagePath);
            currentData.setT_image_name(imageName.substring(0, imageName.lastIndexOf(".")));
            currentData.setIsChecked("0");
            currentData.setT_cur_read_data(imgTime);
            /*更新数据库中当前Task信息*/
            ContentValues values = new ContentValues();
            values.put("t_image_path", imagePath);
            values.put("t_image_name", imageName.substring(0, imageName.lastIndexOf(".")));
            values.put("isChecked", "0");
            values.put("t_cur_read_data", imgTime);

            System.out.println("新增测试开始");
            LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());

            DetailData test = LitePal.where("t_id = ?", currentData.getT_id()).findFirst(DetailData.class);


            /**
             * 在非自动模式下 才允许设置自动跳转
             */
            if (bchData.getIsDetectByPhone().equals("1")) {
                /*判断跳转方式*/
                if (GlobalData.setAutoJump) {
                    moveToMethod(9);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新水表读数
     *
     * @param meterData
     */
    public void updateMeterData(String meterData) {
        String curMeterData = null;
        try {
            //区分末尾红字  true代表正常水表 false 代表末尾红字
            if (!StringUtils.isEmpty(currentData.getT_normal_detect())) {
                if (currentData.getT_normal_detect().equals("true")) {
                    curMeterData = meterData;
                } else {
                    curMeterData = meterData.substring(0, meterData.length() - 1);
                }
            } else {
                curMeterData = meterData;
            }
            float currentReadWaterSum = Float.valueOf(WaterBudgetUtils.getCurrentWaterVloume(currentData.getChange_meter_flag(), currentData.getOld_meter_value(), currentData.getNew_meter_value(), currentData.getT_latest_index(), curMeterData));
            if (currentReadWaterSum >= 0) {
                if (!QuantityCalculationUtils.WaterEstimat(currentData.getT_average(), String.valueOf(currentReadWaterSum))) {
                    if (SharedPreUtils.getReminderDialogStatus(this)) {
                        CommanUtils.showReminderDialog(this, "本月用水量超过平均值");
                    } else {
                        Toast.makeText(this, "本月用水量超过平均值", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                if (SharedPreUtils.getReminderDialogStatus(this)) {
                    CommanUtils.showReminderDialog(this, "当前用水量小于0，请检查识别结果，若识别结果无误请联系相关人员");
                } else {
                    Toast.makeText(this, "当前用水量小于0，请检查识别结果，若识别结果无误请联系相关人员", Toast.LENGTH_LONG).show();
                }
            }
            ContentValues values = new ContentValues();
            values.put("t_cur_meter_data", curMeterData);
            values.put("t_isManual", "1");
            values.put("auto_detect_flag", "0");
            values.put(" modify_detect_num", "");
            values.put("t_cur_read_water_sum", String.valueOf(currentReadWaterSum));
            System.out.println("新增测试开始");
            LitePal.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());

            DetailData test = LitePal.where("t_id = ?", currentData.getT_id()).findFirst(DetailData.class);

            /****/
            showDetectNum.setText(curMeterData);
            showDetectNum.setVisibility(View.VISIBLE);
            currentRead.setText(curMeterData);
            currentRead.setTextColor(getResources().getColor(R.color.cpb_green_dark));
            currentWaterSum.setText(String.valueOf(currentReadWaterSum));
            currentWaterSum.setTextColor(getResources().getColor(R.color.cpb_green_dark));
            currentData = LitePal.where("t_id = ?", currentData.getT_id()).findFirst(DetailData.class);
        } catch (Exception e) {
            e.printStackTrace();
            LogToFileUtils.write(e.toString());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                getDBData();
                Bundle bundle = new Bundle();
                bundle.putInt("postion", taskPosition);
                Intent intent = new Intent(TaskShowActivity.this, TaskListActivity.class);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                TaskShowActivity.this.finish();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                VolumeManage.SetVolume(this);
                intentPhoto();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        currentData = LitePal.where("t_id = ?", currentData.getT_id()).findFirst(DetailData.class);
        tid = currentData.getT_id();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
            mService = null;
        }
    }

}
