package com.example.leonardo.watermeter.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leonardo.watermeter.R;
import com.example.leonardo.watermeter.entity.DetailData;
import com.example.leonardo.watermeter.global.GlobalData;

import org.litepal.crud.DataSupport;

import java.util.Timer;
import java.util.TimerTask;

public class ManualInputActivity extends Activity {

    private DetailData currentData;
    private String tid;
    private TextView homePhoneNumber, lastCbDate, ticketName, address, caliber, meterNum, lastCbNumber, readWaterSum, averageWaterSum, cardNum;
    private EditText currentCbNumber;
    private double lastRead, currentRead, averageRead;
    private double chazhi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_manual_input);

        /*接收上个页面传来的t_id信息*/
        Bundle bundle = this.getIntent().getExtras();
        tid = bundle.getString("tid");

        currentData = DataSupport.where("t_id = ?", tid).findFirst(DetailData.class);

        initView();
        setData();
        setEditLisener();

        /*为了弹出数字键盘*/
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) currentCbNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(currentCbNumber, 0);
            }
        }, 500);
    }

    private void initView() {
        homePhoneNumber = (TextView) findViewById(R.id.homePhoneNumber);
        lastCbDate = (TextView) findViewById(R.id.lastCbDate);
        ticketName = (TextView) findViewById(R.id.ticketName);
        address = (TextView) findViewById(R.id.address);
        caliber = (TextView) findViewById(R.id.caliber);
        meterNum = (TextView) findViewById(R.id.meterNum);
        cardNum = (TextView) findViewById(R.id.cardNum);
        lastCbNumber = (TextView) findViewById(R.id.lastCbNumber);
        readWaterSum = (TextView) findViewById(R.id.readWaterSum);
        currentCbNumber = (EditText) findViewById(R.id.currentCbNumber);
        averageWaterSum = (TextView) findViewById(R.id.averageWaterSum);
        currentCbNumber.requestFocus();//指定焦点
        currentCbNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//设置文本框只能输入数字和小数点
    }

    private void setData() {
        homePhoneNumber.setText(currentData.getT_tel_phone());
        lastCbDate.setText(currentData.getT_latest_date());
        ticketName.setText(currentData.getT_ticket_name());
        address.setText(currentData.getT_location());
        caliber.setText(currentData.getT_caliber());
        cardNum.setText(currentData.getT_card_num());
        meterNum.setText(currentData.getT_meter_num());
        lastCbNumber.setText(currentData.getT_latest_index());
        averageWaterSum.setText(currentData.getT_average());
        lastRead = Double.valueOf(lastCbNumber.getText().toString());
        if (!currentCbNumber.getText().toString().equals("") && currentCbNumber.getText().toString() != null) {
            currentRead = Double.valueOf(currentCbNumber.getText().toString());
        }
    }

    private void setEditLisener() {
        currentCbNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentReadString = currentCbNumber.getText().toString();
                if (!currentReadString.isEmpty()) {
                    currentRead = Double.valueOf(currentReadString);
                    chazhi = currentRead - lastRead;
                    readWaterSum.setText(chazhi + "");
                    averageRead = Double.parseDouble(currentData.getT_average());
                    if (chazhi > 1.2 * averageRead) {
                        Toast.makeText(getApplicationContext(), "您本月用水量超过平均值，请节约用水", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    readWaterSum.setText("");
                }
            }
        });
    }

    /**
     * 保存手抄数据
     *
     * @param view
     */
    public void saveManualInput(View view) {
        if (currentRead >= lastRead) {
            currentData.setT_cur_read_water_sum(readWaterSum.getText().toString());
                /*更新数据库中当前Task信息*/
            ContentValues values = new ContentValues();
            values.put("t_cur_read_water_sum", readWaterSum.getText().toString());
            values.put("t_cur_meter_data", currentCbNumber.getText().toString());
            DataSupport.updateAll(DetailData.class, values, "t_id = ?", currentData.getT_id());
            Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
            GlobalData.isFromManual = true;
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "需要大于等于上期读数", Toast.LENGTH_SHORT).show();
        }

    }
}
