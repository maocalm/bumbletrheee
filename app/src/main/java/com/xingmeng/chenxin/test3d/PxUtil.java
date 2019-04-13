package com.xingmeng.chenxin.test3d;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;




public class PxUtil {
    public static int dip2px(Context context, float dpValue) {

        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {

        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
    public static int dp2px(Resources context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getDisplayMetrics());
    }
}
