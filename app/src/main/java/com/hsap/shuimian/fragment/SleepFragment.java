package com.hsap.shuimian.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.hsap.shuimian.R;
import com.hsap.shuimian.activity.OpenActivity;
import com.hsap.shuimian.base.BaseFragment;
import com.hsap.shuimian.bean.Bean;
import com.hsap.shuimian.utils.ConstantUtils;
import com.hsap.shuimian.utils.SpUtils;
import com.hsap.shuimian.utils.ToastUtils;
import com.medica.restonsdk.Constants;
import com.medica.restonsdk.bluetooth.RestOnHelper;
import com.medica.restonsdk.domain.BleDevice;
import com.medica.restonsdk.domain.RealTimeData;
import com.medica.restonsdk.domain.Summary;
import com.medica.restonsdk.interfs.BleScanListener;
import com.medica.restonsdk.interfs.Method;
import com.medica.restonsdk.interfs.RealtimeDataCallback;
import com.medica.restonsdk.interfs.ResultCallback;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by zhao on 2018/1/30.
 */

public class SleepFragment extends BaseFragment {

    @BindView(R.id.sleepToolbar)
    Toolbar sleepToolbar;
    private final String TAG="SleepFragment";
    private RestOnHelper helper=null;
    private ArrayList<Bean> bledeviceList=new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Log.e(TAG, "handleMessage: ++++" );
                    Log.e(TAG, "handleMessage: "+Thread.currentThread().getName());
                    helper.getDeviceStatus(new ResultCallback() {
                        @Override
                        public void onResult(Method method, final Object o) {
                                  mActivity.runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          Log.e(TAG, "run: "+o.toString() );
                                      }
                                  });
                        }
                    });
                    helper.seeRealtimeData(new RealtimeDataCallback() {
                        @Override
                        public void handleRealtimeData(RealTimeData realTimeData) {
                            Log.e(TAG, "handleRealtimeData: "+realTimeData.toString() );
                            Log.e(TAG, "handleRealtimeData: "+realTimeData.heartRate );
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(TAG, "handleRealtimeData: -----------" );
                                   // realtimeData(realTimeData);
                                }
                            });
                        }
                        @Override
                        public void onResult(Method method, final Object o) {
                            Log.e(TAG, "onResult: -----------"+o.toString() );
                            Log.e(TAG, "RealtimeDataonResult: "+Thread.currentThread().getName());
                        }
                    });
                    break;
                case 1:
                    helper.startCollect(new ResultCallback() {
                        @Override
                        public void onResult(Method method, Object o) {
                            Log.e(TAG, "onResult: "+o.toString());
                            if ((boolean) o){
                                //更新数据
                               /* mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });*/
                                Log.e(TAG, "startCollect: "+Thread.currentThread().getName() );
                                mHandler.sendEmptyMessage(0);
                            }else {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showToast(mActivity,"采集失败，请重新开启睡眠");
                                    }
                                });}}
                    });
                    break;
                case 2:
                    Log.e(TAG, "handleMessage: aaaaaaaaa" );
                    realtimeData((RealTimeData) msg.obj);
                    break;
                    default:
            }
        }
    };

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_sleep, null);
        return view;
    }

    @Override
    public void initData() {
        sleepToolbar.setTitle("睡眠");
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(sleepToolbar);
    }

    @Override
    public void initListener() {

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_sleep,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.begin:
                ToastUtils.showToast(mActivity,"AAAA");
                //startActivity(new Intent(mActivity, OpenActivity.class));
                beginSleep();
                break;
            case R.id.end:
                Toast.makeText(mActivity, "关闭睡眠", Toast.LENGTH_SHORT).show();
                endSleep();
                break;
            default:
        }
        return true;
    }

    private void endSleep() {
        final LoadingDialog dialog = new LoadingDialog(mActivity);
        dialog.setLoadingText("正在关闭")
                .setSuccessText("关闭成功")
                .setFailedText("关闭失败")
                .setInterceptBack(true)
                .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                .setRepeatCount(0)
                .show();
          if (helper!=null){
             switch (helper.getConnectState()){
                 case 0:
                     helper=null;
                     dialog.loadSuccess();
                     break;
                 case 1:
                     helper.stopScan();
                     helper=null;
                     dialog.loadSuccess();
                     break;
                 case 2:
                     helper.disconnect();
                     helper=null;
                     dialog.loadSuccess();
                     break;
                 default:
                     dialog.loadFailed();
                     ToastUtils.showToast(mActivity,"请关闭蓝牙");
                     break;
             }
          }else {
              dialog.close();
              ToastUtils.showToast(mActivity,"当前未开启睡眠");
          }
    }

    //开启睡眠
    private void beginSleep() {
        helper = RestOnHelper.getInstance(mActivity);
        if (helper.isSupportBle()){
            if (!helper.isBluetoothOpen()){
                if(Build.VERSION.SDK_INT>=23){
                    //申请蓝牙权限
                    AndPermission.with(mActivity)
                            .permission(Permission.ACCESS_COARSE_LOCATION,Permission.ACCESS_FINE_LOCATION)
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
                }else {
                    helper.openBluetooth();
                    linkBluetooth();
                }
            }else {
                linkBluetooth();
            }
        }else {

            Toast.makeText(mActivity, "当前设备不支持BLE", Toast.LENGTH_SHORT).show();
        }
    }

    //搜索链接设备
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
                         if(!bledeviceList.contains(bleDevice)){
                             bledeviceList.add(new Bean(bleDevice.deviceId,bleDevice.deviceName,bleDevice.address));
                         }
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
                                 switch (helper.getConnectState()){
                                     case 0:
                                         dialog.loadFailed();
                                         break;
                                     case 1:
                                         dialog.close();
                                         Toast.makeText(mActivity, "连接超时", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 2:
                                         dialog.loadSuccess();
                                         mHandler.sendEmptyMessage(1);
                                         break;
                                     default:break;
                                 }
                             }
                         });
                         Log.e(TAG, "onResult: "+ helper.getConnectState());
                     }
                 });
             }
         });
    }
        //获取睡眠时实时数据
    private void realtimeData(RealTimeData realTimeData) {
        //心跳频率
        short heartRate = realTimeData.heartRate;
        //呼吸频率
        byte breathRate = realTimeData.breathRate;
        byte status = realTimeData.status;
        Log.e(TAG, "heartRate: "+heartRate );
        Log.e(TAG, "breathRate: "+breathRate );
        Log.e(TAG, "status: "+ status);
        status= Constants.SleepStatusType.SLEEP_LEAVE;
    }

}
