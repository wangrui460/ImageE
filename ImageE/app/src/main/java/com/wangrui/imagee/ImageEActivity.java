package com.wangrui.imagee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.wangrui.imagee.crop.ToolCropView;
import com.wangrui.imagee.imagezoom.ImageViewTouch;
import com.wangrui.imagee.imagezoom.ImageViewTouchBase;
import com.wangrui.imagee.tools.ToolAdapter;
import com.wangrui.imagee.tools.ToolType;
import com.wangrui.imagee.utils.BitmapUtils;
import com.wangrui.imagee.utils.ResUtils;
import com.wangrui.imagee.utils.ToastUtils;


public class ImageEActivity extends AppCompatActivity implements ToolAdapter.OnToolSelectedListener {

    private RecyclerView mRvTools;
    private TextView mTvCancel;
    private TextView mTvSave;
    private ToolCropView mToolCropView;

    // 约束
    private ConstraintLayout mRootViewLayout;
    private ConstraintSet mConstraintSet = new ConstraintSet();


    private ToolAdapter mToolsAdapter = new ToolAdapter(this);

    // 主图
    private ImageViewTouch mIvtMain;
    // 主图宽高
    private int mImageWidth, mImageHeight;
    // 主图显示 Bitmap
    public Bitmap mMainBitmap;
    // 异步载入图片
    private LoadImageTask mLoadImageTask;

    // 剪裁
    private CropImageView mCropImageView;

    private String mImagePath;

    public static void start(Context context, String imagePath) {
        Intent intent = new Intent(context, ImageEActivity.class);
        intent.putExtra("imagePath", imagePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_image_e);
        initViews();
        setupEvents();
        loadImage(mImagePath);
    }

    private void initViews() {
        mImagePath = getIntent().getStringExtra("imagePath");

        mRootViewLayout = findViewById(R.id.rootView);
        mIvtMain = findViewById(R.id.ivt_main);
        mRvTools = findViewById(R.id.rv_tools);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvSave= findViewById(R.id.tv_sure);

        // 主图
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mImageWidth = (int) ((float) metrics.widthPixels / 1.5);
        mImageHeight = (int) ((float) metrics.heightPixels / 1.5);
        mIvtMain.setScaleEnabled(false);
        mIvtMain.setDoubleTapEnabled(false);
        mIvtMain.setScrollEnabled(false);

        // 剪裁（旋转、1:1，4:3，16:9，free...）
        mCropImageView = findViewById(R.id.crop_view);
        mCropImageView.setGuidelines(CropImageView.Guidelines.ON);
        setupToolCropView();


        // 工具
        LinearLayoutManager toolLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(toolLayoutManager);
        mRvTools.setAdapter(mToolsAdapter);


    }

    private void setupEvents() {
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showSystemLongMessage("保存");
            }
        });
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case CUT:
                mCropImageView.setVisibility(View.VISIBLE);
                showCropImageView(true);
                showToolCropView(true);
                break;
            case FILTER:
                ToastUtils.showSystemLongMessage("点击了"+ ResUtils.getString(R.string.tool_name_filter));
                break;
            case STICKER:
                ToastUtils.showSystemLongMessage("点击了"+ ResUtils.getString(R.string.tool_name_sticker));
                break;
            case DOODLING:
                ToastUtils.showSystemLongMessage("点击了"+ ResUtils.getString(R.string.tool_name_doodling));
                break;
            case TEXT:
                ToastUtils.showSystemLongMessage("点击了"+ ResUtils.getString(R.string.tool_name_text));
                break;
            case MOSAIC:
                ToastUtils.showSystemLongMessage("点击了"+ ResUtils.getString(R.string.tool_name_mosaic));
                break;
        }
    }

    //<editor-fold desc="剪裁旋转">
    private void showCropImageView(boolean isVisiable) {
        mCropImageView.setVisibility(isVisiable ? View.VISIBLE : View.GONE);
        mIvtMain.setVisibility(isVisiable ? View.INVISIBLE : View.VISIBLE);
    }

    private void showToolCropView(boolean isVisiable) {
        mConstraintSet.clone(mRootViewLayout);

        if (isVisiable) {
            mConstraintSet.clear(mToolCropView.getId(), ConstraintSet.TOP);
            mConstraintSet.connect(mToolCropView.getId(), ConstraintSet.TOP,
                    mRvTools.getId(), ConstraintSet.TOP);
            mConstraintSet.connect(mToolCropView.getId(), ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        } else {
            mConstraintSet.clear(mToolCropView.getId(), ConstraintSet.TOP);
            mConstraintSet.clear(mToolCropView.getId(), ConstraintSet.BOTTOM);
            mConstraintSet.connect(mToolCropView.getId(), ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootViewLayout, changeBounds);

        mConstraintSet.applyTo(mRootViewLayout);
    }

    private void setupToolCropView() {
        mToolCropView = findViewById(R.id.tool_crop_view);
        mToolCropView.setOnToolCropViewListener(new ToolCropView.OnToolCropViewListener() {
            @Override
            public void onClickCancel() {
                showCropImageView(false);
                showToolCropView(false);
            }

            @Override
            public void onClickSure() {
                showCropImageView(false);
                showToolCropView(false);
                Bitmap cropped = mCropImageView.getCroppedImage();
                updateBitmap(cropped);
            }

            @Override
            public void onClickRotate(int rotate) {
                mCropImageView.setRotatedDegrees(rotate);
            }

            @Override
            public void onClickCropFree() {
                mCropImageView.setFixedAspectRatio(false);
            }

            @Override
            public void onClickCrop11() {
                mCropImageView.setAspectRatio(1, 1);
            }

            @Override
            public void onClickCrop43() {
                mCropImageView.setAspectRatio(4, 3);
            }
        });
    }
    //</editor-fold>

    // 设置全屏，没有状态栏
    private void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // 异步载入编辑图片
    private void loadImage(String filepath) {
        if (TextUtils.isEmpty(filepath)) {
            return;
        }
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    private void updateBitmap(Bitmap bitmap) {
        if (mMainBitmap != null) {
            if (!mMainBitmap.isRecycled()) {// 回收
                mMainBitmap.recycle();
            }
            mMainBitmap = bitmap;
        } else {
            mMainBitmap = bitmap;
        }

        mIvtMain.setImageBitmap(mMainBitmap);
        mIvtMain.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        mCropImageView.setImageBitmap(mMainBitmap);
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.loadImageByPath(params[0], mImageWidth, mImageHeight);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mMainBitmap != null) {
                mMainBitmap.recycle();
                mMainBitmap = null;
                System.gc();
            }
            updateBitmap(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
    }
}
