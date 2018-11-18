package cn.fredye.imagepicker.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import cn.fredye.imagepicker.R;

public class LoadingView extends View {
    public final static int STATE_INIT = 111;//初始
    public final static int STATE_LOADING = 112;//加载中
    public final static int STATE_SUCCESS = 113;//加载成功
    public final static int STATE_FAILURE = 114;//加载失败
    private static final int[] VISIBILITY_FLAGS = {VISIBLE, INVISIBLE, GONE};
    private final static float MIN_ALPHA = 76.5f;
    private final static float MAX_ALPHA = 229.5f;
    private final static int ANIMATION_DURATION = 500;

    private boolean mIsDarkBg;

    private float mBetweenSpace;
    private float mRadius;
    private Paint mNormalPaint;
    private Paint mDarkBgPaint;
    private Paint mErrorTipPaint;

    private ValueAnimator mValueAnimator;
    private int mAlpha1, mAlpha2, mAlpha3;
    private float mDTime1, mDTime2, mDTime3;
    private long mCurrentRepeatCount;
    private List<View> mHideViewList;

    private OnReloadListener onReloadListener;
    private String mErrorTip;
    private Paint.FontMetrics mFontMetrics;

    private int mCurrentState = STATE_INIT;


    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.LoadingView);

        Resources res = context.getResources();

        mErrorTip = attributes.getString(R.styleable.LoadingView_lv_error_tip);
        mIsDarkBg = attributes.getBoolean(R.styleable.LoadingView_isDarkBg, false);
        mBetweenSpace = attributes.getDimension(R.styleable.LoadingView_lv_between_space, dp2px(8));
        mRadius = attributes.getDimension(R.styleable.LoadingView_lv_radius, dp2px(4));
        int normalColor = attributes.getColor(R.styleable.LoadingView_lv_normal_color, res.getColor(R.color.lv_normal_color_default));
        int darkBgColor = attributes.getColor(R.styleable.LoadingView_lv_dark_bg_color, res.getColor(R.color.lv_dark_bg_color_default));
        int errorTipColor = attributes.getColor(R.styleable.LoadingView_lv_error_tip_color, Color.GRAY);
        float errorTipSize = attributes.getDimension(R.styleable.LoadingView_lv_error_tip_size, dp2px(14));

        int defaultVisibility = attributes.getInt(R.styleable.LoadingView_defaultVisibility, 2);
        boolean isDefaultDiffAlpha = attributes.getBoolean(R.styleable.LoadingView_isDefaultDiffAlpha, false);
        attributes.recycle();

        //设置默认是否显示
        defaultVisibility = VISIBILITY_FLAGS[defaultVisibility];
        setVisibility(defaultVisibility);


        mNormalPaint = new Paint();
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setColor(normalColor);

        mDarkBgPaint = new Paint();
        mDarkBgPaint.setAntiAlias(true);
        mDarkBgPaint.setColor(darkBgColor);

        mErrorTipPaint = new Paint();
        mErrorTipPaint.setAntiAlias(true);
        mErrorTipPaint.setColor(errorTipColor);
        mErrorTipPaint.setTextSize(errorTipSize);

        mFontMetrics = mErrorTipPaint.getFontMetrics();

        mDTime1 = ANIMATION_DURATION;
        mDTime2 = ANIMATION_DURATION * 0.5f;
        mDTime3 = 0;

        if (isDefaultDiffAlpha) {
            mAlpha1 = (int) MIN_ALPHA;
            mAlpha2 = (int) (MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) / 2);
            mAlpha3 = (int) MAX_ALPHA;
        } else {
            mAlpha1 = (int) MAX_ALPHA;
            mAlpha2 = (int) MAX_ALPHA;
            mAlpha3 = (int) MAX_ALPHA;
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentState != STATE_FAILURE) {
                    return;
                }

                if (onReloadListener != null) {
                    onReloadListener.onReload();
                }
            }
        });
    }

    private float dp2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    /**
     * 绑定执行动画时需要隐藏的View
     * 会在动画完成后显示
     *
     * @param view view
     */
    public void attachHideView(View view) {
        if (mHideViewList == null) {
            mHideViewList = new ArrayList<>();
        }

        mHideViewList.add(view);
    }

    /**
     * 接触绑定需要隐藏的View
     *
     * @param view view
     */
    public void detachHideView(View view) {
        if (mHideViewList != null) {
            mHideViewList.remove(view);
        }
    }

    /**
     * 解绑所有需要隐藏的View
     */
    public void detachAllHideView() {
        if (mHideViewList != null) {
            mHideViewList.clear();
        }
    }

    /**
     * 设置是否暗色背景
     *
     * @param isDarkBg 是否暗色背景
     */
    public void setDarkBg(boolean isDarkBg) {
        this.mIsDarkBg = isDarkBg;
    }


    public void loadingStart(String loadTxt) {
        loading();
    }

    public void loadingStart(String loadTxt, boolean isDark) {
        loading(isDark);
    }

    /**
     * 启动加载动画
     */
    public void loading() {
        loading(mIsDarkBg);
    }

    /**
     * 启动加载动画，并设置主题
     *
     * @param isDarkBg 背景是否是暗色
     */
    public void loading(boolean isDarkBg) {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }

        if (mHideViewList != null && mHideViewList.size() > 0) {
            for (View view : mHideViewList) {
                view.setVisibility(GONE);
            }
        }
        mCurrentState = STATE_LOADING;
        setDarkBg(isDarkBg);
        cancelAnimation();
        getAnimator().start();
    }


    public void loadingSuccess() {
        loadComplete();
    }

    /**
     * 加载完成
     */
    public void loadComplete() {
        cancelAnimation();

        if (getVisibility() != GONE) {
            setVisibility(GONE);
        }

        if (mHideViewList != null && mHideViewList.size() > 0) {
            for (View view : mHideViewList) {
                view.setVisibility(VISIBLE);
            }
        }
        mCurrentState = STATE_SUCCESS;
    }

    /**
     * 加载错误
     *
     * @param errorTip         错误提示
     * @param onReloadListener 点击事件
     */
    public void loadError(@Nullable String errorTip, OnReloadListener onReloadListener) {
        cancelAnimation();

        this.onReloadListener = onReloadListener;
        this.mErrorTip = errorTip;
        mCurrentState = STATE_FAILURE;
        requestLayout();
        invalidate();
    }

    public void loadingFailure(String loadingFailureTxt) {
        loadError(loadingFailureTxt, null);
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    public int getCurrentState() {
        return mCurrentState;
    }

    /**
     * 判断加载动画是否在运行
     *
     * @return 加载动画是否在运行
     */
    public boolean isLoading() {
        return mValueAnimator != null && mValueAnimator.isRunning();
    }

    private ValueAnimator getAnimator() {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofFloat(MIN_ALPHA, MAX_ALPHA);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float mCurrentTime = animation.getCurrentPlayTime();

                    if (mCurrentTime < mCurrentRepeatCount * ANIMATION_DURATION) {
                        mCurrentTime += mCurrentRepeatCount * ANIMATION_DURATION;
                    }

                    mAlpha1 = getViewAlpha(mCurrentTime + mDTime1);
                    mAlpha2 = getViewAlpha(mCurrentTime + mDTime2);
                    mAlpha3 = getViewAlpha(mCurrentTime + mDTime3);

                    invalidate();
                }
            });
            mValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mCurrentRepeatCount = 0;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    mCurrentRepeatCount++;
                }
            });
            mValueAnimator.setDuration(ANIMATION_DURATION);
            mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
        return mValueAnimator;
    }

    /**
     * 取消动画
     */
    public void cancelAnimation() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        if (mErrorTip != null) {
            height = (int) (mFontMetrics.bottom - mFontMetrics.top);
        } else {
            height = (int) (mRadius * 2);
        }
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCurrentState == LoadingView.STATE_FAILURE) {
            float errorTipWidth = mErrorTipPaint.measureText(mErrorTip);
            canvas.drawText(mErrorTip, (getWidth() - errorTipWidth) / 2, getHeight() - mFontMetrics.descent, mErrorTipPaint);
            return;
        }

        Paint paint = mIsDarkBg ? mDarkBgPaint : mNormalPaint;

        float pointCenter = (getWidth() - 6 * mRadius - 2 * mBetweenSpace) / 2 + mRadius;
        paint.setAlpha(mAlpha1);
        canvas.drawCircle(pointCenter, mRadius, mRadius, paint);

        paint.setAlpha(mAlpha2);
        pointCenter += 2 * mRadius + mBetweenSpace;
        canvas.drawCircle(pointCenter, mRadius, mRadius, paint);

        paint.setAlpha(mAlpha3);
        pointCenter += 2 * mRadius + mBetweenSpace;
        canvas.drawCircle(pointCenter, mRadius, mRadius, paint);
    }

    private int getViewAlpha(float currentTime) {
        float result;
        int temp = (int) (currentTime / ANIMATION_DURATION);
        float temp2 = currentTime % ANIMATION_DURATION / ANIMATION_DURATION;
        if (temp % 2 == 0) {
            result = (MAX_ALPHA - MIN_ALPHA) * temp2 + MIN_ALPHA;
        } else {
            result = (MAX_ALPHA - MIN_ALPHA) * (1 - temp2) + MIN_ALPHA;
        }

        return (int) (result);
    }

    public interface OnReloadListener {
        void onReload();
    }
}