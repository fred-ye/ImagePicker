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
package cn.fredye.imagepicker.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import cn.fredye.imagepicker.ImagePickerConfiguration;
import cn.fredye.imagepicker.R;
import cn.fredye.imagepicker.entity.Item;
import cn.fredye.imagepicker.ui.widget.LoadingView;
import cn.fredye.imagepicker.util.PhotoMetadataUtils;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class PreviewItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";

    public static PreviewItemFragment newInstance(Item item) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_item, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Item item = getArguments().getParcelable(ARGS_ITEM);
        if (item == null) {
            return;
        }
        ImageViewTouch image = (ImageViewTouch) view.findViewById(R.id.image_view);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        Point size = PhotoMetadataUtils.getBitmapSize(item.getContentUri(), getActivity());
        final LoadingView loadingView = (LoadingView) view.findViewById(R.id.loading_view);
        if(item.isGif()) {

        } else {
            loadingView.loading();
            ImagePickerConfiguration.getInstance().engine.load(getContext(), image, item.getContentUri(), null,size.x, size.y, new RequestListener<Uri, Bitmap>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    loadingView.loadComplete();
                    return false;
                }
            });
        }
        image.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                BasePreviewActivity activity = (BasePreviewActivity)getActivity();
                activity.onImageViewClicked();
            }
        });
    }


    interface ImageViewClickedListener {
        void onImageViewClicked();
    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.image_view)).resetMatrix();
        }
    }
}
