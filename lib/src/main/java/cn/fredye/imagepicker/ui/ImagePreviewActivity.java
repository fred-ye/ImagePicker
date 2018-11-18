package cn.fredye.imagepicker.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.fredye.imagepicker.ImagePickerConfiguration;
import cn.fredye.imagepicker.data.ImageCollection;
import cn.fredye.imagepicker.entity.Album;
import cn.fredye.imagepicker.entity.Item;
import cn.fredye.imagepicker.ui.adapter.PreviewPagerAdapter;

/**
 * 点击某个图片的缩略图进入这个Activity
 */

public class ImagePreviewActivity extends BasePreviewActivity implements
        ImageCollection.AlbumMediaCallbacks{
    private ImageCollection mCollection = new ImageCollection();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCollection.onCreate(this, this);
        Album album = getIntent().getParcelableExtra(EXTRA_ALBUM);
        mCollection.load(album);
        Item item = getIntent().getParcelableExtra(EXTRA_ITEM);
        if (ImagePickerConfiguration.getInstance().countable) {
            mCheckView.setCountable(true);
            mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
        } else {
            mCheckView.setChecked(mSelectedCollection.isSelected(item));
        }
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        List<Item> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(Item.valueOf(cursor));
        }
        if (items.isEmpty()) {
            return;
        }

        PreviewPagerAdapter adapter = (PreviewPagerAdapter) viewPager.getAdapter();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
        Item selected = getIntent().getParcelableExtra(EXTRA_ITEM);
        int selectedIndex = items.indexOf(selected);
        viewPager.setCurrentItem(selectedIndex, false);
    }

    @Override
    public void onAlbumMediaReset() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollection.onDestroy();
    }
}
