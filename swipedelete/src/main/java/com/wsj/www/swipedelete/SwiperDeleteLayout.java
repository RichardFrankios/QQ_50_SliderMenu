package com.wsj.www.swipedelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/9
 * 作用 : 自定义可以滑动的Item布局.
 *
 *      1. 处理滑动冲突 : 解决在水平滑动过程中,垂直滑动.
 *      2. 根据速度判断滑动打开.
 *      3. 此时虽然解决了点击其他的item可以关闭,并且不可以上下滑动. 但是点击自己的时候还是可以上下滑动.
 *          3.1 为ListView添加滑动监听.
 *          3.2 在这里面处理.
 */

public class SwiperDeleteLayout extends FrameLayout {

    private static final String TAG = "SwiperLayou";

    /**
     * 内容View
     */
    private View mContentView;
    /**
     * 删除View
     */
    private View mDeleteView;


    // 尺寸
    private int mContentWidth;
    private int mContentHeight;
    private int mDeleteWidth;
    private int mDeleteHeight;


    // 拖拽帮助类
    private ViewDragHelper mViewDragHelper;

    // 位置
    private float mDownX;
    private float mDownY;

    enum SwipeState {
        Opened, Closed
    }

    /**
     * 当前状态.
     */
    private SwipeState mCurrentState = SwipeState.Closed;

    private Object mTag;

    private OnSwipeStateChangedListener mListener;

    public void setListener(OnSwipeStateChangedListener listener) {
        mListener = listener;
    }

    /**
     * 帮助类CallBack
     */
    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        /**
         * 拦截的Drag.
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mContentView || child == mDeleteView;
        }

        /**
         * 水平拖拽范围, 最好不要为零.
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mDeleteWidth;
        }


        /**
         * 矫正水平滑动.
         * == 处理滑动范围在此方法中.
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if (child == mContentView) {
                if (left > 0) left = 0;
                if (left < - mDeleteWidth) left = -mDeleteWidth;
            } else if (child == mDeleteView) {
                if (left > mContentWidth) left = mContentWidth;
                if (left < mContentWidth - mDeleteWidth) left = mContentWidth - mDeleteWidth;
            }
            return left;
        }

        /**
         * 在此方法中设置View的之间的连带作用, 手动设置View位置.
         * 1. 手动更新位置.
         * 2. 设置开关状态.
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if (changedView == mContentView) {
                // 手动设置DeleteView
                mDeleteView.layout(mDeleteView.getLeft() + dx, mDeleteView.getTop() + dy,
                        mDeleteView.getRight() + dx , mDeleteView.getBottom() + dy);
            } else if (changedView == mDeleteView) {
                mContentView.layout(mContentView.getLeft() + dx, mContentView.getTop() + dy,
                        mContentView.getRight() + dx , mContentView.getBottom() + dy);
            }


            if (mContentView.getLeft() == 0 && (mCurrentState != SwipeState.Closed)) {
                // 关闭
                mCurrentState = SwipeState.Closed;

                if (mListener != null) {
                    mListener.onClosed(mTag);
                }

                SwipeDeleteLayoutManager.getInstance().clearSwipeLayout();
            } else if (mContentView.getLeft() == -mDeleteWidth && (mCurrentState != SwipeState.Opened)){
                // 开启
                mCurrentState = SwipeState.Opened;

                if (mListener != null) {
                    mListener.onOpened(mTag);
                }

                SwipeDeleteLayoutManager.getInstance().setSwiperDeleteLayout(SwiperDeleteLayout.this);
            }

        }

        /**
         * 手指抬起, 在此方法中设置. 周指抬起后的动作, 比如动画移动等.
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (Math.abs(xvel) > 200) {// 速度大于 200 则可以打开
                toggleSwipe();
            } else if (Math.abs(mContentView.getLeft()) < mDeleteWidth / 2) {
                // 关闭.
                closeSwipe();
            } else {
                // 打开
                openSwipe();
            }

        }
    };

    @Override
    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * 打开滑动
     */
    public void openSwipe() {
        mViewDragHelper.smoothSlideViewTo(mContentView,  - mDeleteWidth, mContentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwiperDeleteLayout.this);
    }

    /**
     * 关闭滑动.
     */
    public void closeSwipe() {
        mViewDragHelper.smoothSlideViewTo(mContentView, 0, mContentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwiperDeleteLayout.this);
    }
    public void toggleSwipe() {
        if (mCurrentState == SwipeState.Closed) {
            openSwipe();
        } else {
            closeSwipe();
        }
    }

    /**
     * 弹性滑动时需要.
     */
    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SwiperDeleteLayout.this);
        }
    }

    /**
     * 在这个方法中获取子View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("this layout must has 2 children!!!");
        }
        mContentView = getChildAt(0);
        mDeleteView = getChildAt(1);
    }

    /**
     * 在这个方法中获取自己和子View的尺寸.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 获取 尺寸.
        mContentHeight = mContentView.getMeasuredHeight();
        mContentWidth = mContentView.getMeasuredWidth();
        mDeleteHeight = mDeleteView.getMeasuredHeight();
        mDeleteWidth = mDeleteView.getMeasuredWidth();
    }

    /**
     * 重新布局
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // 布局内容区域View
        mContentView.layout(0, 0, mContentWidth, mContentHeight);
        // 布局删除区域View
        mDeleteView.layout(mContentWidth, 0, mContentWidth + mDeleteWidth, mDeleteHeight);
    }

    public SwiperDeleteLayout(Context context) {
        this(context, null);
    }

    public SwiperDeleteLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwiperDeleteLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, mCallback);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!SwipeDeleteLayoutManager.getInstance().shouldSwipe(this)) {
            // 不可以滑动表示, 有打开的View. 此时不需要父类拦截事件.
            // 关闭, 尽量不要在onTouchEvent中调用, 因为他的调用频率高, 会出现卡顿.
            SwipeDeleteLayoutManager.getInstance().closeSwipe();
            Log.d(TAG, "onInterceptTouchEvent: 拦截事件");
            requestDisallowInterceptTouchEvent(true);

            return true;// 确保在这种情况下消耗事件.
        }
        Log.d(TAG, "onInterceptTouchEvent: " + mViewDragHelper.shouldInterceptTouchEvent(ev));
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!SwipeDeleteLayoutManager.getInstance().shouldSwipe(this)) {
            // 如果, 有打开灯额View则下面的没有必要执行了.
            requestDisallowInterceptTouchEvent(true);
            return true;
        }


        int action = event.getAction();

        float curX = event.getX();
        float curY = event.getY();
        float deltaX = curX - mDownX;
        float deltaY = curY - mDownY;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    // 让父控件不拦截事件.
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent-- deltaX : " + deltaX + " ====  deltaY : " + deltaY);
                if (deltaX == 0 && deltaY == 0) {
                    return false;
                }
                break;
        }
        // 提交事件
        mViewDragHelper.processTouchEvent(event);
        return true;
    }


    /**
     * 装填监听.
     */
    public interface OnSwipeStateChangedListener {
        public void onOpened(Object tag);
        public void onClosed(Object tag);
    }


}
