package com.wsj.www.qq50slider.slidermenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/8
 * 作用 : 自定义线性布局, 可以拦截并消耗事件.
 * 备注 :
 *      如果想要一个ViewGroup拦截事件.那么必须做到以下两点.
 *          1. 在onInterceptTouchEvent() 方法中返回true, 表示拦截事件.
 *          2. 在onTouchEvent() 方法中. 返回true, 表示消耗事件.
 */

public class MyLinearLayout extends LinearLayout {

    private SliderMenu mSliderMenu;

    public void setSliderMenu(SliderMenu sliderMenu) {
        mSliderMenu = sliderMenu;
    }

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mSliderMenu != null && (mSliderMenu.isOpened())) {
            // 拦截事件.
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSliderMenu != null && (mSliderMenu.isOpened())) {
            // 消耗事件.
            return true;
        }
        return super.onTouchEvent(event);
    }
}
