package com.hsap.shuimian.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;


import com.hsap.shuimian.R;
import com.hsap.shuimian.utils.ActivityManagerUtils;
import com.hsap.shuimian.utils.AndroidWorkaround;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * Created by zhao on 2017/11/20.
 */
public abstract class BaseBackActivity extends AppCompatActivity implements SlidingPaneLayout.PanelSlideListener,View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initSlideBackClose();
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
        }
        ActivityManagerUtils.getInstance().addActivity(this);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }
    private void initSlideBackClose() {
        if (isSupportSwipeBack()) {
            SlidingPaneLayout slidingPaneLayout = new SlidingPaneLayout(this);
            // 通过反射改变mOverhangSize的值为0，
            // 这个mOverhangSize值为菜单到右边屏幕的最短距离，
            // 默认是32dp，现在给它改成0
            try {
                Field overhangSize = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
                overhangSize.setAccessible(true);
                overhangSize.set(slidingPaneLayout, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            slidingPaneLayout.setPanelSlideListener(this);
            slidingPaneLayout.setSliderFadeColor(getResources()
                    .getColor(android.R.color.transparent));

            // 左侧的透明视图
            View leftView = new View(this);
            leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            slidingPaneLayout.addView(leftView, 0);

            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();


            // 右侧的内容视图
            ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
            decorChild.setBackgroundColor(getResources()
                    .getColor(android.R.color.white));
            decorView.removeView(decorChild);
            decorView.addView(slidingPaneLayout);

            // 为 SlidingPaneLayout 添加内容视图
            slidingPaneLayout.addView(decorChild, 1);
        }
    }
    protected boolean isSupportSwipeBack() {
        return true;
    }
    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            default:
                processClick(v);
                break;
        }
    }

    @Override
    public void onPanelOpened(View panel) {
        finish();
    }

    @Override
    public void onPanelClosed(View panel) {

    }
    public abstract int getLayoutId();
    public abstract void initView();
    public abstract void initData();
    public abstract void initListener();
    public abstract void processClick(View v);
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManagerUtils.getInstance().finishActivity(this);
    }
}
