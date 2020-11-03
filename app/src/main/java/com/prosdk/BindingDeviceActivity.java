package com.prosdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.leonardo.watermeter.R;
import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.ILoginDeviceCallback;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.LoginHelper;
import com.macrovideo.sdk.objects.DeviceInfo;
import com.macrovideo.sdk.objects.LoginParam;
import com.objecteye.author.AuthorApplication;

public class BindingDeviceActivity extends Activity {

    private Button btBinding;
    private EditText etDeviceID, etDeviceUser, etDevicePwd;
    private ProgressBar progress;
    private RadioGroup radioGroup;
    private RadioButton standardDefinitionRD;
    private RadioButton highDefinitionRD;
    static final int HANDLE_MSG_CODE_LOGIN_RESULT = 0x10;
    public static DeviceInfo deviceInfo = null;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == HANDLE_MSG_CODE_LOGIN_RESULT) {
                progress.setVisibility(View.GONE);
                switch (msg.arg2) {
                    case ResultCode.RESULT_CODE_SUCCESS:
                        Bundle bundle = msg.getData();
                        bundle.putBoolean("isOnlyShow", true);  //是否仅仅实时预览
                        Intent intent = new Intent(BindingDeviceActivity.this, NVPlayerShowActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL:
                        ShowAlert(getString(R.string.alert_title_login_failed) + "  (" + getString(R.string.notice_Result_BadResult) + ")", getString(R.string.alert_connect_tips));
                        break;
                    case ResultCode.RESULT_CODE_FAIL_VERIFY_FAILED:
                        ShowAlert(getString(R.string.alert_title_login_failed), getString(R.string.notice_Result_VerifyFailed));
                        break;
                    case ResultCode.RESULT_CODE_FAIL_USER_NOEXIST:
                        progress.setVisibility(View.GONE);
                        ShowAlert(getString(R.string.alert_title_login_failed), getString(R.string.notice_Result_UserNoExist));
                        break;
                    case ResultCode.RESULT_CODE_FAIL_PWD_ERROR:
                        ShowAlert(getString(R.string.alert_title_login_failed), getString(R.string.notice_Result_PWDError));
                        break;
                    case ResultCode.RESULT_CODE_FAIL_OLD_VERSON:
                        ShowAlert(getString(R.string.alert_title_login_failed), getString(R.string.notice_Result_Old_Version));
                        break;
                    default:
                        ShowAlert(getString(R.string.alert_title_login_failed) + "  (" + getString(R.string.notice_Result_ConnectServerFailed) + ")", "");
                        break;

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binding_device_activity);
        initViews();
        initListeners();
        initDatas();
    }

    private void initDatas() {
        int definition = LocalDefines.getDeviceDefinition(AuthorApplication.getContext());
        switch (definition) {
            case LocalDefines.IMAGE_STANDARD_DEFINITION:
                standardDefinitionRD.setChecked(true);
                break;
            case LocalDefines.IMAGE_HIGH_DEFINITION:
                highDefinitionRD.setChecked(true);
                break;
            default:
                highDefinitionRD.setChecked(true);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceInfo deviceInfoFromSP = LocalDefines.getDeviceInfoFromSP(AuthorApplication.getContext());
        if (deviceInfoFromSP != null) {
            deviceInfo = deviceInfoFromSP;
        }
        if (deviceInfo != null) {
            if (deviceInfo.getnDevID() > 0) {
                etDeviceID.setText(String.valueOf(deviceInfo.getnDevID()));
            }
            if (!TextUtils.isEmpty(deviceInfo.getStrUsername())) {
                etDeviceUser.setText(deviceInfo.getStrUsername());
            }
            if (!TextUtils.isEmpty(deviceInfo.getStrPassword())) {
                etDevicePwd.setText(deviceInfo.getStrPassword());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (deviceInfo != null) {
            LocalDefines.setDeviceInfoToSP(AuthorApplication.getContext(), deviceInfo);
        }
    }


    /**
     * 初始化控件
     */
    private void initViews() {
        btBinding = findViewById(R.id.bt_binding);
        etDeviceID = findViewById(R.id.et_device_id);
        etDeviceUser = findViewById(R.id.et_device_username);
        etDevicePwd = findViewById(R.id.et_device_password);
        progress = findViewById(R.id.progressbar);
        radioGroup = findViewById(R.id.radioGroupID);
        standardDefinitionRD = findViewById(R.id.standardDefinition);
        highDefinitionRD = findViewById(R.id.highDefinition);
    }

    /**
     * 添加控件监听
     */
    private void initListeners() {
        btBinding.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceID = etDeviceID.getText().toString().trim();
                String deviceUser = etDeviceUser.getText().toString().trim();
                String devicePwd = etDevicePwd.getText().toString().trim();
                if (TextUtils.isEmpty(deviceID)) {
                    if (deviceInfo == null) {
                        Toast.makeText(BindingDeviceActivity.this, "请输入设备ID", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (TextUtils.isEmpty(deviceUser)) {
                    if (deviceInfo == null) {
                        Toast.makeText(BindingDeviceActivity.this, "请输入设备用户名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!TextUtils.isEmpty(deviceID)) {
                    deviceInfo = new DeviceInfo(-1, Integer.parseInt(deviceID), deviceID, "192.168.1.1", 8800, deviceUser, devicePwd, "ABC", deviceID + ".nvdvr.net", Defines.SERVER_SAVE_TYPE_ADD);
                }
                progress.setVisibility(View.VISIBLE);
                bindingDevice();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == standardDefinitionRD.getId()) {
                    LocalDefines.setDeviceDefinition(AuthorApplication.getContext(), LocalDefines.IMAGE_STANDARD_DEFINITION);
                } else if (checkedId == highDefinitionRD.getId()) {
                    LocalDefines.setDeviceDefinition(AuthorApplication.getContext(), LocalDefines.IMAGE_HIGH_DEFINITION);
                }
            }
        });
    }


    /**
     * 设备登录，绑定wifi设备
     */
    private void bindingDevice() {
        LoginParam loginParam = new LoginParam(deviceInfo, Defines.LOGIN_FOR_PLAY);
        int loginResult = LoginHelper.loginDevice(AuthorApplication.getContext(), loginParam, new ILoginDeviceCallback() {
            @Override
            public void onLogin(LoginHandle loginHandle) {
                if (loginHandle != null && loginHandle.getnResult() == ResultCode.RESULT_CODE_SUCCESS) {
                    // 登录成功
                    Message msg = handler.obtainMessage();
                    msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                    msg.arg2 = ResultCode.RESULT_CODE_SUCCESS;
                    Bundle data = new Bundle();
                    data.putParcelable("device_param", loginHandle);
                    msg.setData(data);
                    handler.sendMessage(msg);
                } else if (loginHandle != null) {
                    // 登录失败，可查看具体错误码
                    Message msg = handler.obtainMessage();
                    msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                    msg.arg2 = loginHandle.getnResult();
                    handler.sendMessage(msg);
                } else {
                    // 登录失败
                    Message msg = handler.obtainMessage();
                    msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
                    msg.arg2 = ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL;
                    handler.sendMessage(msg);
                }
            }
        });

        if (loginResult != 0) {
            // 登录失败，参数错误等
            Message msg = handler.obtainMessage();
            msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
            msg.arg2 = ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL;
            handler.sendMessage(msg);
        }
    }

    /**
     * 自定义dialog
     *
     * @param title
     * @param msg
     */
    private void ShowAlert(String title, String msg) {
        try {
            new AlertDialog.Builder(BindingDeviceActivity.this).setTitle(title)
                    .setMessage(msg)
                    .setIcon(R.drawable.ic_watermeter_launcher)
                    .setPositiveButton(getString(R.string.alert_btn_OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(RESULT_OK);
                        }
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

