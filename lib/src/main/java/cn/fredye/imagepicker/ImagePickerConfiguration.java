package cn.fredye.imagepicker;

import java.util.List;

import cn.fredye.imagepicker.engine.ImageLoaderEngine;
import cn.fredye.imagepicker.entity.CaptureStrategy;


public class ImagePickerConfiguration {
    private final int MAX_COUNT = 9;
    private final int ROW_COUNT = 3;
    private final boolean DEFAULT_COUNTABLE = true;
    private final boolean DEFAULT_WITH_CAPTURE = true;

    public int maxCount = MAX_COUNT ;
    public int rowCount = ROW_COUNT;
    public ImageLoaderEngine engine;
    public ActionMode actionMode = ActionMode.PICK;
    public boolean countable = DEFAULT_COUNTABLE;
    public float thumbnailScale = 0.5f;
    public boolean withCapture = DEFAULT_WITH_CAPTURE;
    public CaptureStrategy captureStrategy = new CaptureStrategy(true, "com.imagepicker.fileprovider");
    public List<String> imageUrlsForView;


    private static ImagePickerConfiguration instance;

    public static ImagePickerConfiguration getInstance() {
        if (instance == null) {
            synchronized (ImagePickerConfiguration.class) {
                if (instance == null) {
                    instance = new ImagePickerConfiguration();
                }
            }
        }
        return instance;
    }
    public void resetData() {
        maxCount = MAX_COUNT;
        rowCount = ROW_COUNT;
        countable = DEFAULT_COUNTABLE;
        actionMode = ActionMode.PICK;
        thumbnailScale = 0.5f;
        withCapture = DEFAULT_WITH_CAPTURE;
        imageUrlsForView = null;

    }

    public enum ActionMode {
        VIEW,
        PICK
    }


}
