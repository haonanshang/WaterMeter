package com.example.leonardo.watermeter.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.FireHydrant.entity.FireHydrantBchData;
import com.FireHydrant.entity.FireHydrantDetailData;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.BchData;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.interfaces.UploadTaskInterface;
import com.example.leonardo.watermeter.utils.DialogUtils;
import com.example.leonardo.watermeter.utils.DownloadData;
import com.example.leonardo.watermeter.utils.GlobalVariables;
import com.example.leonardo.watermeter.utils.Month;
import com.example.leonardo.watermeter.utils.NetWorkUtils;
import com.example.leonardo.watermeter.utils.PhoneState;
import com.example.leonardo.watermeter.utils.SharedPreUtils;
import com.example.leonardo.watermeter.utils.UploadTaskNum;
import com.extended.BcSettingActivity;
import com.extended.CancelUploadMessageActiviy;
import com.extended.CustomYearDialog;
import com.extended.DownLoadMessageActiviy;
import com.extended.OtherSettingActivity;
import com.objecteye.author.AuthorApplication;
import com.prosdk.BindingDeviceActivity;
import com.tencent.bugly.Bugly;


import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class MonthListViewActivity extends Activity implements UploadTaskInterface {
    Context mContext;
    Handler myHandler;
    private static int chooseMonth = -1;
    public DownloadData downloadData = null;
    private ListView monthListView;
    private TextView tvMonth, showIMEI, IsUploadText;// content_month_list.xml当中的文本
    private BaseAdapter baseAdapterList;
    private String imei;
    private List<DetailData> dataListForUpload = new ArrayList<>();
    private List<FireHydrantDetailData> FireHydrantDataListForUpload = new ArrayList<>();
    private int numberForUpload, numberCurrentIndex, numberForDownload, numberCurrentIndexDownload;
    final String[] months = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
    final String[] monthsInt = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    final String[] years = {"2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027"};
    final String[] deviceArray = {"外接盒子-1", "外接盒子-2", "外接盒子-3"};
    TreeSet<String> uploadmonthSet = new TreeSet<>();
    String yearString = "";
    private ProgressDialog downloadDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ListView menulistview;
    private DrawerLayout main_drawerlayout;
    private TextView dataType;// 数据分为普通数据和消防栓数据 设备支持外接设备—1 外接设备2
    private TextView showYearTV;
    private String updataYearString = null;//保存将要更新的年份
    private TextView showVersion;
    private ImageView menuIV;
    private static String tag = "waterMeter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //自动更新buggly
        //Bugly.init(getApplicationContext(), "ae502d00f2", false);
        setContentView(R.layout.act_month_drawerlayout);
        initShapre();
        initView();
        initDrawer();
        initMenuListView();
        //启动授权服务
        AuthorApplication.startService(this);
    }

    /**
     * 初始化drawerlayout
     */
    private void initDrawer() {
        main_drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (main_drawerlayout.isDrawerOpen(Gravity.LEFT)) {
                    main_drawerlayout.closeDrawer(Gravity.LEFT);
                } else {
                    main_drawerlayout.openDrawer(Gravity.LEFT);
                }
            }
        });
    }

    /**
     * 加载左侧菜单栏
     */
    private void initMenuListView() {
        final String[] menulistdata = getResources().getStringArray(R.array.menulist);
        final ArrayAdapter<String> menulistviewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, menulistdata);
        menulistview.setAdapter(menulistviewAdapter);
        menulistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:// 版本号
                        Intent versionintent = new Intent(MonthListViewActivity.this, VersionActivity.class);
                        startActivity(versionintent);
                        main_drawerlayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 1://IP号
                        Intent IPintent = new Intent(MonthListViewActivity.this, IPActivity.class);
                        startActivity(IPintent);
                        main_drawerlayout.closeDrawer(Gravity.LEFT);
                        break;
//                    case 2://普通水司和消防栓之间的切换
//                        boolean isFireHydrant = SharedPreUtils.getDataType(MonthListViewActivity.this);
//                        if (isFireHydrant) {
//                            dataType.setText("普通数据");
//                        } else {
//                            dataType.setText("消防栓数据");
//                        }
//                        SharedPreUtils.setDataType(MonthListViewActivity.this, !isFireHydrant);
//                        main_drawerlayout.closeDrawer(Gravity.LEFT);
//                        baseAdapterList.notifyDataSetChanged();
//                        break;
                    case 3://上传列表
                        Intent uploadIntent = new Intent(MonthListViewActivity.this, CancelUploadMessageActiviy.class);
                        startActivity(uploadIntent);
                        main_drawerlayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 4://导出列表
                        Intent downloadIntent = new Intent(MonthListViewActivity.this, DownLoadMessageActiviy.class);
                        startActivity(downloadIntent);
                        main_drawerlayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 5://切换年份
                        new CustomYearDialog(MonthListViewActivity.this).showDialog();
                        break;
                    case 6://外接设备选择
                        int selectDeviceType = SharedPreUtils.getDeviceType(MonthListViewActivity.this);
                        DialogUtils.singleChoices(MonthListViewActivity.this, deviceArray, selectDeviceType);
                        main_drawerlayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 7://表册设置
                        Intent bcSettingIntent = new Intent(MonthListViewActivity.this, BcSettingActivity.class);
                        startActivity(bcSettingIntent);
                        main_drawerlayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 8: //设备绑定
                        Intent bindingDeviceIntent = new Intent(MonthListViewActivity.this, BindingDeviceActivity.class);
                        startActivity(bindingDeviceIntent);
                        main_drawerlayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 9://其他设置
                        Intent otherSettingIntent = new Intent(MonthListViewActivity.this, OtherSettingActivity.class);
                        startActivity(otherSettingIntent);
                        main_drawerlayout.closeDrawer(Gravity.LEFT);
                        break;

                    default:
                        Toast.makeText(MonthListViewActivity.this, "该功能暂未开放", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * 显示年份
     */
    private void initShapre() {
        sharedPreferences = getSharedPreferences("Yearinfo", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        yearString = sharedPreferences.getString("yearString", Calendar.getInstance().get(Calendar.YEAR) + "");
    }

    /**
     * 加载adapter
     */
    public void loadAdapter() {
        baseAdapterList = new BaseAdapter() {
            @Override
            public int getCount() {
                return Arrays.asList(months).size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = MonthListViewActivity.this.getLayoutInflater();
                View view;
                if (convertView == null) {
                    view = inflater.inflate(R.layout.content_month_list_main, null);
                } else {
                    view = convertView;
                }
                tvMonth = (TextView) view.findViewById(R.id.contentListRight);
                IsUploadText = (TextView) view.findViewById(R.id.IsUploadText);
                tvMonth.setText(Arrays.asList(months).get(position));
                //判断是正常水司还是消防栓水司
                if (SharedPreUtils.getDataType(MonthListViewActivity.this)) {
                    if (!LitePal.where("cbyf = ?", yearString + monthsInt[position]).find(FireHydrantBchData.class).isEmpty()) {
                        IsUploadText.setText("已下载");
                        IsUploadText.setTextColor(Color.parseColor("#0000ff"));
                        uploadmonthSet.add(monthsInt[position]);
                    } else {
                        IsUploadText.setText("");
                    }
                } else {
                    if (!LitePal.where("cbyf = ?", yearString + monthsInt[position]).find(BchData.class).isEmpty()) {
                        IsUploadText.setText("已下载");
                        IsUploadText.setTextColor(Color.parseColor("#0000ff"));
                        uploadmonthSet.add(monthsInt[position]);
                    } else {
                        IsUploadText.setText("");
                    }
                }
                return view;
            }
        };
        monthListView.setAdapter(baseAdapterList);
        monthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvMonth = (TextView) view.findViewById(R.id.contentListRight);
                boolean dataIsEmpty = true;
                if (SharedPreUtils.getDataType(MonthListViewActivity.this)) {
                    if (!LitePal.where("cbyf = ?", yearString + monthsInt[position]).find(FireHydrantBchData.class).isEmpty()) {
                        dataIsEmpty = false;
                    }
                } else {
                    if (!LitePal.where("cbyf = ?", yearString + monthsInt[position]).find(BchData.class).isEmpty()) {
                        dataIsEmpty = false;
                    }
                }
                System.out.println("下载的表册数据是否为空：" + dataIsEmpty);
                if (dataIsEmpty) {
                    Toast.makeText(MonthListViewActivity.this, "请先下载相应表册号", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MonthListViewActivity.this, BCListViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("monthIndex", position);
                    bundle.putString("yearString", yearString);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

    }

    @SuppressLint("HandlerLeak")
    private void initView() {
        mContext = getApplicationContext();
        showVersion = findViewById(R.id.showVersion);
        try {
            showVersion.setText("WaterMeter " + getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        dataType = (TextView) findViewById(R.id.DataType);
        if (SharedPreUtils.getDataType(this)) {
            dataType.setText("消防栓数据");
        } else {
            dataType.setText("普通数据");
        }
        showIMEI = (TextView) findViewById(R.id.showIMEI);
        downloadData = new DownloadData();
        imei = PhoneState.getDeviceId(this);
        showYearTV = findViewById(R.id.showYear);
        showYearTV.setText(yearString);
        showIMEI.setText(PhoneState.getDeviceId(getApplicationContext()).toString());
        downloadDialog = new ProgressDialog(this);
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        downloadDialog.setTitle("下载表册号及数据");
        downloadDialog.setMessage("下载进行中，请不要进行其他操作！");
        downloadDialog.setCancelable(false);
        menulistview = (ListView) findViewById(R.id.item_menulistview);
        main_drawerlayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        menuIV = findViewById(R.id.menu_dr);
        /*以下是List View部分*/
        monthListView = (ListView) findViewById(R.id.monthListView);
        loadAdapter();
    }


    /**
     * 上传文本-多条上传
     *
     * @param view
     */
    public void uploadEvent(View view) {
        NetWorkUtils.isNetWorkAvailableOfDNS(GlobalVariables.NETIP, new Comparable<Boolean>() {
            @Override
            public int compareTo(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    //TODO 设备允许访问网络
                    uploadDialog();
                } else {
                    // TODO 设备无法访问Internet
                    Toast.makeText(MonthListViewActivity.this, "当前网络不可用，请连接有效网络", Toast.LENGTH_SHORT).show();
                }
                return 0;
            }
        });
    }

    /*
     *上传Dialog
     */
    public void uploadDialog() {
        List<String> uploadmonthList = new ArrayList<>();
        for (Iterator iterator = uploadmonthSet.iterator(); iterator.hasNext(); ) {
            uploadmonthList.add(Month.GetMonthStr((String) iterator.next()));
        }
        final String[] uploadmonths = new String[uploadmonthList.size()];
        for (int i = 0; i < uploadmonthList.size(); i++) {
            uploadmonths[i] = uploadmonthList.get(i);
        }
        final ArrayList<String> choosemonthlist = new ArrayList();
        final AlertDialog.Builder builder = new AlertDialog.Builder(MonthListViewActivity.this);
        builder.setTitle("请选择要上传的月份");
        builder.setMultiChoiceItems(uploadmonths, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (choosemonthlist.contains(Month.Monthtransform(MonthListViewActivity.this, uploadmonths[which]))) {
                    if (!isChecked) {
                        choosemonthlist.remove(Month.Monthtransform(MonthListViewActivity.this, uploadmonths[which]));
                    }
                } else {
                    choosemonthlist.add(Month.Monthtransform(MonthListViewActivity.this, uploadmonths[which]));
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final boolean isFireHydrant = SharedPreUtils.getDataType(MonthListViewActivity.this);
                if (choosemonthlist.size() != 0) {
                    if (isFireHydrant) {//消防栓
                        for (int i = 0; i < choosemonthlist.size(); i++) {
                            if (LitePal.where("cbyf = ?", choosemonthlist.get(i)).find(FireHydrantBchData.class).isEmpty()) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), choosemonthlist.get(i) + "份表冊未下载，请下载表册号再上传数据", Toast.LENGTH_SHORT).show();
                                choosemonthlist.clear();
                                numberForUpload = 0;
                                FireHydrantDataListForUpload.clear();
                            } else {
                                numberForUpload += LitePal.where("isChecked = '0'and isUpload = '1'and detail_date=?", choosemonthlist.get(i)).count(FireHydrantDetailData.class);
                            }
                        }
                    } else { // 普通水司
                        for (int i = 0; i < choosemonthlist.size(); i++) {
                            if (LitePal.where("cbyf = ?", choosemonthlist.get(i)).find(BchData.class).isEmpty()) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), choosemonthlist.get(i) + "份表冊未下载，请下载表册号再上传数据", Toast.LENGTH_SHORT).show();
                                choosemonthlist.clear();
                                numberForUpload = 0;
                                dataListForUpload.clear();
                            } else {
                                numberForUpload += LitePal.where("isChecked = '0'and isUpload = '1'and t_cbyf=?", choosemonthlist.get(i)).count(DetailData.class);
                            }
                        }

                    }
                    if (choosemonthlist.size() != 0 && numberForUpload > 0) {
                        AlertDialog.Builder upbuilder = new AlertDialog.Builder(MonthListViewActivity.this);
                        upbuilder.setMessage("待上传数据有：" + numberForUpload + "条");
                        upbuilder.setTitle("提示");
                        upbuilder.setCancelable(false);
                        upbuilder.setPositiveButton("上传", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("点击了上传按钮");
                                if (PhoneState.isNetworkAvailable(getApplicationContext())) {
                                    System.out.println("点击了上传按钮，并且有网");
                                    //消防水司的
                                    if (isFireHydrant) {
                                        //NOTICE  找出拍了照片而且没有上传的
                                        for (int i = 0; i < choosemonthlist.size(); i++) {
                                            List<FireHydrantDetailData> datalist = LitePal.where("isChecked = '0' and isUpload = '1'and detail_date=?", choosemonthlist.get(i)).find(FireHydrantDetailData.class);
                                            for (int j = 0; j < datalist.size(); j++) {
                                                FireHydrantDataListForUpload.add(datalist.get(j));
                                            }
                                        }
                                        System.out.println("上传-找到的符合条件的数据有：" + FireHydrantDataListForUpload.size());
                                        /*上传功能*/
                                        numberCurrentIndex = 0;
                                        System.out.println("...............上传功能已经开启");
                                        new UploadTaskNum(FireHydrantDataListForUpload, MonthListViewActivity.this, isFireHydrant).execute();
                                    } else {//普通水司的
                                        for (int i = 0; i < choosemonthlist.size(); i++) {
                                            List<DetailData> datalist = LitePal.where("isChecked = '0' and isUpload = '1'and t_cbyf=?", choosemonthlist.get(i)).find(DetailData.class);
                                            for (int j = 0; j < datalist.size(); j++) {
                                                dataListForUpload.add(datalist.get(j));
                                            }
                                        }
                                        System.out.println("上传-找到的符合条件的数据有：" + dataListForUpload.size());
                                        /*上传功能*/
                                        numberCurrentIndex = 0;
                                        System.out.println("...............上传功能已经开启");
                                        new UploadTaskNum(dataListForUpload, MonthListViewActivity.this, isFireHydrant).execute();
                                    }
                                } else {
                                    //.stopCapture();
                                    numberForUpload = 0;
                                    dataListForUpload.clear();
                                    FireHydrantDataListForUpload.clear();
                                    Toast.makeText(MonthListViewActivity.this, "当前网络不可用，请检查当前网络", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        upbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                numberForUpload = 0;
                                choosemonthlist.clear();
                                dialog.dismiss();
                            }
                        });
                        upbuilder.create().show();
                    } else {
                        Toast.makeText(MonthListViewActivity.this, "没有可上传的数据", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choosemonthlist.clear();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    /**
     * 下载对应月份表册号
     *
     * @param view
     */
    public void downloadBCEvent(View view) {
        NetWorkUtils.isNetWorkAvailableOfDNS(GlobalVariables.NETIP, new Comparable<Boolean>() {
            @Override
            public int compareTo(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    //TODO 设备允许访问网络
                    showBCYearDialog();
                } else {
                    // TODO 设备无法访问Internet
                    Toast.makeText(MonthListViewActivity.this, "当前网络不可用，请连接有效网络", Toast.LENGTH_SHORT).show();
                }
                return 0;
            }
        });
    }

    /**
     * 显示选择下载表册号的弹出框
     */
    private void showBCDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MonthListViewActivity.this, android.R.style.Theme_Holo_Light_Dialog);
        builder.setTitle("选择对应月份");
        builder.setItems(months, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseMonth = which;
                downloadData.GetBCdata(imei, updataYearString + monthsInt[which], MonthListViewActivity.this);//开启下载表册号的线程
            }
        });
        builder.show();
    }

    /**
     * 显示选择下载表册号年份的弹出框
     */
    private void showBCYearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MonthListViewActivity.this, android.R.style.Theme_Holo_Light_Dialog);
        builder.setTitle("选择对应年份");
        builder.setItems(years, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updataYearString = years[which];
                showBCDialog();
            }
        });
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MonthListViewActivity.this);
            builder.setMessage("确定要退出程序吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MonthListViewActivity.this.finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void fishActivity() {
        Log.i(tag, " MonthListViewActivit finish");
        MonthListViewActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AuthorApplication.stopService();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * 上传回调
     */
    @Override
    public void TaskResult() {
        numberForUpload = 0;
        dataListForUpload.clear();
        FireHydrantDataListForUpload.clear();
    }

    /*
     *更新显示的数据（下载列表和年份）
     */
    public void updateData() {
        uploadmonthSet.clear();
        initShapre();
        showYearTV.setText(yearString);
        baseAdapterList.notifyDataSetChanged();
        if (main_drawerlayout.isDrawerOpen(Gravity.LEFT)) {
            main_drawerlayout.closeDrawer(Gravity.LEFT);
        }
    }

}

