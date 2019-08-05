package com.example.leonardo.watermeter.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.FireHydrant.entity.FireHydrantBchData;
import com.FireHydrant.entity.FireHydrantDetailData;
import com.FireHydrant.ui.FireHydrantTaskListActivity;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.BchData;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.global.GlobalData;
import com.example.leonardo.watermeter.utils.SharedPreUtils;
import com.itgoyo.logtofilelibrary.LogToFileUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class BCListViewActivity extends Activity {
    private BaseAdapter baseAdapter;
    private ListView bchListView;
    private TextView monthTextView, bchTextView, monthAll, monthNot, monthDone;
    private int monthAllNumber, monthNotNumber, monthDoneNumber;
    private int monthIndex = -1;
    final String[] months = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
    final String[] monthsInt = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    String yearString = "";
    String cbyfString = "";
    private List<BchData> bchDataList;
    private List<FireHydrantBchData> fireHydrantBchDataList;
    private ArrayList<String> treelist = new ArrayList<>();
    private Switch detect_Switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_bclist_view);
        /*接收月份列表页面传来的月份信息*/
        Bundle bundle = this.getIntent().getExtras();
        monthIndex = bundle.getInt("monthIndex");
        yearString = bundle.getString("yearString");
        cbyfString = yearString + monthsInt[monthIndex];
        initView();
        GlobalData.setAutoJump = false;
    }

    private void initView() {
        monthAll = (TextView) findViewById(R.id.monthAll);
        monthNot = (TextView) findViewById(R.id.monthNot);
        monthDone = (TextView) findViewById(R.id.monthDone);
        updateTip();
        if (SharedPreUtils.getDataType(this)) {
            fireHydrantBchDataList = DataSupport.where("cbyf = ?", cbyfString).find(FireHydrantBchData.class);
        } else {
            bchDataList = DataSupport.where("cbyf = ?", cbyfString).find(BchData.class);
        }
        showtreeset();
        /*以下是List View部分*/
        bchListView = (ListView) findViewById(R.id.bchListView);
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return treelist.size();
            }

            @Override
            public Object getItem(int position) {
                return treelist.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, final View convertView, ViewGroup parent) {
                LayoutInflater inflater = BCListViewActivity.this.getLayoutInflater();
                final View view;
                if (convertView == null) {
                    view = inflater.inflate(R.layout.content_month_list, null);
                } else {
                    view = convertView;
                }
                detect_Switch = view.findViewById(R.id.detect_switch);
                if (!SharedPreUtils.getDataType(BCListViewActivity.this)) {
                    detect_Switch.setShowText(true);
                    /**
                     * 判断表册是否允许手机端识别 默认数据库是自动识别的
                     * 根据数据库状态设置switch的状态
                     */
                    BchData bchData = DataSupport.where("cbyf= ? and bch= ?", cbyfString, treelist.get(position)).findFirst(BchData.class);
                    if (bchData.getIsDetectByPhone().equals("0")) {
                        detect_Switch.setChecked(true);
                    } else {
                        detect_Switch.setChecked(false);
                    }
                    detect_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            String isDetectFlag;
                            Log.e("detectFlag", "按钮是否被点击：" + b);
                            if (b) {
                                isDetectFlag = "0";
                                detect_Switch.setTextOn("开");
                            } else {
                                isDetectFlag = "1";
                                detect_Switch.setTextOff("关");
                            }
                            ContentValues values = new ContentValues();
                            values.put("isDetectByPhone", isDetectFlag);
                            DataSupport.updateAll(BchData.class, values, "cbyf= ? and bch= ?", cbyfString, treelist.get(position));
                        }
                    });
                } else {
                    detect_Switch.setVisibility(View.INVISIBLE);
                }
                monthTextView = (TextView) view.findViewById(R.id.contentListLeft);
                bchTextView = (TextView) view.findViewById(R.id.contentListRight);
                monthTextView.setText(months[monthIndex]);
                bchTextView.setText(treelist.get(position));
                return view;
            }
        };
        bchListView.setAdapter(baseAdapter);
        bchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //判断是消防栓还是普通水司
                try {
                    List detailDataList;
                    Intent intent;
                    if (SharedPreUtils.getDataType(BCListViewActivity.this)) {
                        detailDataList = DataSupport.where("detail_date = ? and booklet_no= ?", cbyfString, bchTextView.getText().toString()).find(FireHydrantDetailData.class);
                        intent = new Intent(BCListViewActivity.this, FireHydrantTaskListActivity.class);
                    } else {
                        detailDataList = DataSupport.where("t_cbyf = ? and t_volume_num = ?", cbyfString, bchTextView.getText().toString()).find(DetailData.class);
                        intent = new Intent(BCListViewActivity.this, TaskListActivity.class);
                    }
                    if (!detailDataList.isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("cbyf", cbyfString);
                        bundle.putString("bch", treelist.get(i));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(BCListViewActivity.this, "没有数据，请返回上一级重新下载", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    LogToFileUtils.write(e.toString());
                }
            }
        });
    }

    /*
    将表册号按自然顺序排序
     */
    private void showtreeset() {
        TreeSet<String> treeSet = new TreeSet<>();
        if (SharedPreUtils.getDataType(this)) {
            for (int i = 0; i < fireHydrantBchDataList.size(); i++) {
                if (treeSet.contains(fireHydrantBchDataList.get(i).getBch())) {
                    treelist.add(fireHydrantBchDataList.get(i).getBch());
                } else {
                    treeSet.add(fireHydrantBchDataList.get(i).getBch());
                }
            }
            Iterator it = treeSet.iterator();
            while (it.hasNext()) {
                treelist.add(it.next().toString());
            }
        } else {
            for (int i = 0; i < bchDataList.size(); i++) {
                if (treeSet.contains(bchDataList.get(i).getBch())) {
                    treelist.add(bchDataList.get(i).getBch());
                } else {
                    treeSet.add(bchDataList.get(i).getBch());
                }
            }
            Iterator it = treeSet.iterator();
            while (it.hasNext()) {
                treelist.add(it.next().toString());
            }
        }
    }

    private void updateTip() {
        //判断是普通水司还是消防栓水司
        if (SharedPreUtils.getDataType(this)) {
            monthAllNumber = DataSupport.where("detail_date = ?", cbyfString).count(FireHydrantDetailData.class);
            monthDoneNumber = DataSupport.where("detail_date = ? and isChecked = ?", cbyfString, "0").count(FireHydrantDetailData.class);
            monthNotNumber = DataSupport.where("detail_date = ? and isChecked = ?", cbyfString, "1").count(FireHydrantDetailData.class);
        } else {
            monthAllNumber = DataSupport.where("t_cbyf = ?", cbyfString).count(DetailData.class);
            monthDoneNumber = DataSupport.where("t_cbyf = ? and isChecked = ?", cbyfString, "0").count(DetailData.class);
            monthNotNumber = DataSupport.where("t_cbyf = ? and isChecked = ?", cbyfString, "1").count(DetailData.class);
        }
        monthAll.setText("全部数据:" + monthAllNumber);
        monthNot.setText("未抄数据:" + monthNotNumber);
        monthDone.setText("已抄数据:" + monthDoneNumber);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GlobalData.setAutoJump = false;
        updateTip();
    }

}
