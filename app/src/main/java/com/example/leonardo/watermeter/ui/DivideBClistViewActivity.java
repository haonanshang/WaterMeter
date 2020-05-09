package com.example.leonardo.watermeter.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.adapter.DivideDetailAdapter;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.entity.DetailDivideData;
import com.example.leonardo.watermeter.utils.LoadSearchDataRunnable;

import org.apache.commons.lang3.StringUtils;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DivideBClistViewActivity extends Activity {
    private TextView titleView, taskCountTv;
    private ListView detailDivideListView;
    private String cbyf, bch;
    private List<DetailDivideData> mDatas = new ArrayList<>();
    private String TAG = "waterMeter";
    private EditText searchEt;
    private ImageView clearSearchIv;
    private Handler myHandler = new Handler();
    private ConcurrentLinkedDeque<List<DetailData>> detailDataListAll = new ConcurrentLinkedDeque<>();
    private DivideDetailAdapter divideDetailAdapter;
    private boolean isLoadSearchData = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.divide_bclist_view);
        /*接收月份列表页面传来的月份信息*/
        Bundle bundle = this.getIntent().getExtras();
        cbyf = bundle.getString("cbyf");
        bch = bundle.getString("bch");
        initViews();
        initDatas();
        updateAdapterData();
        initAdapter();
        setFocusListener();
        setSearchListener();//设置搜索框改变的监听器
    }

    /**
     * 焦点监听
     */
    private void setFocusListener() {
        searchEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {//TODO 获取到焦点
                    if (isLoadSearchData) {
                        isLoadSearchData = false;
                        loadSearchData();
                    }
                }
            }
        });
    }


    private void loadSearchData() {
        ExecutorService loadSearchDataThreadPool = Executors.newSingleThreadExecutor();
        for (int i = 0; i < mDatas.size(); i++) {
            loadSearchDataThreadPool.execute(new LoadSearchDataRunnable(cbyf, bch, detailDataListAll, mDatas.get(i)));
        }
    }


    private void updateAdapterData() {
        List<DetailDivideData> tempDatas = LitePal.where("cbyf = ? and bch = ?", cbyf, bch).find(DetailDivideData.class);
        mDatas.clear();
        for (int i = 0; i < tempDatas.size(); i++) {
            mDatas.add(tempDatas.get(i));
        }
    }

    private void setSearchListener() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    clearSearchIv.setVisibility(View.GONE);
                } else {
                    clearSearchIv.setVisibility(View.VISIBLE);
                }
                //触发更新线程
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String searchText = searchEt.getText().toString();
                        updateAdapterBySearchText(searchText);
                        divideDetailAdapter.notifyDataSetChanged();//更新
                    }
                });
            }
        });
    }


    private void initAdapter() {
        divideDetailAdapter = new DivideDetailAdapter(this, mDatas);
        detailDivideListView.setAdapter(divideDetailAdapter);
        detailDivideListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DivideBClistViewActivity.this, TaskListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("cbyf", cbyf);
                bundle.putString("bch", bch);
                bundle.putString("divideNumber", mDatas.get(i).getDivideNumber());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initDatas() {
        int taskCount = LitePal.where("t_cbyf = ? and t_volume_num = ? ", cbyf, bch).count(DetailData.class);
        taskCountTv.setText("任务数量为：" + taskCount);
        titleView.setText("表册号 " + bch);
    }

    private void initViews() {
        titleView = findViewById(R.id.bch);
        taskCountTv = findViewById(R.id.divide_taskCount);
        detailDivideListView = findViewById(R.id.divideListView);
        searchEt = findViewById(R.id.divide_search);
        searchEt.clearFocus();
        clearSearchIv = findViewById(R.id.divide_clearSearch);
        clearSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText("");
            }
        });

    }

    /**
     * 根据文本框内容进行子列表的更新
     *
     * @param searchText 用于搜索的文本框
     */
    private void updateAdapterBySearchText(String searchText) {
        if (!StringUtils.isEmpty(searchText)) {
            if (!detailDataListAll.isEmpty()) {
                mDatas.clear();
                Iterator<List<DetailData>> listIterator = detailDataListAll.iterator();
                for (Iterator<List<DetailData>> it = listIterator; it.hasNext(); ) {
                    List<DetailData> tempDetailDataList = it.next();
                    for (int j = 0; j < tempDetailDataList.size(); j++) {
                        DetailData detailDataTemp = tempDetailDataList.get(j);
                        if (detailDataTemp.getT_card_num().contains(searchText) || detailDataTemp.getT_ticket_name().contains(searchText) || detailDataTemp.getT_meter_num().contains(searchText)) {
                            DetailDivideData detailDivideData = LitePal.where("cbyf = ? and bch = ? and divideNumber = ? ", cbyf, bch, detailDataTemp.getDivideNumber()).findFirst(DetailDivideData.class);
                            if (checkDivideNumberExist(detailDivideData.getDivideNumber())) {
                                break;
                            } else {
                                mDatas.add(detailDivideData);
                            }
                        }
                    }

                }
            }
        } else {
            updateAdapterData();
        }

    }

    /**
     * @param divideNumber
     * @return
     */
    private boolean checkDivideNumberExist(String divideNumber) {
        boolean result = false;
        for (int i = 0; i < mDatas.size(); i++) {
            if (divideNumber.equals(mDatas.get(i).getDivideNumber())) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        divideDetailAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatas.clear();
        detailDataListAll.clear();
    }
}

