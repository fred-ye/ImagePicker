package cn.fredye.imagepicker.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import cn.fredye.imagepicker.util.UIUtils;
import cn.fredye.imagepicker.util.ViewBgUtil;

public class DotIndicator extends LinearLayout {
    private int dotSize = UIUtils.dip2px(getContext(), 8);
    public DotIndicator(Context context) {
        this(context, null);
    }

    public DotIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);
    }
    public void initParams(int currentIndex, int count) {
        if (count < 2) {
            Log.i("DotIndicator","image count should not less than 2");
            setVisibility(GONE);
        }
        LayoutParams layoutParams = new LayoutParams(dotSize, dotSize);
        for (int i = 0; i < count; i ++) {
            View view = new View(getContext());
            ViewBgUtil.setShapeBg(view, Color.parseColor("#999999"), dotSize);
            if (i != 0) {
                layoutParams.leftMargin = dotSize;
            } else {
                ViewBgUtil.setShapeBg(view, Color.parseColor("#ffffff"), dotSize);
                layoutParams.leftMargin = 0;
            }
            addView(view, layoutParams);
        }
    }
    public void setIndex(int currentIndex) {
        if (getChildAt(currentIndex) == null) {
            return;
        }
        ViewBgUtil.setShapeBg(getChildAt(currentIndex), Color.parseColor("#ffffff"), dotSize);

        int left = currentIndex -1;
        int right = currentIndex + 1;
        if (left >= 0) {
            ViewBgUtil.setShapeBg(getChildAt(left), Color.parseColor("#999999"), dotSize);
        }
        if (right <= getChildCount() - 1) {
            ViewBgUtil.setShapeBg(getChildAt(right), Color.parseColor("#999999"), dotSize);
        }
    }

}
