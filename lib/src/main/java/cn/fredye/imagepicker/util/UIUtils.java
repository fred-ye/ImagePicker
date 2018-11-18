package cn.fredye.imagepicker.util;

import android.content.Context;

/**
 * Created by fred on 13/07/2018.
 */

public class UIUtils {

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
