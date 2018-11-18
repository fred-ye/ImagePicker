package cn.fredye.imagepicker.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.fredye.imagepicker.R;

public class BaseActivity extends AppCompatActivity {

//    protected int themeId = R.style.CommonTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(themeId);
    }
}
