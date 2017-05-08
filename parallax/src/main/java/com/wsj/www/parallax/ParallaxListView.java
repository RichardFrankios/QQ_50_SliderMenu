package com.wsj.www.parallax;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/8
 * 作用 : 自定义ListView 实现头部视差效果
 */

public class ParallaxListView extends ListView {


    /**
     * 头部图片
     */
    private ImageView mHeaderView;

    public void setHeadView(ImageView headerView){
        mHeaderView = headerView;
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
     * @param deltaX    继续在X方向滑动的距离
     * @param deltaY    继续在Y方向滑动的距离.
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
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        // 如果使用手指进行再次滑动了.
        if (deltaY != 0 && (isTouchEvent)) {
            
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        // 这个时间不要消耗. 否则可能导致Item无法接受到点击事件.
        return super.onTouchEvent(ev);
    }
}
