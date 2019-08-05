package com.FireHydrant.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.FireHydrant.entity.FireHydrantDetailData;
import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.utils.SharedPreUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class FireHydrantTaskListActivity extends Activity {
    private String cbyf, bch;
    private String whichPage = "001";
    private ListView taskListView;
    private BaseAdapter baseAdapter;
    private EditText searchE;
    private ImageView clearSearch;
    private List<FireHydrantDetailData> detailDataListAll, detailDataListNotYet, detailDataListDone, detailDataListOriginal;
    private List<FireHydrantDetailData> subDetailDataList;
    private TextView taskIDTextView, cardNumberTextView, ticketNameTextView, locationTextView, isCheckedTextView, taskCountTextView;
    Handler myHandler = new Handler();
    private BottomNavigationView navigation;
    private int currentPostion;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    subDetailDataList.clear();
                    for (int i = 0; i < detailDataListOriginal.size(); i++) {
                        subDetailDataList.add(detailDataListOriginal.get(i));
                    }
                    whichPage = "001";
                    updateData(whichPage);
                    updateTipInfo();
                    setListViewAdapter();
                    return true;
                case R.id.navigation_dashboard:
                    subDetailDataList.clear();
                    for (int i = 0; i < detailDataListNotYet.size(); i++) {
                        subDetailDataList.add(detailDataListNotYet.get(i));
                    }
                    whichPage = "002";
                    updateData(whichPage);
                    updateTipInfo();
                    setListViewAdapter();
                    return true;
                case R.id.navigation_notifications:
                    subDetailDataList.clear();
                    for (int i = 0; i < detailDataListDone.size(); i++) {
                        subDetailDataList.add(detailDataListDone.get(i));
                    }
                    whichPage = "003";
                    updateData(whichPage);
                    updateTipInfo();
                    setListViewAdapter();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_tasklist_view);
        /*接收月份列表页面传来的月份信息*/
        Bundle bundle = this.getIntent().getExtras();
        cbyf = bundle.getString("cbyf");
        bch = bundle.getString("bch");
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initView();
    }


    public void initView() {
        searchE = (EditText) findViewById(R.id.searchE_bch);
        clearSearch = (ImageView) findViewById(R.id.clearSearch_bch);
        taskListView = (ListView) findViewById(R.id.taskListView_bch);
        taskCountTextView = (TextView) findViewById(R.id.taskCount);

        //NOTICE 下面这个list数据要一直保存，作为元数据
        detailDataListOriginal = DataSupport.select("fire_hydrant_id", "fire_hydrant_name", "booklet_no", "org_code", "detail_date", "address", "longitude", "latitude", "state", "type", "isChecked", "isUpload").where("detail_date = ? and booklet_no = ?", cbyf, bch).find(FireHydrantDetailData.class);
        detailDataListAll = DataSupport.select("fire_hydrant_id", "fire_hydrant_name", "booklet_no", "org_code", "detail_date", "address", "longitude", "latitude", "state", "type", "isChecked", "isUpload").where("detail_date = ? and booklet_no = ?", cbyf, bch).find(FireHydrantDetailData.class);//这个列表用来作为存储用于当前显示的列表，并且当前所有的操作也是要基于这个列表的
        detailDataListNotYet = DataSupport.select("fire_hydrant_id", "fire_hydrant_name", "booklet_no", "org_code", "detail_date", "address", "longitude", "latitude", "state", "type", "isChecked", "isChecked", "isUpload").where("detail_date = ? and booklet_no = ? and isChecked = ?", cbyf, bch, "1").find(FireHydrantDetailData.class);
        detailDataListDone = DataSupport.select("fire_hydrant_id", "fire_hydrant_name", "booklet_no", "org_code", "detail_date", "address", "longitude", "latitude", "state", "type", "isChecked", "isChecked", "isUpload").where("detail_date = ? and booklet_no = ? and isChecked = ?", cbyf, bch, "0").find(FireHydrantDetailData.class);

        updateData(whichPage);
        updateTipInfo();
        setSearchLisener();//设置搜索框改变的监听器
        setClearEditEvent();//清空输入框
        setListViewAdapter();//给list View设置adapter
    }


    private void updateData(String str) {
        if (whichPage.equals("001")) {
            subDetailDataList = DataSupport.select("fire_hydrant_id", "fire_hydrant_name", "booklet_no", "org_code", "detail_date", "address", "longitude", "latitude", "state", "type", "isChecked", "isUpload").where("detail_date = ? and booklet_no = ?", cbyf, bch).find(FireHydrantDetailData.class);
        } else if (whichPage.equals("002")) {
            subDetailDataList = DataSupport.select("fire_hydrant_id", "fire_hydrant_name", "booklet_no", "org_code", "detail_date", "address", "longitude", "latitude", "state", "type", "isChecked", "isUpload").where("detail_date = ? and booklet_no = ? and isChecked = ?", cbyf, bch, "1").find(FireHydrantDetailData.class);
        } else if (whichPage.equals("003")) {
            subDetailDataList = DataSupport.select("fire_hydrant_id", "fire_hydrant_name", "booklet_no", "org_code", "detail_date", "address", "longitude", "latitude", "state", "type", "isChecked", "isUpload").where("detail_date = ? and booklet_no = ? and isChecked = ?", cbyf, bch, "0").find(FireHydrantDetailData.class);
        }
    }

    public void updateTipInfo() {
        taskCountTextView.setText("任务数量为：" + subDetailDataList.size());
    }


    /**
     * 设置List View 的Adapter
     */
    private void setListViewAdapter() {
        /*以下是List View部分*/

        baseAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return subDetailDataList.size();
            }

            @Override
            public Object getItem(int position) {
                return subDetailDataList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = FireHydrantTaskListActivity.this.getLayoutInflater();
                View view;
                FireHydrantDetailData detailDataTemp = subDetailDataList.get(position);
                if (convertView == null) {
                    view = inflater.inflate(R.layout.firehydrant_content_task_list, null);
                } else {
                    view = convertView;
                }
                taskIDTextView = (TextView) view.findViewById(R.id.taskID);
                cardNumberTextView = (TextView) view.findViewById(R.id.cardNumberTextView);
                ticketNameTextView = (TextView) view.findViewById(R.id.ticketNameTextView);
                locationTextView = (TextView) view.findViewById(R.id.locationTextView);
                isCheckedTextView = (TextView) view.findViewById(R.id.isChecked);
                taskIDTextView.setText((position + 1) + "");
                cardNumberTextView.setText(detailDataTemp.getFire_hydrant_id());
                ticketNameTextView.setText(detailDataTemp.getFire_hydrant_name());
                locationTextView.setText(detailDataTemp.getAddress());
                if (detailDataTemp.getIsUpload().equals("0") && detailDataTemp.getIsChecked().equals("0")) {
                    isCheckedTextView.setText("已上传");
                    isCheckedTextView.setTextColor(Color.parseColor("#ff0000"));
                } else if (detailDataTemp.getIsChecked().equals("0") && detailDataTemp.getIsUpload().equals("1")) {
                    isCheckedTextView.setText(" 已抄");
                    isCheckedTextView.setTextColor(Color.parseColor("#0000ff"));
                } else if (detailDataTemp.getIsChecked().equals("1") && detailDataTemp.getIsUpload().equals("1")) {
                    isCheckedTextView.setText("未抄");
                    isCheckedTextView.setTextColor(Color.parseColor("#A3A3A3"));
                }
                return view;
            }
        };
        taskListView.setAdapter(baseAdapter);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                currentPostion = position;
                final FireHydrantDetailData tempData = subDetailDataList.get(position);
                Intent intent = new Intent(FireHydrantTaskListActivity.this, FireHydrantDataListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("fire_hydrant_id", tempData.getFire_hydrant_id());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    /**
     * 设置搜索框的监听器
     */
    private void setSearchLisener() {
        searchE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    clearSearch.setVisibility(View.GONE);
                    updateData(whichPage);
                } else {
                    clearSearch.setVisibility(View.VISIBLE);
                }
                System.out.println("搜索-检测到了文本改变");
                myHandler.post(changeList);//触发更新线程
            }
        });
    }

    /*Hanlder触发的更新线程*/
    Runnable changeList = new Runnable() {
        @Override
        public void run() {
            String searchText = searchE.getText().toString();
            //TODO 更新数据
            updateSubList(searchText);
            baseAdapter.notifyDataSetChanged();//更新
            System.out.println("搜索-触发了更新线程");
        }
    };

    /**
     * 根据文本框内容进行子列表的更新
     *
     * @param searchText 用于搜索的文本框
     */
    private void updateSubList(String searchText) {
        if (!searchText.isEmpty() && searchText.length() != 0) {
            subDetailDataList.clear();
            System.out.println("搜索-更新子列表-内容不为空");
            int length = 0;
            List<FireHydrantDetailData> currentdata = new ArrayList<>();
            if (whichPage.equals("001")) {
                length = detailDataListAll.size();
                currentdata = detailDataListAll;
            } else if (whichPage.equals("002")) {
                length = detailDataListNotYet.size();
                currentdata = detailDataListNotYet;
            } else if (whichPage.equals("003")) {
                length = detailDataListDone.size();
                currentdata = detailDataListDone;
            }
            for (int i = 0; i < length; i++) {
                System.out.println("搜索-进行了循环");
                FireHydrantDetailData detailDataTemp = currentdata.get(i);
                if (detailDataTemp.getFire_hydrant_id().contains(searchText) || detailDataTemp.getFire_hydrant_name().contains(searchText)) {
                    subDetailDataList.add(detailDataTemp);
                    System.out.println("搜索-更新子列表-检测到了文本匹配");
                }
            }
        }
    }

    /**
     * 设置清空输入框的监听器
     */
    private void setClearEditEvent() {
        searchE.setText("");
        clearSearch.setVisibility(View.INVISIBLE);
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchE.setText("");
                initView();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 找到数据的位置
        FireHydrantDetailData postionData = subDetailDataList.get(currentPostion);
        if (whichPage.equals("001")) {
            currentPostion = SharedPreUtils.GetFHDataPostion(detailDataListAll, postionData);
        } else if (whichPage.equals("002")) {
            currentPostion = SharedPreUtils.GetFHDataPostion(detailDataListNotYet, postionData);
        } else if (whichPage.equals("003")) {
            currentPostion = SharedPreUtils.GetFHDataPostion(detailDataListDone, postionData);
        }
        initView();
        taskListView.setSelection(currentPostion);
    }


}
