package com.wangrui.imagee;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;

import java.util.List;

/**
 * created by WR
 * 时间：2020-03-26 18:42
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTvGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupEvents();
    }

    private void initViews() {
        mTvGet = findViewById(R.id.tv_get);
    }

    private void setupEvents() {
        mTvGet.setOnClickListener(v -> getPicturesByGallery());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            // 图片选择结果回调
            List<LocalMedia> localMediaPics = PictureSelector.obtainMultipleResult(data);
            if (localMediaPics != null && localMediaPics.size() == 1) {
                String picPath = localMediaPics.get(0).getPath();
                ImageEActivity.start(this, picPath);
//                ImageEActivity2.start(this, picPath);
            }
        }
    }

    @SuppressLint("CheckResult")
    private void getPicturesByGallery() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        PictureSelector.create(MainActivity.this)
                                .openGallery(PictureMimeType.ofImage())
                                .theme(R.style.picture_default_style)
                                .maxSelectNum(1)
                                .minSelectNum(1)
                                .imageSpanCount(4)
                                .sizeMultiplier(1)
                                .isCamera(false)
                                .compress(true)
                                .cropCompressQuality(50) // 裁剪压缩质量 默认90 int
                                .minimumCompressSize(100) // 小于100kb的图片不压缩
                                .synOrAsy(true) //同步或异步压缩 默认同步true
                                .previewEggs(true) // 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                    }
                });
    }
}
