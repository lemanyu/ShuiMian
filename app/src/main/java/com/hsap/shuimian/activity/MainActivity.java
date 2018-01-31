package com.hsap.shuimian.activity;

import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hsap.shuimian.R;
import com.hsap.shuimian.fragment.ReprotFragment;
import com.hsap.shuimian.fragment.SleepFragment;
import com.medica.restonsdk.bluetooth.RestOnHelper;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";

    private RadioButton[] rbs=new RadioButton[2];
    private SleepFragment sf;
    private ReprotFragment rf;
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        RadioGroup rgMain = findViewById(R.id.rgMain);
        rgMain.setOnCheckedChangeListener(this);
        RadioButton rbSleep = findViewById(R.id.rbSleep);
        rbSleep.setChecked(true);
        rbs[0]= findViewById(R.id.rbSleep);
        rbs[1]=findViewById(R.id.rbReport);
        for (int i = 0; i < rbs.length; i++) {

            Drawable[] drawables = rbs[i].getCompoundDrawables();
                Rect rect = new Rect(0, 0,
                        drawables[1].getMinimumWidth()/6,
                        drawables[1].getMinimumHeight()/6);
                drawables[1].setBounds(rect);
                rbs[i].setCompoundDrawables(null,drawables[1],null,null);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        hideAllFragment(ft);
        switch (checkedId){
            case R.id.rbSleep:
                if (sf == null) {
                    sf = new SleepFragment();
                    ft.add(R.id.fl_main, sf);
                } else {ft.show(sf);}
                break;
            case R.id.rbReport:
                if (rf== null) {
                    rf = new ReprotFragment();
                    ft.add(R.id.fl_main, rf);
                } else {ft.show(rf);}
                break;
            default:
        }
        ft.commit();
    }

    private void hideAllFragment(FragmentTransaction ft) {
        if (sf != null) {ft.hide(sf);}
        if (rf != null) {ft.hide(rf);}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
