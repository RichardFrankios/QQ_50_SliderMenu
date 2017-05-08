package com.wsj.www.qq50slider.slidermenu;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/8
 * 作用 : QQ Slider Menu 自定义ViewGroup.
 */

public class SliderMenu extends FrameLayout {


    private static final String TAG = "SliderMenu";

    /**
     * 左侧菜单栏
     */
    private ViewGroup mMenuLayout;
    /**
     * 内容布局.
     */
    private ViewGroup mContentLayout;

    /**
     * 拖拽事件帮助类.
     */
    private ViewDragHelper mViewDragHelper;

    /**
     * 上下文.
     */
    private Context mContext;


    /**
     * 宽度.
     */
    private int mWidth;

    /**
     * 拖拽宽度.
     */
    private float mDragRange;


    /**
     * Int 估值器
     */
    private IntEvaluator mIntEvaluator;
    /**
     * Float 估值器.
     */
    private FloatEvaluator mFloatEvaluator;

    /**
     * SliderMenu 的状态.
     */
    enum SliderMenuState {
        Opened,Closed
    }

    private SliderMenuState mCurrentState = SliderMenuState.Closed;

    private SliderMenuListener mListener;

    public void setListener(SliderMenuListener listener) {
        mListener = listener;
    }

    /**
     * SliderMenu 监听者.
     */
    public interface SliderMenuListener {
        /**
         * 开启
         */
        public void onOpened();

        /**
         * 关闭
         */
        public void onClosed();

        /**
         * 拖拽
         */
        public void onDragging(float fraction);
    }


    public SliderMenu(Context context) {
        this(context, null);
    }

    public SliderMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliderMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        initData();


    }

    /**
     * 是否打开.
     */
    public boolean isOpened(){
        return mCurrentState == SliderMenuState.Opened;
    }

    private void initData() {
        // 创建 ViewDragHelper
        mViewDragHelper = ViewDragHelper.create(this, mCallback);

        mIntEvaluator = new IntEvaluator();
        mFloatEvaluator = new FloatEvaluator();

    }

    /**
     * ViewDragHelper帮助类回调方法.
     */
    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        /**
         * 用于判断是否捕获当前View的触摸事件.
         * @param child      当前View
         * @return  true 表示捕获并处理事件.
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 如果是菜单, 或者内容布局则捕获事件.
            return child == mMenuLayout || child == mContentLayout;
        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面;
         * 最好不要返回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) mDragRange;
        }

        /**
         * 矫正水平拖拽位置,
         * @param child 发生拖拽的View.
         * @param left  拖拽后View的x
         * @param dx    x,变化量.
         * @return      最终x值.
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {


            // 限制左右的边界.
            if (child == mContentLayout) {
                if (left < 0) left = 0;
                if (left > mDragRange) left = (int) mDragRange;
            }

            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            // 菜单View 是不可以移动的.需要固定一下.
            // 滑动Menu,更正位置.
            if (changedView == mMenuLayout) {
                int newLeft = mContentLayout.getLeft() + dx;
                if (newLeft < 0) newLeft = 0;
                if (newLeft > mDragRange) newLeft = (int) mDragRange;
                mMenuLayout.layout(0, 0, mMenuLayout.getMeasuredWidth(), mMenuLayout.getMeasuredHeight());
                mContentLayout.layout(newLeft, mContentLayout.getTop(),
                        newLeft + mContentLayout.getMeasuredWidth(),
                        mContentLayout.getTop() + mContentLayout.getMeasuredHeight());
            }


            // 设置伴随动画.
            // Log.d(TAG, "onViewPositionChanged: =====" + mContentLayout.getLeft());
            // Log.d(TAG, "onViewPositionChanged: -----" + mDragRange);
            float fraction = (mContentLayout.getLeft() / mDragRange);
            executeAnims(fraction);
            // Log.d(TAG, "onViewPositionChanged: " + fraction);
            // 通知监听
            if (fraction == 0 && (mCurrentState != SliderMenuState.Closed)) {
                // 关闭
                mCurrentState = SliderMenuState.Closed;
                if (mListener != null) {
                    mListener.onClosed();
                }
            } else if (fraction == 1.0 && (mCurrentState != SliderMenuState.Opened)) {
                mCurrentState = SliderMenuState.Opened;
                if (mListener != null) {
                    mListener.onOpened();
                }
            }
            if (mListener != null) {
                mListener.onDragging(fraction);
            }

        }

        /**
         * 松开手指时调用. 更正位置.
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (mContentLayout.getLeft() > mDragRange * 0.5) {
                // 打开
                openMenu();
            } else {
                closeMenu();
            }

            // 用户快速滑动来关闭
            if (Math.abs(xvel) > 200) {
                toggleMenu();
            }

        }
    };

    /**
     * 执行伴随动画.
     */
    private void executeAnims( float fraction ) {
        // 缩小内容区
        ViewHelper.setScaleX(mContentLayout, mFloatEvaluator.evaluate(fraction, 1.0f, 0.8f));
        ViewHelper.setScaleY(mContentLayout, mFloatEvaluator.evaluate(fraction, 1.0f, 0.8f));

        // 移动Menu  负的一半宽度 ~ 0
        ViewHelper.setTranslationX(mMenuLayout, mFloatEvaluator.evaluate(fraction, -mMenuLayout.getMeasuredWidth()/2, 0));

        // 放大菜单
        ViewHelper.setScaleX(mMenuLayout, mFloatEvaluator.evaluate(fraction, 0.5f, 1.0f));
        ViewHelper.setScaleY(mMenuLayout, mFloatEvaluator.evaluate(fraction, 0.5f, 1.0f));

        // 设置透明度
        ViewHelper.setAlpha(mMenuLayout, mFloatEvaluator.evaluate(fraction, 0.3f, 1.0f));

        // 设置背景

//        Log.d(TAG, "executeAnims: " + (Integer) ColorUtils.evaluateColor(fraction,
//                Color.BLACK,
//                Color.TRANSPARENT));
//        getBackground().setColorFilter((Integer) ColorUtils.evaluateColor(fraction,
//                Color.BLACK,
//                Color.RED), PorterDuff.Mode.SRC_OVER);
    }

    /**
     * 关闭动画.
     */
    private void closeMenu() {
        mViewDragHelper.smoothSlideViewTo(mContentLayout, 0, mContentLayout.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 打开动画
     */
    private void openMenu() {
        mViewDragHelper.smoothSlideViewTo(mContentLayout, (int) mDragRange, mContentLayout.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 切换状态
     */
    private void toggleMenu() {
        if (isOpened()) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * 布局文件加载完毕. 在这个方法中初始化子View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SliderMenu layout can only has two children!!!");
        }
        mMenuLayout = (ViewGroup) getChildAt(0);
        mContentLayout = (ViewGroup) getChildAt(1);
    }

    /**
     * 这个方法会在onMeasure() 执行完后执行, 因此可以在这个方法中,
     * 获取自己和子View的宽和高.
     * @param w     当前宽.
     * @param h     当前高.
     * @param oldw  原来宽.
     * @param oldh  原来高.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = getMeasuredWidth();
        // 可以拖拽的宽度是总宽度的 0.6 .
        mDragRange = (float) (int)(mWidth * 0.6f);
    }
}
