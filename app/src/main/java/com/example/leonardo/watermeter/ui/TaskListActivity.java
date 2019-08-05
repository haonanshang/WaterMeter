package com.example.leonardo.watermeter.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.DetailData;
import com.itgoyo.logtofilelibrary.LogToFileUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends Activity {
    private String cbyf, bch;
    private String whichPage = "001";
    private ListView taskListView;
    private BaseAdapter baseAdapter;
    private EditText searchE;
    private ImageView clearSearch;
    private List<DetailData> detailDataListAll, detailDataListNotYet, detailDataListDone, detailDataListOriginal;
    private List<DetailData> subDetailDataList;
    private TextView taskIDTextView, cardNumberTextView, ticketNameTextView, locationTextView, isCheckedTextView, taskCountTextView, meterNumberTextView;
    Handler myHandler = new Handler();
    private int pos;//记录返回时item的位置
    private BottomNavigationView navigation;
    private static int RequestCode = 1;
    private boolean isonpause = false;
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
                    //myHandler.post(changeList);//触发更新线程
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
                    System.out.println("数据库-尚未抄表数量：" + detailDataListNotYet.size());
                    //myHandler.post(changeList);//触发更新线程
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
                    System.out.println("数据库-已经抄表数量：" + detailDataListDone.size());
                    //myHandler.post(changeList);//触发更新线程
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

        detailDataListOriginal = DataSupport.select("t_card_num", "t_meter_num", "t_ticket_name", "t_location", "isChecked", "t_cbyf", "t_volume_num", "t_id", "isUpload ", "t_volume_order", "t_meter_num").where("t_cbyf = ? and t_volume_num = ?", cbyf, bch).find(DetailData.class);
        detailDataListAll = DataSupport.select("t_card_num", "t_meter_num", "t_ticket_name", "t_location", "isChecked", "t_cbyf", "t_volume_num", "t_id", "isUpload ", "t_volume_order", "t_meter_num").where("t_cbyf = ? and t_volume_num = ?", cbyf, bch).find(DetailData.class);//这个列表用来作为存储用于当前显示的列表，并且当前所有的操作也是要基于这个列表的
        detailDataListNotYet = DataSupport.select("t_card_num", "t_meter_num", "t_ticket_name", "t_location", "isChecked", "t_cbyf", "t_volume_num", "t_id", "isUpload ", "t_volume_order", "t_meter_num").where("t_cbyf = ? and t_volume_num = ? and isChecked = ?", cbyf, bch, "1").find(DetailData.class);
        detailDataListDone = DataSupport.select("t_card_num", "t_meter_num", "t_ticket_name", "t_location", "isChecked", "t_cbyf", "t_volume_num", "t_id", "isUpload ", "t_volume_order", "t_meter_num").where("t_cbyf = ? and t_volume_num = ? and isChecked = ?", cbyf, bch, "0").find(DetailData.class);

        updateData(whichPage);
        updateTipInfo();
        setSearchLisener();//设置搜索框改变的监听器
        setClearEditEvent();//清空输入框
        setListViewAdapter();//给list View设置adapter
    }


    private void updateData(String str) {
        if (whichPage.equals("001")) {
            subDetailDataList = DataSupport.select("t_card_num", "t_meter_num", "t_ticket_name", "t_location", "isChecked", "t_cbyf", "t_volume_num", "t_id", "isUpload ", "t_volume_order").where("t_cbyf = ? and t_volume_num = ?", cbyf, bch).find(DetailData.class);//NOTICE 查询后的子数据
        } else if (whichPage.equals("002")) {
            subDetailDataList = DataSupport.select("t_card_num", "t_meter_num", "t_ticket_name", "t_location", "isChecked", "t_cbyf", "t_volume_num", "t_id", "isUpload ", "t_volume_order").where("t_cbyf = ? and t_volume_num = ? and isChecked = ? ", cbyf, bch, "1").find(DetailData.class);
        } else if (whichPage.equals("003")) {
            subDetailDataList = DataSupport.select("t_card_num", "t_meter_num", "t_ticket_name", "t_location", "isChecked", "t_cbyf", "t_volume_num", "t_id", "isUpload ", "t_volume_order").where("t_cbyf = ? and t_volume_num = ? and isChecked = ?", cbyf, bch, "0").find(DetailData.class);
        }
    }

    public void updateTipInfo() {
        taskCountTextView.setText("任务数量为：" + subDetailDataList.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isonpause) {
            isonpause = false;
            initView();
            if (!whichPage.equals("002")) {
                System.out.println("--------------------当前所处的位置为：" + pos);
                if (subDetailDataList.size() > pos) {
                    if (pos <= 4) {
                        taskListView.setSelection(pos);//记录当前item的位置
                    } else {
                        taskListView.setSelection(pos - 3);//记录当前item的位置
                    }
                }
            }
            System.out.println("重要！回退测试-执行了");
        }
    }

    /**
     * 设置List View 的Adapter
     */
    private void setListViewAdapter() {
        /*以下是List View部分*/
        try {
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
                    LayoutInflater inflater = TaskListActivity.this.getLayoutInflater();
                    View view;
                    DetailData detailDataTemp = subDetailDataList.get(position);
                    if (convertView == null) {
                        view = inflater.inflate(R.layout.content_task_list, null);
                    } else {
                        view = convertView;
                    }
                    taskIDTextView = (TextView) view.findViewById(R.id.taskID);
                    cardNumberTextView = (TextView) view.findViewById(R.id.cardNumberTextView);
                    meterNumberTextView = (TextView) view.findViewById(R.id.meterNumberTextView);
                    ticketNameTextView = (TextView) view.findViewById(R.id.ticketNameTextView);
                    locationTextView = (TextView) view.findViewById(R.id.locationTextView);
                    isCheckedTextView = (TextView) view.findViewById(R.id.isChecked);
                    taskIDTextView.setText(detailDataTemp.getT_volume_order());
                    cardNumberTextView.setText(detailDataTemp.getT_card_num());
                    meterNumberTextView.setText(detailDataTemp.getT_meter_num());
                    ticketNameTextView.setText(detailDataTemp.getT_ticket_name());
                    locationTextView.setText(detailDataTemp.getT_location());
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
                    final DetailData tempData = subDetailDataList.get(position);
                    if (tempData.getIsUpload().equals("0")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(TaskListActivity.this);
                        dialog.setTitle("提示");
                        dialog.setMessage("该客户本月数据已经上传，可进行以下操作");
                        dialog.setNegativeButton("修改数据", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ContentValues values = new ContentValues();
                                values.put("isUpload", "1");
                                DataSupport.updateAll(DetailData.class, values, "t_id = ?", tempData.getT_id());
                                Intent intent = new Intent(TaskListActivity.this, TaskShowActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("cbyf", tempData.getT_cbyf());
                                bundle.putString("bch", tempData.getT_volume_num());
                                bundle.putString("tid", tempData.getT_id());
                                bundle.putString("whichPage", whichPage);
                                // bundle.putInt("taskPosition", Integer.valueOf(tempData.getT_volume_order()));
                                intent.putExtras(bundle);
                                System.out.println("检测-数据库-本条数据的id为：" + subDetailDataList.get(position).getT_id());
                                startActivityForResult(intent, RequestCode);
                            }
                        });
                        dialog.setPositiveButton("浏览数据", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ContentValues values = new ContentValues();
                                DataSupport.updateAll(DetailData.class, values, "t_id = ?", tempData.getT_id());
                                Intent intent = new Intent(TaskListActivity.this, TaskShowActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("cbyf", tempData.getT_cbyf());
                                bundle.putString("bch", tempData.getT_volume_num());
                                bundle.putString("tid", tempData.getT_id());
                                bundle.putString("whichPage", whichPage);
                                //bundle.putInt("taskPosition", Integer.valueOf(tempData.getT_volume_order()));
                                intent.putExtras(bundle);
                                System.out.println("检测-数据库-本条数据的id为：" + subDetailDataList.get(position).getT_id());
                                startActivityForResult(intent, RequestCode);
                            }
                        });
                        dialog.show();

                    } else {
                        Intent intent = new Intent(TaskListActivity.this, TaskShowActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("cbyf", tempData.getT_cbyf());
                        bundle.putString("bch", tempData.getT_volume_num());
                        bundle.putString("tid", tempData.getT_id());
                        bundle.putString("whichPage", whichPage);
                        // bundle.putInt("taskPosition", Integer.valueOf(tempData.getT_volume_order()));
                        intent.putExtras(bundle);
                        System.out.println("检测-数据库-本条数据的id为：" + subDetailDataList.get(position).getT_id());
                        startActivityForResult(intent, RequestCode);
                    }
                }
            });
        } catch (Exception e) {
            LogToFileUtils.write(e.toString());
        }
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
            List<DetailData> currentdata = new ArrayList<>();
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
                DetailData detailDataTemp = currentdata.get(i);
                if (detailDataTemp.getT_card_num().contains(searchText) || detailDataTemp.getT_ticket_name().contains(searchText) || detailDataTemp.getT_meter_num().contains(searchText)) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("-----任务列表当前已经执行----");
        if (requestCode == RequestCode && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            pos = bundle.getInt("postion");
            isonpause = true;

        }

    }
}
