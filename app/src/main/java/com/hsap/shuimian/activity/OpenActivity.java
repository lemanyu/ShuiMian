package com.hsap.shuimian.activity;


import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.hsap.shuimian.R;
import com.hsap.shuimian.base.BaseBackActivity;
import com.medica.restonsdk.bluetooth.RestOnHelper;
import com.medica.restonsdk.interfs.BleScanListener;
import com.medica.restonsdk.interfs.Method;
import com.medica.restonsdk.interfs.ResultCallback;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.ArrayList;
import butterknife.BindView;


public class OpenActivity extends BaseBackActivity {
    private static final String TAG = "OpenActivity";
    @BindView(R.id.lvDialog)
    ListView lvDialog;
    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    private RestOnHelper helper;
    private ArrayList<String> addressList=new ArrayList<>();
    private ArrayList<String> nameList=new ArrayList<>();
    @Override
    public int getLayoutId() {

        return R.layout.activity_open;
    }

    @Override
    public void initView() {
        toolbarTitle.setText("请选择要连接的设备");
    }

    @Override
    public void initData() {
        helper = RestOnHelper.getInstance(this);
        initReston();


    }

    private void initReston() {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.setLoadingText("加载中")
                .setSuccessText("加载成功")
                .setFailedText("加载失败")
                .setInterceptBack(true)
                .setLoadSpeed(LoadingDialog.Speed.SPEED_ONE)
                .setRepeatCount(0)
                .show();
        if(helper.isSupportBle()){
                if (!helper.isBluetoothOpen()){

                }
                helper.openBluetooth();
                helper.scanBleDevice(new BleScanListener() {
                    @Override
                    public void onBleScanStart() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });


                    }
                    @Override
                    public void onBleScan(final com.medica.restonsdk.domain.BleDevice bleDevice) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!addressList.contains(bleDevice.address)){
                                    addressList.add(bleDevice.address);
                                    nameList.add(bleDevice.deviceName);
                                }
                                Log.e(TAG, "onBleScan: "+bleDevice.address );
                                Log.e(TAG, "onBleScan: "+bleDevice.deviceId );
                                Log.e(TAG, "onBleScan: "+bleDevice.deviceName );
                                Log.e(TAG, "onBleScan: "+bleDevice.modelName );
                            }
                        });

                    }
                    @Override
                    public void onBleScanFinish() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "onBleScanFinish: " );
                                for (int i = 0; i <nameList.size(); i++) {
                                    Log.e(TAG, "run: -------"+nameList.get(i) );
                                    Log.e(TAG, "run: -------"+addressList.get(i) );
                                }
                                dialog.loadSuccess();
                                helper.connDevice(new ResultCallback() {
                                    @Override
                                    public void onResult(Method method, Object o) {
                                        Log.e(TAG, "onResult: "+ helper.getConnectState());
                                    }
                                });
                            }
                        });
                    }
                });

        }else {
            Toast.makeText(this, "当前设备不支持BLE", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void initListener() {
        back.setOnClickListener(this);
    }

    @Override
    public void processClick(View v) {

    }


}
