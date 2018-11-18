package cn.fredye.imagepicker;

import android.database.Cursor;

public interface Callbacks {
    public interface AlbumCollectionCallback {
        void onAlbumLoad(Cursor cursor);
        void onAlbumReset();
    }
    public interface ImageCollectionCallback {
        void onImageLoaded(Cursor cursor);
        void onImageReset();
    }
}
