package cn.fredye.imagepicker.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

import cn.fredye.imagepicker.ImagePickerConfiguration;
import cn.fredye.imagepicker.R;
import cn.fredye.imagepicker.data.SelectedItemCollection;
import cn.fredye.imagepicker.entity.Item;
import cn.fredye.imagepicker.ui.adapter.PreviewPagerAdapter;
import cn.fredye.imagepicker.ui.widget.CheckView;
import cn.fredye.imagepicker.ui.widget.DotIndicator;

public class BasePreviewActivity extends BaseActivity implements  ViewPager.OnPageChangeListener, PreviewItemFragment.ImageViewClickedListener  {
    public static final String EXTRA_DEFAULT_BUNDLE = "extra_default_bundle";
    public static final String EXTRA_ACTION_MODE_VIEW = "extra_action_mode";
    public static final String EXTRA_RESULT_BUNDLE = "extra_result_bundle";
    public static final String EXTRA_ALBUM = "extra_album";
    public static final String EXTRA_ITEM = "extra_item";
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";


    protected ViewPager viewPager;
    protected PreviewPagerAdapter mAdapter;
    private LinearLayout llTitle;
    private ImmersionBar immersionBar;
    private boolean isFullScreen = false;
    private final int ANIM_DURATION  = 200;
    protected CheckView mCheckView;
    private TextView tvImageSelectedCount;
    protected FrameLayout frameLayoutNext;
    private TextView tvBack;
    private DotIndicator indicator;

    protected final SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        if (savedInstanceState == null) {
            mSelectedCollection.onCreate(getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE));
        } else {
            mSelectedCollection.onCreate(savedInstanceState);
        }
        initView();
        initImmersionBar();

        setCountNum();
        if (ImagePickerConfiguration.getInstance().actionMode == ImagePickerConfiguration.ActionMode.VIEW) {
            immersionBar.hideBar(BarHide.FLAG_HIDE_STATUS_BAR);
            immersionBar.init();
            llTitle.setVisibility(View.GONE);
        }
    }



    protected void initView() {
        tvBack = (TextView) findViewById(R.id.btn_back);
        frameLayoutNext = (FrameLayout) findViewById(R.id.fl_next);
        llTitle = (LinearLayout) findViewById(R.id.ll_title);
        mCheckView = (CheckView) findViewById(R.id.check_view);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tvImageSelectedCount = (TextView) findViewById(R.id.tv_select_image_count);
        viewPager.addOnPageChangeListener(this);
        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), null);
        viewPager.setAdapter(mAdapter);
        tvBack.setOnClickListener(onClickListener);
        frameLayoutNext.setOnClickListener(onClickListener);
        if (ImagePickerConfiguration.getInstance().actionMode == ImagePickerConfiguration.ActionMode.VIEW) {
            mCheckView.setVisibility(View.GONE);
        }
        indicator = (DotIndicator) findViewById(R.id.dot_indicator);
        //如果是预览已选择好的图片，则显示底部的indicator
        if (mSelectedCollection.asList().size() != 0) {
            indicator.initParams(0, mSelectedCollection.asList().size());
        }

        mCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Item item = mAdapter.getMediaItem(viewPager.getCurrentItem());
              if (mSelectedCollection.isSelected(item)) {
                  mSelectedCollection.remove(item);
                  if (ImagePickerConfiguration.getInstance().countable) {
                      mCheckView.setCheckedNum(CheckView.UNCHECKED);
                  } else {
                      mCheckView.setChecked(false);
                  }
              } else {
                  if (!mSelectedCollection.maxSelectableReached()) {
                      mSelectedCollection.add(item);
                      if (ImagePickerConfiguration.getInstance().countable) {
                          mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
                      } else {
                          mCheckView.setChecked(true);
                      }
                  } else { //超过最大值
                      Toast.makeText(BasePreviewActivity.this, "最多只能选择" + ImagePickerConfiguration.getInstance().maxCount + "张图片", Toast.LENGTH_SHORT).show();
                  }
              }
              setCountNum();

            }
          });
        resetView();
        addTopBar();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_back) {
                sendBackResult(true);
                finish();
            } else if (v.getId() == R.id.fl_next) {
                sendBackResult(true);
                finish();
            }
        }
    };

    private void addTopBar() {
        View topBar = new View(this);
        topBar.setBackgroundColor(getResources().getColor(android.R.color.black));
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        resourceId = resourceId > 0 ? resourceId : R.dimen.screen_status_bar_height;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(resourceId));
        llTitle.addView(topBar, 0, lp);
    }

    private void resetView() {
        //如果仅仅是preview
        if (ImagePickerConfiguration.getInstance().actionMode == ImagePickerConfiguration.ActionMode.VIEW) {
            frameLayoutNext.setVisibility(View.GONE);
            mCheckView.setVisibility(View.GONE);
        } else {
            frameLayoutNext.setVisibility(View.VISIBLE);
            mCheckView.setVisibility(View.VISIBLE);
        }
    }
    public void setCountNum() {
        if (ImagePickerConfiguration.getInstance().countable && mSelectedCollection != null && mSelectedCollection.count() > 0) {
            tvImageSelectedCount.setText(String.valueOf(mSelectedCollection.count()));
            tvImageSelectedCount.setVisibility(View.VISIBLE);
        } else {
            tvImageSelectedCount.setVisibility(View.GONE);
        }
    }


    public void initImmersionBar() {
        immersionBar = ImmersionBar.with(BasePreviewActivity.this);
        immersionBar.fullScreen(true);
        immersionBar.statusBarColorInt(Color.BLACK);
        immersionBar.statusBarDarkFont(false);
        immersionBar.init();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PreviewPagerAdapter adapter = (PreviewPagerAdapter) viewPager.getAdapter();
        Item item = adapter.getMediaItem(position);
        //如果是预览已选择好的图片，则显示底部的indicator
        if (mSelectedCollection.asList().size() != 0) {
            indicator.setIndex(position);
        }
        if (ImagePickerConfiguration.getInstance().countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            mCheckView.setCheckedNum(checkedNum);
            if (checkedNum > 0) {
                mCheckView.setEnabled(true);
            } else {
                mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
            }
        } else {
            boolean checked = mSelectedCollection.isSelected(item);
            mCheckView.setChecked(checked);
            if (checked) {
                mCheckView.setEnabled(true);
            } else {
                mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mSelectedCollection.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onImageViewClicked() {
        if (ImagePickerConfiguration.getInstance().actionMode== ImagePickerConfiguration.ActionMode.PICK) {
            if (isFullScreen) {
                showTitle();
            } else {
                hideTitle();
            }
            isFullScreen = !isFullScreen;
        } else {
            //外部预览时，关闭界面
            finish();
        }
    }

    private void hideTitle() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(llTitle, "translationY", 0, -getTitleHeight());
        animator.setDuration(ANIM_DURATION);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                immersionBar.hideBar(BarHide.FLAG_HIDE_STATUS_BAR);
                immersionBar.init();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    private void showTitle() {
        immersionBar.reset();
        immersionBar.fullScreen(true);
        immersionBar.statusBarColorInt(Color.BLACK);
        immersionBar.statusBarDarkFont(false);
        immersionBar.init();
        ObjectAnimator animator = ObjectAnimator.ofFloat(llTitle, "translationY", -getTitleHeight(), 0);
        animator.setDuration(ANIM_DURATION);
        animator.start();
    }

    /**
     * 获取title + statusbar高度
     * @return
     */
    private int getTitleHeight() {
        int titleHeight = getResources().getDimensionPixelSize(R.dimen.title_bar_height);
        float statusHeight = getStatusBarHeight();
        statusHeight = statusHeight > 0 ? statusHeight : R.dimen.screen_status_bar_height;
        return titleHeight + (int)statusHeight;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        immersionBar.destroy();
    }

    @Override
    public void onBackPressed() {
        sendBackResult(false);
        super.onBackPressed();
    }

    /**
     * 是否将结果直接返回调用者
     *
     * @param apply true:将结果直接返回给调用者，会关闭当前页面，上一层选择相册（图片）页面
     *              false: 返回到一层页面
     */

    protected void sendBackResult(boolean apply) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_BUNDLE, mSelectedCollection.getDataWithBundle());
        intent.putExtra(EXTRA_RESULT_APPLY, apply);
        setResult(Activity.RESULT_OK, intent);
    }


    public float getStatusBarHeight() {
        float result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimension(resourceId);
        }
        return result;
    }
}
