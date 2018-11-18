package cn.fredye.imagepicker.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;

public class GlideEngine implements ImageLoaderEngine {
    @Override
    public void load(Context context, ImageView imageView, Uri url) {
        Glide.with(context).load(url).asBitmap().into(imageView);
    }

    @Override
    public void load(Context context, ImageView imageView, Uri url, Drawable placeHolder) {
        Glide.with(context).load(url).asBitmap().placeholder(placeHolder).centerCrop().into(imageView);

    }

    @Override
    public void load(Context context, ImageView imageView, String url, Drawable placeHolder) {
        Glide.with(context).load(url).asBitmap().placeholder(placeHolder).centerCrop().into(imageView);

    }

    @Override
    public void load(Context context, ImageView imageView, Uri url, Drawable placeHolder, int width, int height) {
        Glide.with(context).load(url).asBitmap().placeholder(placeHolder).override(width, height).centerCrop().into(imageView);
    }

    @Override
    public void load(Context context, ImageView imageView, Uri url, Drawable placeHolder, int width, int height, RequestListener<Uri, Bitmap> listener) {
        Glide.with(context).load(url).asBitmap().listener(listener).placeholder(placeHolder).override(width, height).centerCrop().into(imageView);
    }
}
