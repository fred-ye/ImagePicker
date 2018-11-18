/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.fredye.imagepicker.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.fredye.imagepicker.ImagePickerConfiguration;
import cn.fredye.imagepicker.R;
import cn.fredye.imagepicker.entity.Item;
import com.bumptech.glide.Glide;

public class GridItemView extends SquareFrameLayout implements View.OnClickListener {

    private ImageView mThumbnail;
    private CheckView mCheckView;

    private Item mMedia;
    private PreBindInfo mPreBindInfo;
    private OnMediaGridClickListener mListener;

    public GridItemView(Context context) {
        super(context);
        init(context);
    }

    public GridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_grid_item_content, this, true);

        mThumbnail = (ImageView) findViewById(R.id.media_thumbnail);
        mCheckView = (CheckView) findViewById(R.id.check_view);
        if (ImagePickerConfiguration.getInstance().actionMode == ImagePickerConfiguration.ActionMode.VIEW) {
            mCheckView.setVisibility(View.GONE);
        } else {
            mCheckView.setOnClickListener(this);
        }
        mThumbnail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (v == mThumbnail) {
                mListener.onThumbnailClicked(mThumbnail, mMedia, mPreBindInfo.mViewHolder);
            } else if (v == mCheckView) {
                mListener.onCheckViewClicked(mCheckView, mMedia, mPreBindInfo.mViewHolder);
            }
        }
    }

    public void preBindMedia(PreBindInfo info) {
        mPreBindInfo = info;
    }

    public void bindMedia(Item item) {
        mMedia = item;
        initCheckView();
        setImage();
    }

    public Item getMedia() {
        return mMedia;
    }


    private void initCheckView() {
        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable);
    }

    public void setCheckEnabled(boolean enabled) {
        mCheckView.setEnabled(enabled);
    }

    public void setCheckedNum(int checkedNum) {
        mCheckView.setCheckedNum(checkedNum);
    }

    public void setChecked(boolean checked) {
        mCheckView.setChecked(checked);
    }

    private void setImage() {
        ImagePickerConfiguration.getInstance().engine.load(getContext(), mThumbnail, mMedia.getContentUri(), mPreBindInfo.mPlaceholder, mPreBindInfo.mResize, mPreBindInfo.mResize);
    }

    public void setOnMediaGridClickListener(OnMediaGridClickListener listener) {
        mListener = listener;
    }

    public void removeOnMediaGridClickListener() {
        mListener = null;
    }

    public interface OnMediaGridClickListener {

        void onThumbnailClicked(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder);

        void onCheckViewClicked(CheckView checkView, Item item, RecyclerView.ViewHolder holder);
    }

    public static class PreBindInfo {
        int mResize;
        Drawable mPlaceholder;
        boolean mCheckViewCountable;
        RecyclerView.ViewHolder mViewHolder;

        public PreBindInfo(int resize, Drawable placeholder, boolean checkViewCountable,
                           RecyclerView.ViewHolder viewHolder) {
            mResize = resize;
            mPlaceholder = placeholder;
            mCheckViewCountable = checkViewCountable;
            mViewHolder = viewHolder;
        }
    }

}
