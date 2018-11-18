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
package cn.fredye.imagepicker.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

import cn.fredye.imagepicker.R;
import cn.fredye.imagepicker.entity.Album;
import cn.fredye.imagepicker.entity.Item;

public class AlbumListAdapter extends BaseRecyclerViewCursorAdapter<RecyclerView.ViewHolder> {

    private final Drawable mPlaceholder;
    private Context context;

    private OnAlbumClickListener onAlbumClickListener;

    public AlbumListAdapter(Context context) {
        super(null);
        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        final Album album = Album.valueOf(cursor);
        ListItemHolder itemHolder = (ListItemHolder) holder;
        int size = context.getResources().getDimensionPixelSize(R.dimen.media_grid_size);

        Glide.with(context).load(Uri.fromFile(new File(album.getCoverPath()))).asBitmap()
                .placeholder(mPlaceholder).override(size, size)
                .centerCrop().into(itemHolder.IvAlbum);
        itemHolder.tvAlbumName.setText(album.getDisplayName(context));
        itemHolder.tvCount.setText(String.valueOf(album.getCount()));
        ((ListItemHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAlbumClickListener.onAlbumClick(album);
            }
        });
    }

    @Override
    protected int getItemViewType(int position, Cursor cursor) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.album_list_item, parent, false);
        ListItemHolder holder = new ListItemHolder(view);
        return holder;
    }

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    public void setOnAlbumClickListener(OnAlbumClickListener listener) {
        this.onAlbumClickListener = listener;
    }

    private static class ListItemHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private ImageView IvAlbum;
        private TextView tvAlbumName;
        private TextView tvCount;

        ListItemHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            IvAlbum = (ImageView)itemView.findViewById(R.id.album_cover);
            tvAlbumName = (TextView) itemView.findViewById(R.id.album_name);
            tvCount = (TextView) itemView.findViewById(R.id.album_media_count);
        }
    }

}
