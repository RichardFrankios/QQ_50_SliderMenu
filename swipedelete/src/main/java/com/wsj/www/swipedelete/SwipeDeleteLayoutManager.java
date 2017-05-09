package com.wsj.www.swipedelete;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/9
 * 作用 : 管理SwipeDeleteLayout的单利类.
 */
public class SwipeDeleteLayoutManager {

    private static final String TAG = "SwipeDeleteLayoutManage";
    /**
     * 当前打开的SwipeDeleteLayout
     */
    private SwiperDeleteLayout mSwiperDeleteLayout;



    private static SwipeDeleteLayoutManager ourInstance = new SwipeDeleteLayoutManager();

    public static SwipeDeleteLayoutManager getInstance() {
        return ourInstance;
    }

    private SwipeDeleteLayoutManager() {
    }


    /**
     * 设置当前View
     */
    public void setSwiperDeleteLayout(SwiperDeleteLayout swiperDeleteLayout) {
        mSwiperDeleteLayout = swiperDeleteLayout;
    }

    /**
     * 关闭当前的Swipe
     */
    public void closeSwipe() {
        if (mSwiperDeleteLayout != null)
            mSwiperDeleteLayout.closeSwipe();
    }

    /**
     * 清楚当前的SwipeLayout
     */
    public void clearSwipeLayout() {
        mSwiperDeleteLayout = null;
    }

    /**
     * 1. 如果没有打开的View则可以滑动.<br>
     * 2. 有打开的View : <br>
     *      2.1 是当前View可以滑动.<br>
     *      2.2 不是当前View不可以滑动.<br>
     */
    public boolean shouldSwipe(SwiperDeleteLayout layout) {
        return mSwiperDeleteLayout == null || layout == mSwiperDeleteLayout;
    }






}
