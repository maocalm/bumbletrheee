package com.xingmeng.chenxin.test3d;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class ParticleView2 extends View {
    private float mSize;
    private Paint mPaint;
    private float roundRadius = 10;// 小球半径
    private float trackWidth;// 轨迹宽度，这里宽度等于view大小减去小球直径
    private float trackHeight;// 轨迹高度，这里高度等于view大小减去小球直径后的3分之1
    private PointF roundPoint;// 小球的位置点
    private int round1Color;
    private int round3Color;
    private int round2Color;
    private boolean needTrack = true;
    private boolean clockwise = true;//是否顺时针旋转
    private int mChildCount = 1;
    private double[] mAngles;//所有view角度的集合
    private Bitmap mBitmap;
    private Thread mThread;

    public ParticleView2(Context context) {
        this(context, null);
    }

    public ParticleView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 消锯齿
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mAngles = new double[mChildCount];
        startAnimator();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = (float) (trackWidth / 2 * Math.cos(mAngles[0] * Math.PI / 180));
        float y = (float) (trackHeight / 2 * Math.sin(mAngles[0] * Math.PI / 180));
        RectF rectF2 = new RectF(mSize / 2 - x - roundRadius, mSize / 2 - y - roundRadius, mSize / 2 - x + roundRadius, mSize / 2 - y + roundRadius);
        roundPoint.set(rectF2.centerX(), rectF2.centerY());
        if (round1Color != 0) {
            mPaint.setColor(round1Color);
        }
        mPaint.setStrokeWidth(3);
        drawTrack(canvas, roundPoint);//画水平椭圆及小球

        if (round2Color != 0) {
            mPaint.setColor(round2Color);
        }
        canvas.rotate(120, mSize / 2, mSize / 2);//旋转一圈是360度，这里有三条退机，所以他们间隔为120度，旋转120度，画椭圆及小球
        drawTrack(canvas, roundPoint);


        if (round3Color != 0) {
            mPaint.setColor(round3Color);
        }
        canvas.rotate(120, mSize / 2, mSize / 2);//旋转120度，画椭圆及小球
        drawTrack(canvas, roundPoint);
        canvas.save();
        canvas.rotate(-240, mSize / 2, mSize / 2);
        int rectfRad = PxUtil.dp2px(getResources(), 20);
        RectF rectF = new RectF(mSize / 2 - rectfRad, mSize / 2 - rectfRad, mSize / 2 + rectfRad, mSize / 2 + rectfRad);
        mPaint.reset();
        canvas.drawBitmap(mBitmap, null, rectF, mPaint);
        canvas.restore();

        mPaint.reset();
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }


    private float animateTime = 5000;//默认的动画持续时间
    private float currentTime = 0;//当前动画执行的时间
    private My3dInterpolate interpolate;//自定义的插值器
    private String TAG = getClass().getSimpleName();

    public boolean startAnimator() {
        mThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (currentTime<animateTime) {
                    if (interpolate != null) {
                        calculateAngle(interpolate.getInterpolation(currentTime / animateTime));
                        Log.e(TAG, "run: calculateAngle" + currentTime / animateTime);
                    } else {
                        calculateAngle(getInterpolate(currentTime / animateTime));
                    }
                    postInvalidate();
                    Log.e(TAG, "run: ");
                    try {
                        currentTime += 5;
                        if (currentTime>=animateTime){
                            currentTime= 0;
                        }
                        sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                currentTime= 0;

            }
        };
        mThread.start();
        return false;
    }

    public interface My3dInterpolate {
        /**
         * 自定义的插值器方法,返回下一帧要增加的角度，因为旋转菜单是根据角度来计算位移的。
         *
         * @param timing 当前动画的时间占总动画时间的百分比
         * @return 角度
         */
        double getInterpolation(float timing);
    }

    /**
     * 画轨迹
     *
     * @param canvas
     * @param p      小球的位置
     */
    private void drawTrack(Canvas canvas, PointF p) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        if (needTrack) {
            // 画轨迹
            RectF oval = new RectF(roundRadius, roundRadius
                    + (mSize - 2 * roundRadius) / 3, mSize - roundRadius,
                    roundRadius + (mSize - 2 * roundRadius) * 2 / 3);
//            RectF oval1 = new RectF(mSize / 2 - trackWidth / 2 - roundRadius, mSize / 2 - trackHeight / 2 - roundRadius, mSize / 2 + trackWidth / 2 + roundRadius, mSize / 2 + trackHeight / 2 + roundRadius);
            canvas.drawOval(oval, mPaint);
        }

        // 画小球
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(p.x, p.y, roundRadius, mPaint);
    }

    /**
     * 设置小球半径
     *
     * @return
     */
    public void setRoundRadius(float roundRadius) {
        this.roundRadius = roundRadius;
    }

    /**
     * 三条轨迹颜色
     *
     * @param round1Color
     * @param round2Color
     * @param round3Color
     */
    public void setRoundColor(int round1Color, int round2Color, int round3Color) {
        this.round1Color = round1Color;
        this.round2Color = round2Color;
        this.round3Color = round3Color;
    }

    /**
     * 是否是要轨迹
     */
    public void setNeedTrack(boolean needTrack) {
        this.needTrack = needTrack;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int desired = (int) (getPaddingLeft() + getWidth() + getPaddingRight());
            width = desired;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            height = heightSize;
        } else {
            int desired = (int) (getPaddingTop() + getWidth() + getPaddingBottom());
            height = desired;
        }
        mSize = width < height ? width : height;// 保证view是正方形
        roundPoint = new PointF(roundRadius, mSize / 2);//小球对应的初始坐标
        trackWidth = (mSize - 2 * roundRadius);//轨迹宽，view的宽度-小球直径
        trackHeight = (mSize - 2 * roundRadius) / 3;//轨迹高，（view的宽度-小球直径）/3
        setMeasuredDimension((int) mSize, (int) mSize);
    }

    /**
     * 自定义的插值器
     */
    public interface MyInterpolate extends TimeInterpolator {
        /**
         * 自定义的插值器方法,返回下一帧要增加的角度，因为旋转菜单是根据角度来计算位移的。
         *
         * @param timing 当前动画的时间占总动画时间的百分比
         * @return 角度
         */
        float getInterpolation(float timing);
    }

    private float getInterpolate(float timing) {
        if (timing < 0.4) {
            return 3;
        } else if (timing < 0.5) {
            return 2.0f;
        } else if (timing < 0.6) {
            return 1.9f;
        } else if (timing < 0.7) {
            return 1.5f;
        } else if (timing < 0.8) {
            return 1.2f;
        } else if (timing < 0.85) {
            return 1;
        } else {
            return 0.8f;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    public void calculateAngle(double j) {
        if (clockwise) {
            for (int i = 0; i < mChildCount; i++) {
                if ((mAngles[i] + j) <= 360) {
                    mAngles[i] += j;
                } else {
                    mAngles[i] += j - 360;
                }
            }
        } else {
            for (int i = 0; i < mChildCount; i++) {
                if ((mAngles[i] + j) >= 0) {
                    mAngles[i] -= j;
                } else {
                    mAngles[i] -= j - 360;
                }
            }
        }
    }

}
