package cn.fredye.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.fredye.imagepicker.data.SelectedItemCollection;
import cn.fredye.imagepicker.engine.GlideEngine;
import cn.fredye.imagepicker.engine.ImageLoaderEngine;
import cn.fredye.imagepicker.entity.Item;
import cn.fredye.imagepicker.ui.ImageGridActivity;
import cn.fredye.imagepicker.ui.SelectedImagePreviewActivity;

import static cn.fredye.imagepicker.ui.BasePreviewActivity.EXTRA_ACTION_MODE_VIEW;
import static cn.fredye.imagepicker.ui.BasePreviewActivity.EXTRA_DEFAULT_BUNDLE;

public class ConfigurationCreator {
    private final ImagePicker imagePicker;
    private final ImagePickerConfiguration configuration;

    public ConfigurationCreator(ImagePicker imagePicker) {
        this.imagePicker = imagePicker;
        configuration = ImagePickerConfiguration.getInstance();
        configuration.resetData();
    }

    public ConfigurationCreator maxCount(int maxCount) {
        configuration.maxCount = maxCount;
        return this;
    }
    public ConfigurationCreator rowCount(int rowCount) {
        configuration.rowCount = rowCount;
        return this;
    }
    public ConfigurationCreator imageLoaderEngine(ImageLoaderEngine engine) {
        configuration.engine = engine;
        return this;
    }
    public ConfigurationCreator actionMode(ImagePickerConfiguration.ActionMode actionMode) {
        configuration.actionMode = actionMode;
        return this;
    }
    public ConfigurationCreator countable(boolean countable) {
        configuration.countable = countable;
        return this;
    }
    public ConfigurationCreator imageUrls(List<String> list) {
        configuration.imageUrlsForView = list;
        return this;
    }
    public ConfigurationCreator thumbnailScale(float thumbnailScale) {
        configuration.thumbnailScale = thumbnailScale;
        return this;
    }

    public void start(int requestCode) {
        if (configuration.engine == null) {
            configuration.engine = new GlideEngine();
        }
        //如果是预览
        if (configuration.actionMode == ImagePickerConfiguration.ActionMode.VIEW) {
            List <String> imageUrls = configuration.imageUrlsForView;
            ArrayList<Item> itemArrayList = new ArrayList();
            for (String url : imageUrls) {
                itemArrayList.add(Item.newInStance(Uri.parse(url)));
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(SelectedItemCollection.STATE_SELECTION, itemArrayList);
            Activity activity = imagePicker.getActivity();
            if (activity ==null) {
                return;
            }
            Intent intent = new Intent(activity, SelectedImagePreviewActivity.class);
            intent.putExtra(EXTRA_DEFAULT_BUNDLE, bundle);
            intent.putExtra(EXTRA_ACTION_MODE_VIEW, true);
            activity.startActivity(intent);
        } else {
            startForResult(requestCode);
        }
    }
    private void startForResult(int requestCode) {
        Activity activity = imagePicker.getActivity();
        if (activity == null) {
            return;
        }
        Fragment fragment = imagePicker.getFragment();
        Intent intent = new Intent(activity, ImageGridActivity.class);

        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }
}
