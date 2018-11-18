package cn.fredye.imagepicker;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.fredye.imagepicker.engine.ImageLoaderEngine;

public class ImagePicker {
    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;


    private ImagePicker(Activity activity) {
        this(activity, null);
    }
    private ImagePicker(Fragment fragment) {
        this(fragment.getActivity(), fragment);

    }
    private ImagePicker(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<Activity>(activity);
        mFragment = new WeakReference<Fragment>(fragment);
    }

    @Nullable
    Activity getActivity() {
        return mActivity.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }


    public static ConfigurationCreator from(Activity activity) {
        ImagePicker instance = new ImagePicker(activity);
        ConfigurationCreator creator = new ConfigurationCreator(instance);
        return creator;
    }

    public static ConfigurationCreator from(Fragment fragment) {
        ImagePicker instance = new ImagePicker(fragment);
        ConfigurationCreator creator = new ConfigurationCreator(instance);
        return creator;
    }
}
