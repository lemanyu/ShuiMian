package com.hsap.shuimian.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hsap.shuimian.R;
import com.hsap.shuimian.base.BaseFragment;
import com.medica.restonsdk.bluetooth.RestOnHelper;
import com.medica.restonsdk.domain.BleDevice;
import com.medica.restonsdk.interfs.BleScanListener;
import com.medica.restonsdk.interfs.Method;
import com.medica.restonsdk.interfs.ResultCallback;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zhao on 2018/1/30.
 */

public class ReprotFragment extends BaseFragment implements View.OnClickListener{

    @BindView(R.id.reportToolbar)
    Toolbar reportToolbar;
    @BindView(R.id.tvReportTitle)
    TextView tvReportTitle;
    @BindView(R.id.btReportCalendar)
    Button btReportCalendar;
    @BindView(R.id.btReportUpdate)
    Button btReportUpdate;
    Unbinder unbinder;
    private RestOnHelper helper;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    break;
                default:
            }
            super.handleMessage(msg);

        }
    };

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_report, null);
        return view;
    }

    @Override
    public void initData() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(reportToolbar);
    }

    @Override
    public void initListener() {
        tvReportTitle.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_report, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lianjie:
                lianjie();
                break;

            default:
        }
        return true;
    }

    private void lianjie() {
        helper = RestOnHelper.getInstance(mActivity);
        if (helper.isSupportBle()) {
            if (!helper.isBluetoothOpen()) {
                if (Build.VERSION.SDK_INT >= 23) {
                    //申请蓝牙权限
                    AndPermission.with(mActivity)
                            .permission(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                    linkBluetooth();
                                }
                            }).onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            Toast.makeText(mActivity, "不给不让你玩了", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    helper.openBluetooth();
                    linkBluetooth();
                }
            } else {
                linkBluetooth();
            }
        } else {

            Toast.makeText(mActivity, "当前设备不支持BLE", Toast.LENGTH_SHORT).show();
        }
    }

    private void linkBluetooth() {
        final LoadingDialog dialog = new LoadingDialog(mActivity);
        dialog.setLoadingText("获取关联中")
                .setSuccessText("连接成功")
                .setFailedText("连接失败")
                .setInterceptBack(true)
                .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                .setRepeatCount(0);

        helper.scanBleDevice(new BleScanListener() {
            @Override
            public void onBleScanStart() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });

            }

            @Override
            public void onBleScan(final BleDevice bleDevice) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onBleScanFinish() {
                //现在一个，后期删除加入选择
                helper.connDevice(new ResultCallback() {
                    @Override
                    public void onResult(Method method, Object o) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (helper.getConnectState()) {
                                    case 0:
                                        dialog.loadFailed();
                                        break;
                                    case 1:
                                        dialog.close();
                                        Toast.makeText(mActivity, "连接超时", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2:
                                        dialog.loadSuccess();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
