package com.wsj.www.stickinesscircle.stickiness_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.wsj.www.stickinesscircle.utils.GeometryUtil;
import com.wsj.www.stickinesscircle.utils.Utils;

/**
 * 作者 : WSJ
 * 时间 : 2017/5/10
 * 作用 : 使用贝塞尔曲线绘制粘性球.
 */

public class StickinessView extends View {

    private static final String TAG = "StickinessView";

    /**
     * 固定圆的圆心.
     */
    private PointF mStickyCenter = new PointF(300f, 300f);
    private float mStickyRadus = 50f;
    /**
     * 拖拽圆的圆心.
     */
    private PointF mDragCenter = new PointF(400f, 100f);
    private float mDragRadus = 50f;


    private Paint mPaint;
    /**
     * Bessel 曲线路径.
     */
    private Path mBesselPath;
    private PointF[] mStickyBesselPoints;
    private PointF[] mDragBesselPoints;
    private PointF mContralBesselPoint;

    /**
     * 最大距离.
     */
    private float mMaxDistance = 400;

    private boolean mShouldDrawBessel = true;






    
    
    public StickinessView(Context context) {
        this(context, null);
    }

    public StickinessView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickinessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        initPaint();

    }

    /**
     * 初始化画笔.
     */
    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);

        mBesselPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画布 向上移动 状态栏高度.
        canvas.translate(0, -Utils.getStatusBarHeight(getResources()));


        // 绘制两个圆.

        canvas.drawCircle(mDragCenter.x, mDragCenter.y, mDragRadus, mPaint);


//        mPaint.setStrokeWidth(10);
//        canvas.drawPoint(mContralBesselPoint.x, mContralBesselPoint.y, mPaint);
//        canvas.drawPoint(mDragBesselPoints[0].x, mDragBesselPoints[0].y, mPaint);
//        canvas.drawPoint(mDragBesselPoints[1].x, mDragBesselPoints[1].y, mPaint);
//        canvas.drawPoint(mStickyBesselPoints[0].x, mStickyBesselPoints[0].y, mPaint);
//        canvas.drawPoint(mStickyBesselPoints[1].x, mStickyBesselPoints[1].y, mPaint);
        if (mShouldDrawBessel) {
            updateStickyRadus();
            canvas.drawCircle(mStickyCenter.x, mStickyCenter.y, mStickyRadus, mPaint);

            // 绘制贝塞尔曲线, 使用Path
            mBesselPath.reset();
            // 更新bessel曲线 关键点坐标
            updateBesselPoint();
            mBesselPath.moveTo(mDragBesselPoints[0].x, mDragBesselPoints[0].y);
            mBesselPath.quadTo(mContralBesselPoint.x, mContralBesselPoint.y,
                    mStickyBesselPoints[0].x,mStickyBesselPoints[0].y);
            mBesselPath.lineTo(mStickyBesselPoints[1].x, mStickyBesselPoints[1].y);
            mBesselPath.quadTo(mContralBesselPoint.x, mContralBesselPoint.y,
                    mDragBesselPoints[1].x,mDragBesselPoints[1].y);
            mBesselPath.close();// 封闭曲线, 默认就是封闭的.

            canvas.drawPath(mBesselPath, mPaint);
        }


    }

    private void updateStickyRadus() {
        float distance = GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickyCenter);
        float fraction = distance / mMaxDistance;

        mStickyRadus = GeometryUtil.evaluateValue(fraction, 50f, 4f);
    }

    /**
     * 获取Bessel曲线的四个顶点.
     */
    private void updateBesselPoint() {

        // 计算斜率, 注意角度有正负
        float xOffset = mDragCenter.x - mStickyCenter.x;
        float yOffset = mDragCenter.y - mStickyCenter.y;
        double lineK = yOffset / xOffset;

        mStickyBesselPoints = GeometryUtil.getIntersectionPoints(mStickyCenter, mStickyRadus, lineK);
        mDragBesselPoints = GeometryUtil.getIntersectionPoints(mDragCenter, mDragRadus, lineK);

        mContralBesselPoint = GeometryUtil.getMiddlePoint(mDragCenter, mStickyCenter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        float x = event.getRawX();
        float y = event.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDragCenter.set(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mDragCenter.set(x, y);
                // 移动过程中动态计算, 距离
                if (GeometryUtil.getDistanceBetween2Points(mDragCenter, mStickyCenter) > mMaxDistance) {
                    mShouldDrawBessel = false;
                } else {
                    mShouldDrawBessel = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 抬起后, 弹回.
                ValueAnimator valueAnimator = ObjectAnimator.ofFloat(1);
                final PointF startPoint = new PointF(mDragCenter.x, mDragCenter.y);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        // 获取百分比
                        float fraction = valueAnimator.getAnimatedFraction();
                        PointF point = GeometryUtil.getPointByPercent(startPoint, mStickyCenter, fraction);

                        mDragCenter.set(point);

                        invalidate();
                    }
                });
                valueAnimator.setDuration(500);
                valueAnimator.setInterpolator(new OvershootInterpolator(4));
                valueAnimator.start();
                break;
        }
        // 请求重绘
        invalidate();

        return true;
    }
}
