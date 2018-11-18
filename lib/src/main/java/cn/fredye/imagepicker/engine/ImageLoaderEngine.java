package cn.fredye.imagepicker.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;

import cn.fredye.imagepicker.R;

/**
 * Created by fred on 10/07/2018.
 */

public interface ImageLoaderEngine {
    public void load(Context context, ImageView imageView, Uri url);
    public void load(Context context, ImageView imageView, Uri url, Drawable placeHolder);
    public void load(Context context, ImageView imageView, String url, Drawable placeHolder);
    public void load(Context context, ImageView imageView, Uri url, Drawable placeHolder, int width, int height);
    public void load(Context context, ImageView imageView, Uri url, Drawable placeHolder, int width, int height, RequestListener<Uri, Bitmap> listener);
}
