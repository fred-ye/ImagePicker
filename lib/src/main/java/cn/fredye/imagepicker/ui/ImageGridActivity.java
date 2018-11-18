package cn.fredye.imagepicker.ui;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.fredye.imagepicker.ImagePickerConfiguration;
import cn.fredye.imagepicker.R;
import cn.fredye.imagepicker.data.AlbumCollection;
import cn.fredye.imagepicker.data.ImageCollection;
import cn.fredye.imagepicker.data.SelectedItemCollection;
import cn.fredye.imagepicker.entity.Album;
import cn.fredye.imagepicker.entity.Item;
import cn.fredye.imagepicker.ui.adapter.AlbumListAdapter;
import cn.fredye.imagepicker.ui.adapter.AlbumMediaAdapter;
import cn.fredye.imagepicker.ui.widget.MediaGridInset;
import cn.fredye.imagepicker.util.MediaStoreCompat;
import cn.fredye.imagepicker.util.PathUtils;

import static cn.fredye.imagepicker.ui.BasePreviewActivity.EXTRA_DEFAULT_BUNDLE;

/**
 * 图片选择器主界面：
 * 列出相册，用户可以选择相册查看该相册中的图片
 */

public class ImageGridActivity extends BaseActivity implements AlbumCollection.AlbumCallbacks,
        ImageCollection.AlbumMediaCallbacks, AlbumMediaAdapter.CheckStateListener,
        AlbumMediaAdapter.OnMediaClickListener, AlbumMediaAdapter.OnPhotoCapture {
    public static final String EXTRA_RESULT_SELECTION = "extra_result_selection";
    public static final String EXTRA_RESULT_SELECTION_PATH = "extra_result_selection_path";

    private ImageCollection mAlbumMediaCollection = new ImageCollection();
    private AlbumCollection mAlbumCollection = new AlbumCollection();
    private SelectedItemCollection mSelectedItemCollection = new SelectedItemCollection(this);

    private RecyclerView mRecyclerView;
    private RecyclerView albumListView;
    private AlbumMediaAdapter mAdapter;
    private TextView tvImageSelectedCount;
    private AlbumListAdapter albumListAdapter;
    private TextView tvSelectedAlbum;
    private View mask;
    private int albumListCount = 1;

    AlphaAnimation mShowAction = new AlphaAnimation(0, 0.5f);
    AlphaAnimation mHideAction = new AlphaAnimation(0.5f, 0);
    private boolean albumListShowed = false;

    private final int ANIM_DURATION = 200;
    private Album currentAlbum = null;


    private static final int REQUEST_CODE_PREVIEW = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;
    private MediaStoreCompat mMediaStoreCompat;
    private TextView tvPreview;
    private FrameLayout flNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);
        initUI();
        mAlbumCollection.onCreate(ImageGridActivity.this, this);
        mAlbumCollection.loadAlbums();

        mSelectedItemCollection.onCreate(savedInstanceState);
        mAdapter = new AlbumMediaAdapter(this, mSelectedItemCollection, mRecyclerView);

        albumListAdapter = new AlbumListAdapter(this);
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnMediaClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        albumListView.setAdapter(albumListAdapter);
        albumListAdapter.setOnAlbumClickListener(new AlbumListAdapter.OnAlbumClickListener() {
            @Override
            public void onAlbumClick(Album album) {
                onAlbumSelected(album);
                toogleAlbumListDialog();
                currentAlbum = album;
            }
        });

        if (ImagePickerConfiguration.getInstance().withCapture) {
            mMediaStoreCompat = new MediaStoreCompat(this);
            mMediaStoreCompat.setCaptureStrategy(ImagePickerConfiguration.getInstance().captureStrategy);
        }
        setCountNum();
    }

    private void initUI() {
        albumListView = (RecyclerView) findViewById(R.id.rv_album_list);
        albumListView.setLayoutManager(new LinearLayoutManager(ImageGridActivity.this));
        tvSelectedAlbum = (TextView) findViewById(R.id.selected_album);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mask = findViewById(R.id.mask);
        mRecyclerView.setHasFixedSize(true);
        ImagePickerConfiguration configuration = ImagePickerConfiguration.getInstance();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, configuration.rowCount));
        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
        mRecyclerView.addItemDecoration(new MediaGridInset(configuration.rowCount, spacing, false));
        tvSelectedAlbum.setOnClickListener(onClickListener);
        tvImageSelectedCount = (TextView) findViewById(R.id.tv_select_image_count);
        tvPreview = (TextView) findViewById(R.id.tv_preview);
        tvPreview.getBackground().setAlpha(200);
        tvPreview.setOnClickListener(onClickListener);
        flNext = (FrameLayout) findViewById(R.id.fl_next);
        flNext.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.selected_album) {
                toogleAlbumListDialog();
            } else if (v.getId() == R.id.tv_preview) {
                previewSelectedItems();
            } else if (v.getId() == R.id.fl_next) {
                List<Item> items = mSelectedItemCollection.asList();
                ArrayList<String> imageUrlList = new ArrayList<>();
                for (Item item : items) {
                    imageUrlList.add(item.uri.toString());
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, imageUrlList);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    private void previewSelectedItems() {
        if (mSelectedItemCollection.asList().size() < 1) {
            Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(ImageGridActivity.this, SelectedImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SelectedItemCollection.STATE_SELECTION, new ArrayList<>(mSelectedItemCollection.asList()));
        intent.putExtra(EXTRA_DEFAULT_BUNDLE, bundle);
        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
    }

    private void toogleAlbumListDialog() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getAlbumListHeight());
        albumListView.setLayoutParams(layoutParams);
        if (albumListShowed) {
            updateDrawableRightDownArrow();
            hideAlbumListView();
            hideMask();
        } else {
            updateDrawableRightUpArrow();
            showAlbumListView();
            showMask();
        }
        this.albumListShowed = !this.albumListShowed;
    }

    private int getAlbumListHeight() {
        int row = albumListCount > 5 ? 5 : albumListCount;
        return row * getResources().getDimensionPixelSize(R.dimen.album_item_height);
    }


    private void showAlbumListView() {
        int height = getAlbumListHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(albumListView, "translationY", -height, 0);
        animator.setDuration(ANIM_DURATION);
        animator.start();
    }

    private void hideAlbumListView() {
        int height = getAlbumListHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(albumListView, "translationY", 0, -height);
        animator.setDuration(ANIM_DURATION);
        animator.start();
    }

    //向上箭头
    private void updateDrawableRightUpArrow() {
        TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.title_arrowUp});
        int attributeResourceId = a.getResourceId(0, 0);
        Drawable icon = getResources().getDrawable(attributeResourceId);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        tvSelectedAlbum.setCompoundDrawables(null, null, icon, null);
    }

    private void updateDrawableRightDownArrow() {
        TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.title_arrowDown});
        int attributeResourceId = a.getResourceId(0, 0);
        Drawable icon = getResources().getDrawable(attributeResourceId);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        tvSelectedAlbum.setCompoundDrawables(null, null, icon, null);
    }

    private void showMask() {
        mShowAction.setInterpolator(new AccelerateInterpolator());
        mShowAction.setDuration(ANIM_DURATION);
        mask.startAnimation(mShowAction);
        mask.setVisibility(View.VISIBLE);
        mask.setOnClickListener(null);
    }

    private void hideMask() {
        mHideAction.setInterpolator(new AccelerateInterpolator());
        mHideAction.setDuration(ANIM_DURATION);
        mask.startAnimation(mHideAction);
        mask.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onAlbumMediaReset() {
        mAdapter.swapCursor(null);

    }

    @Override
    public void onAlbumLoad(final Cursor cursor) {
        albumListAdapter.swapCursor(cursor);
        albumListCount = cursor.getCount();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
                Album album = Album.valueOf(cursor);
                onAlbumSelected(album);
                currentAlbum = album;
            }
        });
    }

    private void onAlbumSelected(Album album) {
        if (mAlbumMediaCollection != null) {
            mAlbumMediaCollection.onDestroy();
        }
        mAlbumMediaCollection = new ImageCollection();
        tvSelectedAlbum.setText(album.getDisplayName(ImageGridActivity.this));
        mAlbumMediaCollection.onCreate(ImageGridActivity.this, this);
        mAlbumMediaCollection.load(album, ImagePickerConfiguration.getInstance().withCapture);
    }

    @Override
    public void onAlbumReset() {
        albumListAdapter.swapCursor(null);
    }

    @Override
    public void onUpdate() {
        setCountNum();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCountNum();
    }

    @Override
    public void onMediaClick(Album album, Item item, int adapterPosition) {
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra(ImagePreviewActivity.EXTRA_ALBUM, currentAlbum);
        intent.putExtra(ImagePreviewActivity.EXTRA_ITEM, item);
        intent.putExtra(EXTRA_DEFAULT_BUNDLE, mSelectedItemCollection.getDataWithBundle());
        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == REQUEST_CODE_PREVIEW) {
            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
            ArrayList<Item> selected = resultBundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
            int collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE,
                    SelectedItemCollection.COLLECTION_UNDEFINED);
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
                Intent result = new Intent();
                ArrayList<Uri> selectedUris = new ArrayList<>();
                ArrayList<String> selectedPaths = new ArrayList<>();
                if (selected != null) {
                    for (Item item : selected) {
                        selectedUris.add(item.getContentUri());
                        selectedPaths.add(PathUtils.getPath(this, item.getContentUri()));
                    }
                }
                result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
                result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
                setResult(RESULT_OK, result);
                finish();
            } else {
                mSelectedItemCollection.overwrite(selected, collectionType);
                mAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE) {
            MediaScannerConnection.scanFile(this, new String[]{mMediaStoreCompat.getCurrentPhotoPath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            System.out.println("[Android Log]---------------->ExternalStorage " + path + ":");
                            System.out.println("ExternalStorage-> uri=" + uri);
                        }
                    });
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaStoreCompat.getCurrentPhotoUri());
            sendBroadcast(mediaScanIntent);
            File f = new File(mMediaStoreCompat.getCurrentPhotoPath());
            Uri contentUri = Uri.fromFile(f);
            final Uri imageContentUri = getImageContentUri(this, f);
            mSelectedItemCollection.add(Item.newInStance(imageContentUri));

            mAlbumCollection.setStateCurrentSelection(0);
            albumListAdapter.getCursor().moveToPosition(0);
            final Album album = Album.valueOf(albumListAdapter.getCursor());
            onAlbumSelected(album);
        }

    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    public void setCountNum() {
        if (ImagePickerConfiguration.getInstance().countable && mSelectedItemCollection != null && mSelectedItemCollection.count() > 0) {
            tvImageSelectedCount.setText(String.valueOf(mSelectedItemCollection.count()));
            tvImageSelectedCount.setVisibility(View.VISIBLE);
        } else {
            tvImageSelectedCount.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedItemCollection.onSaveInstanceState(outState);
    }

    @Override
    public void capture() {
        if (mMediaStoreCompat != null) {
            mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
        }
    }

    @Override
    public void onBackPressed() {
        if (albumListShowed) {
            toogleAlbumListDialog();
            return;
        }
        super.onBackPressed();

    }
}
