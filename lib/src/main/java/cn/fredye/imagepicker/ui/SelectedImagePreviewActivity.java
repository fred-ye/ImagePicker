package cn.fredye.imagepicker.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.fredye.imagepicker.ImagePicker;
import cn.fredye.imagepicker.ImagePickerConfiguration;
import cn.fredye.imagepicker.R;
import cn.fredye.imagepicker.data.SelectedItemCollection;
import cn.fredye.imagepicker.entity.Item;

/**
 * 从"预览"直接进来
 */
public class SelectedImagePreviewActivity extends BasePreviewActivity {
    private TextView tvTitleContent;
    private int selectedImageCount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE);
        List<Item> selected = bundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
        Log.i("SelectedImage", "selected:" + selected.get(0).getContentUri().toString());
        mAdapter.addAll(selected);
        mAdapter.notifyDataSetChanged();
        selectedImageCount = selected.size();
        tvTitleContent = (TextView)findViewById(R.id.tv_title_content);

        if (ImagePickerConfiguration.getInstance().countable) {
            mCheckView.setCountable(true);
            mCheckView.setCheckedNum(1);
        } else {
            mCheckView.setChecked(true);
            tvTitleContent.setText("1/" + selectedImageCount);
        }
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        if (!ImagePickerConfiguration.getInstance().countable) {
            tvTitleContent.setText("" + (position + 1) + "/" + selectedImageCount);
        }
    }
}
