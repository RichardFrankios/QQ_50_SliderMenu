package com.wsj.www.qq50slider.test;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/5
 * 作用 : 自定义ViewGroup
 */

public class DragLayout extends ViewGroup {
    private static final String TAG = "DragLayout";


    private TextView mRedView;
    private TextView mBlueView;


    /**
     * 拖拽帮助类.
     */
    ViewDragHelper mViewDragHelper;





    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        // 创建帮助类.
        mViewDragHelper = ViewDragHelper.create(this, mCallback);
    }

    /**
     * 拖拽帮助类回调
     */
    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        /**
         * 判断是否捕获当前View的拖拽事件.
         * @param child     拖拽的子View
         * @param pointerId
         * @return true : 捕获并解析
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mBlueView || child == mRedView;
        }

        /**
         * 被拖拽的View在水平方向上的位置变化
         * @param child  被拖拽的子View
         * @param left   该View的当前的Left值 (被拖拽后的位置.)
         * @param dx     水平方向的拖拽距离.
         * @return       View的最终位置, 也就说我们可以通过这个返回值来确定我们给的View在拖拽后的位置.
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 1. 直接返回 left 就可以实现跟随鼠标移动的需求.
            // 2. 控制移动边界.

            if (left < 0) {
                left = 0;
            } else if (left > (getMeasuredWidth() - child.getMeasuredWidth())) {
                left = (getMeasuredWidth() - child.getMeasuredWidth());
            }

            // 6. 在移动过程中使用动画

            // 移动百分比.
            float fraction = left * 1.0f / (getMeasuredWidth() - child.getMeasuredWidth());

            Log.d(TAG, "clampViewPositionHorizontal: fraction :"+ fraction);
//            ViewHelper.setAlpha(mRedView, 1 - fraction);
            ViewHelper.setRotation(mRedView, 360 * fraction);

            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {

            if (top < 0) {
                top = 0;
            } else if (top > (getMeasuredHeight() - child.getMeasuredHeight())) {
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;
        }

        /**
         * 位置发生改变时调用
         * @param changedView  位置位置变化的View
         * @param left         当前left
         * @param top          当前top
         * @param dx           变化dx
         * @param dy           变化dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            // 3. 实现两个子View的粘附效果
            if (changedView == mRedView) {
                mBlueView.layout(left, top + mRedView.getMeasuredHeight(), left + mBlueView.getMeasuredWidth(),
                        top + mBlueView.getMeasuredHeight());
            } else if (changedView == mBlueView) {
                mRedView.layout(left, top - mRedView.getMeasuredHeight(), left + mRedView.getMeasuredWidth(),
                        top);
            }
        }

        /**
         * 当手指抬起是调用
         * @param releasedChild 被拖拽的View
         * @param xvel          xVel 速度.
         * @param yvel          yVel 速度.
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            // 4. 实现了吸边效果.
            final int centerLeft = (getMeasuredWidth() / 2) - releasedChild.getMeasuredWidth()/2;

            if (releasedChild.getLeft() < centerLeft) {
                // 向左侧粘附.
                mViewDragHelper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            } else if (releasedChild.getLeft() > centerLeft) {
                mViewDragHelper.smoothSlideViewTo(releasedChild,
                        getMeasuredWidth() - releasedChild.getMeasuredWidth(), releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }
        }
    };

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }

    /**
     * View事件分发机制中用来判断ViewGroup是否拦截事件的方法.
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 事件拦截.
        mViewDragHelper.processTouchEvent(event);
        // 表示当前的ViewGroup拦截事件.
        return true;
    }

    /**
     * 当当前View的XML文件的所有标签都被读取完成后调用这个方法,
     * 此时会知道自己有几个自View, 因此常在这个方法中初始化自View的引用.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mRedView = (TextView) getChildAt(0);
        mBlueView = (TextView) getChildAt(1);
    }

    /**
     * 测量子View
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 方法一 简单方法.
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // 方法二 计算指定的子View
//        measureChild(mBlueView, widthMeasureSpec, heightMeasureSpec);
//        measureChild(mRedView, widthMeasureSpec, heightMeasureSpec);
        // 方法三 麻烦, 自己构造尺寸
//        MeasureSpec.makeMeasureSpec(mRedView.getLayoutParams().width, MeasureSpec.EXACTLY);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = getPaddingTop();
        int left = getPaddingLeft();
        mRedView.layout(left, top,
                left + mRedView.getMeasuredWidth(),
                top + mRedView.getMeasuredHeight());
        top = mRedView.getBottom();
        mBlueView.layout(left, top,
                left + mBlueView.getMeasuredWidth(),
                top + mBlueView.getMeasuredHeight());

    }
}
