package com.wsj.www.qq50slider.slidermenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wsj.www.qq50slider.R;

import java.util.Random;

public class SliderMenuActivity extends AppCompatActivity {

    private static final String TAG = "SliderMenuActivity";

    private ListView lv_menu ;
    private ListView lv_content;
    private SliderMenu sm_main;
    private ImageView iv_header;
    private MyLinearLayout mll_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_menu);

        initUi();
        initData();

    }

    private void initUi() {
        lv_menu = (ListView) findViewById(R.id.lv_menu);
        lv_content = (ListView) findViewById(R.id.lv_content);
        sm_main = (SliderMenu) findViewById(R.id.sm_main);
        iv_header = (ImageView) findViewById(R.id.iv_header);
        mll_content = (MyLinearLayout) findViewById(R.id.mll_content);
    }

    private void initData() {
        // 设置菜单
        lv_menu.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                Constant.sCheeseStrings){
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // 修改文字颜色.
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        });
        // 设置内容区域.
        lv_content.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                Constant.NAMES){
            // 实现文字缩放效果

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                // 1. 缩放0.5;
                ViewHelper.setScaleX(textView, 0.5f);
                ViewHelper.setScaleY(textView, 0.5f);

                // 2. 使用属性动画放大
                ViewPropertyAnimator.animate(textView).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(textView).scaleY(1).setDuration(350).start();
                return textView;
            }
        });

        // 设置监听
        sm_main.setListener(new SliderMenu.SliderMenuListener() {
            @Override
            public void onOpened() {
                Log.d(TAG, "onOpened: ");
                // 菜单移动到任意位置.
                lv_menu.smoothScrollToPosition(new Random().nextInt(lv_menu.getCount()));
            }

            @Override
            public void onClosed() {
                Log.d(TAG, "onClosed: ");
                // 头像 抖动
                ViewPropertyAnimator.animate(iv_header)
                        .translationXBy(30)
                        .setInterpolator(new CycleInterpolator(4))
                        .setDuration(500)
                        .start();
            }

            @Override
            public void onDragging(float fraction) {
                // Log.d(TAG, "onDragging: ");
            }
        });
        // 用于控制控制事件拦截.
        mll_content.setSliderMenu(sm_main);
    }
}
