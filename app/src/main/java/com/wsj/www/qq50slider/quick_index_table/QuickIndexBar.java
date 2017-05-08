package com.wsj.www.qq50slider.quick_index_table;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/8
 * 作用 : 自定义快速索引栏.
 * 思路 :
 *      整体思路通过重写onDraw方法进行绘制.
 *      1. 在onSizeChange() 方法中获取尺寸.
 *      2. 计算字母的高度.
 *      3. 拦截事件, 处理选择事件.
 */

public class QuickIndexBar extends View {

    private static final String TAG = "QuickIndexBar";
    /**
     * 26 个英文字母
     */
    private static String[] sLetters = { "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z" };

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * Bar的宽度.
     */
    private int mWidth ;
    /**
     * Bar 的高度.
     */
    private int mHeight;

    /**
     * 文本Cell 的高度.
     */
    private float mTextCellHeight;

    /**
     * 索引变量.
     */
    private int mIndexTemp = -1;

    private OnTouchLetterListener mOnTouchLetterListener;

    public void setOnTouchLetterListener(OnTouchLetterListener onTouchLetterListener) {
        mOnTouchLetterListener = onTouchLetterListener;
    }

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();
    }

    /**
     * 初始化画笔.
     */
    private void initPaint() {
        // 1. 设置抗锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 2. 颜色
        mPaint.setColor(Color.WHITE);
        // 3. 文字大小.
        mPaint.setTextSize(16);
        // 4. 设置文字位置 : 文字底部边框中心.
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * onMeasure 之后会调用这个, 一般在这个方法中进行获取, 控件的尺寸.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mTextCellHeight = mHeight / sLetters.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 循环绘制字母.
        // x : 文本Cell的中心. mWidth/2
        float x = mWidth / 2;
        // y : position * Cell高度 + 0.5 * Cell高度. + 0.5 * 文本高度
        float y = 0;
        for (int i = 0; i < sLetters.length; i++) {
            // 计算文本高度
            float txtHeight = getTextHeight(sLetters[i]);
            // 计算y值
            y = (float) (i * mTextCellHeight + 0.5 * mTextCellHeight + 0.5 * txtHeight);

            // 设置画笔颜色.
            mPaint.setColor(mIndexTemp == i ? Color.BLACK : Color.WHITE);

            // 绘制文字
            canvas.drawText(sLetters[i], x, y, mPaint);
        }
    }

    /**
     * 拦截点击事件, 来实现点击选择.
     * 获取点击字符的思路 :
     *      1. 使用y坐标 除以 Cell高度.也就是 :　index = y / cellHeight;
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int currentIndex = (int) (event.getY()/mTextCellHeight);
                if (currentIndex != mIndexTemp) {
                    mIndexTemp = currentIndex;
                    if (mOnTouchLetterListener != null) {
                        mOnTouchLetterListener.onSelectLetter(sLetters[mIndexTemp]);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mIndexTemp = -1;
                break;
        }
        // 请求重绘
        invalidate();
        return true;
    }

    /**
     * 获取文本高度.
     * @param txt 文本内容.
     * @return 高度.
     */
    private float getTextHeight(String txt) {
        Rect bounds = new Rect();
        mPaint.getTextBounds(txt, 0, txt.length(), bounds);
        return bounds.height();
    }


    /**
     * 字母点击事件
     */
    public interface OnTouchLetterListener {
        public void onSelectLetter(String letter);
    }


}
