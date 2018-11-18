package cn.fredye.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.fredye.imagepicker.ImagePicker;
import cn.fredye.imagepicker.ImagePickerConfiguration;

import static cn.fredye.imagepicker.ui.ImageGridActivity.EXTRA_RESULT_SELECTION_PATH;

public class MainActivity extends AppCompatActivity {
    private TextView tvTest1;
    private TextView tvTest2;
    private TextView tvTest3;
    private TextView tvContent;

    private List<String> imageUrl = new ArrayList<>();
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Log.d(TAG, "---------------onCreate:  savedInstanceState is null");
        } else {
            Log.d(TAG, "---------------onCreate:  savedInstanceState not null");

        }
        mockData();
        initView();
    }

    private void mockData() {
        imageUrl.add("https://goss1.vcg.com/creative/vcg/800/new/VCG211162047588.jpg");
        imageUrl.add("https://goss1.vcg.com/creative/vcg/800/version23/VCG21gic12545934.jpg");
        imageUrl.add("https://goss4.vcg.com/creative/vcg/800/version23/VCG21gic12547015.jpg");
        imageUrl.add("https://goss3.vcg.com/creative/vcg/800/new/VCG211164070555.jpg");
    }

    private void initView() {
        tvTest1 = (TextView) findViewById(R.id.tv_test1);
        tvTest2 = (TextView) findViewById(R.id.tv_test2);
        tvTest3 = (TextView) findViewById(R.id.tv_test3);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvTest1.setOnClickListener(listener);
        tvTest2.setOnClickListener(listener);
        tvTest3.setOnClickListener(listener);
    }
    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_test1:

                    ImagePicker.from(MainActivity.this).rowCount(4).countable(true).start(1);
                    break;
                case R.id.tv_test2:

                    ImagePicker.from(MainActivity.this).rowCount(4).countable(false).start(1);
                    break;
                case R.id.tv_test3:
                    ImagePicker.from(MainActivity.this).actionMode(ImagePickerConfiguration.ActionMode.VIEW).imageUrls(imageUrl).start(1);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArrayList<String> selectedImages = data.getStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH);
            Log.i(TAG, selectedImages.toString());
            tvContent.setText("选择的图片地址："+ selectedImages.toString());

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

