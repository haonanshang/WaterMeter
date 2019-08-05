package com.example.leonardo.watermeter.download;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.ui.MonthListViewActivity;
import com.example.leonardo.watermeter.utils.DownloadData;
import com.example.leonardo.watermeter.utils.PhoneState;

import java.util.ArrayList;
import java.util.List;

public class DownloadDialog extends AlertDialog implements View.OnClickListener {
    private CheckBox mMainCkb;
    private ListView mListView;
    private List<DownloadModel> models;
    private MonthListViewActivity mContext;
    private String[] mBchArray;
    private DownloadAdapter mMyAdapter;
    private String mCbyf;
    //监听来源
    public boolean mIsFromItem = false;
    private Button cancel_bt, confirm_bt;

    public DownloadDialog(MonthListViewActivity context, String cbyf, String[] bchArray) {
        super(context);
        this.mContext = context;
        this.mCbyf = cbyf;
        this.mBchArray = bchArray;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_checkbox);
        initViews();
        initDatas();
        initViewOper();
        initListenners();
    }

    /**
     * s设置监听
     */
    private void initListenners() {
        cancel_bt.setOnClickListener(this);
        confirm_bt.setOnClickListener(this);
    }


    /**
     * 加载adapter
     */
    private void initViewOper() {
        mMyAdapter = new DownloadAdapter(models, this, mContext, mCbyf, new AllCheckListener() {

            @Override
            public void onCheckedChanged(boolean b) {
                //根据不同的情况对maincheckbox做处理
                if (!b && !mMainCkb.isChecked()) {
                    return;
                } else if (!b && mMainCkb.isChecked()) {
                    mIsFromItem = true;
                    mMainCkb.setChecked(false);
                } else if (b) {
                    mIsFromItem = true;
                    mMainCkb.setChecked(true);
                }
            }
        });
        mListView.setAdapter(mMyAdapter);
        //全选的点击监听
        mMainCkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //当监听来源为点击item改变maincbk状态时不在监听改变，防止死循环
                if (mIsFromItem) {
                    mIsFromItem = false;
                    return;
                }
                //改变数据
                for (DownloadModel model : models) {
                    model.setIscheck(b);
                }
                //刷新listview
                mMyAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        mMainCkb = findViewById(R.id.ckb_main);
        mListView = findViewById(R.id.download_list);
        cancel_bt = findViewById(R.id.cancel);
        confirm_bt = findViewById(R.id.bch_download);
    }

    /**
     * 加载数据
     */
    private void initDatas() {
        models = new ArrayList<>();
        DownloadModel model;
        for (int i = 0; i < mBchArray.length; i++) {
            model = new DownloadModel();
            model.setSt(mBchArray[i]);
            model.setIscheck(false);
            models.add(model);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                DownloadDialog.this.dismiss();
                break;
            case R.id.bch_download:
                List<DownloadModel> allModelData = mMyAdapter.getAllModelOnCheck();
                if (allModelData.size() > 0) {
                    String[] bchArray = new String[allModelData.size()];
                    for (int i = 0; i < allModelData.size(); i++) {
                        bchArray[i] = allModelData.get(i).getSt();
                    }
                    DownloadData downloadData = new DownloadData();
                    downloadData.GetTaskData(mContext, PhoneState.getDeviceId(mContext), mCbyf, bchArray);
                }
                DownloadDialog.this.dismiss();
                break;
        }


    }

    /**
     * 对item导致maincheckbox改变做监听
     */
    public interface AllCheckListener {
        void onCheckedChanged(boolean b);
    }
}
