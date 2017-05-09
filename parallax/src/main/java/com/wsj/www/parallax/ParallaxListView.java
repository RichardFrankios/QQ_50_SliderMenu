package com.wsj.www.parallax;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/8
 * 作用 : 自定义ListView 实现头部视差效果
 */

public class ParallaxListView extends ListView {

    private static final String TAG = "ParallaxListView";
    /**
     * 头部图片
     */
    private ImageView mHeaderView;

    /**
     * ImageView 最大高度.
     */
    private int mMaxHeight;
    /**
     * ImageView原始高度.
     */
    private int mOriginHeight;


    public void setParallaxImageView(ImageView headerView){
        mHeaderView = headerView;

        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 一定要记得移除, 在这里面获取到的值一定有.
                mHeaderView.getViewTreeObserver().removeOnGlobalLayoutListener(this);


                mOriginHeight = mHeaderView.getHeight();
                Log.d(TAG, "onGlobalLayout: mOriginH = " + mOriginHeight);
                // 获取Drawable资源的高度.
                int drawableHeight = mHeaderView.getDrawable().getIntrinsicHeight();
                Log.d(TAG, "onGlobalLayout: drawableH = " + drawableHeight);
                mMaxHeight = mOriginHeight > drawableHeight ? mOriginHeight * 2 : drawableHeight;
            }
        });
    }



    public ParallaxListView(Context context) {
        super(context);
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 在ListView滚动到头的时候调用, 可获取到继续滑动的距离和方向.
     * @param deltaX    继续在X方向滑动的距离.
     * @param deltaY    继续在Y方向滑动的距离. 上方到顶是负数.下方到头是负数.
     * @param scrollX
     * @param scrollY
     * @param scrollRangeX
     * @param scrollRangeY
     * @param maxOverScrollX  在X方向可以滑动的最大距离.
     * @param maxOverScrollY  在Y方向可以滑动的最大距离.
     * @param isTouchEvent    是否是手指滑动, true : 手指滑动 ; false :　fling(惯性)滑动.
     * @return
     */
    @Override
    protected boolean overScrollBy(int deltaX,
                                   int deltaY,
                                   int scrollX,
                                   int scrollY,
                                   int scrollRangeX,
                                   int scrollRangeY,
                                   int maxOverScrollX,
                                   int maxOverScrollY,
                                   boolean isTouchEvent) {
        // 如果使用手指进行再次滑动了.
        if (deltaY != 0 && (isTouchEvent)) {
            // 将ImageView的高度增加.由于是上方, 因此deltaY 是负数.
            Log.d(TAG, "overScrollBy: deltaY = " + deltaY);
            int newHeight = mHeaderView.getHeight() - deltaY;
            if (newHeight > mMaxHeight) newHeight = mMaxHeight;


    //            Log.d(TAG, "overScrollBy: " + mMaxHeight);

            // 设置布局参数.
            mHeaderView.getLayoutParams().height = newHeight;
            // 请求重绘ImageView即可.
            mHeaderView.requestLayout();
        }
        return super.overScrollBy(deltaX, deltaY,
                scrollX, scrollY,
                scrollRangeX, scrollRangeY,
                maxOverScrollX, maxOverScrollY,
                isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        // 手指抬起后, 需要重新弹回.
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            // 使用 valueAnimator
            ValueAnimator animator = ValueAnimator.ofInt(mHeaderView.getHeight(), mOriginHeight);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    // 更新UI
                    int aValue = (int) valueAnimator.getAnimatedValue();

                    mHeaderView.getLayoutParams().height = aValue;
                    mHeaderView.requestLayout();
                }
            });
            animator.setInterpolator(new OvershootInterpolator(4));
            animator.setDuration(350);
            animator.start();
        }
        // 这个时间不要消耗. 否则可能导致Item无法接受到点击事件.
        return super.onTouchEvent(ev);
    }
}
