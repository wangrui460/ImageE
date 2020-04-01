package com.wangrui.imagee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.Visibility;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.wangrui.imagee.crop.ToolCropView;
import com.wangrui.imagee.filter.FilterUtils;
import com.wangrui.imagee.filter.ToolFilterView;
import com.wangrui.imagee.imagezoom.ImageViewTouch;
import com.wangrui.imagee.imagezoom.ImageViewTouchBase;
import com.wangrui.imagee.sticker.StickerItem;
import com.wangrui.imagee.sticker.StickerView;
import com.wangrui.imagee.sticker.ToolStickerView;
import com.wangrui.imagee.tools.ToolAdapter;
import com.wangrui.imagee.tools.ToolType;
import com.wangrui.imagee.utils.BitmapUtils;
import com.wangrui.imagee.utils.LogUtils;
import com.wangrui.imagee.utils.Matrix3;
import com.wangrui.imagee.utils.ResUtils;
import com.wangrui.imagee.utils.ToastUtils;

import java.util.List;



public class ImageEActivity extends AppCompatActivity implements ToolAdapter.OnToolSelectedListener {

    private RecyclerView mRvTools;
    private TextView mTvCancel;
    private TextView mTvSave;
    private ToolCropView mToolCropView;
    private ToolFilterView mToolFilterView;
    private ToolStickerView mToolStickerView;
    // 蒙版
    private LinearLayout mLlMaskLeft;
    private LinearLayout mLlMaskTop;
    private LinearLayout mLlMaskRight;
    private LinearLayout mLlMaskBottom;

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

    // 滤镜
    private ImageViewTouch mIvtFilter;
    // 滤镜 Bitmap
    public Bitmap mFilterBitmap;

    // 贴图（贴图显示控件）
    private StickerView mSvSticker;
    // 异步保存贴图
    private SaveStickersTask mSaveStickersTask;

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

        // 滤镜
        mIvtFilter = findViewById(R.id.ivt_filter);
        setupToolFilterView();

        // 贴纸
        mSvSticker = findViewById(R.id.sv_sticker);
        setupToolStickerView();

        // 蒙版
        mLlMaskLeft = findViewById(R.id.ll_mask_left);
        mLlMaskTop = findViewById(R.id.ll_mask_top);
        mLlMaskRight = findViewById(R.id.ll_mask_right);
        mLlMaskBottom = findViewById(R.id.ll_mask_bottom);

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

        mIvtMain.setOnBitmapRectChangeListener(new ImageViewTouchBase.OnBitmapRectChangeListener() {
            @Override
            public void onBitmapRectChanged(RectF rectF) {
                    int width = (int) rectF.width();
                    int height = (int) rectF.height();
                    int left = (int) rectF.left;
                    int top = (int) rectF.top;
                    int right = (int) rectF.right - width;
                    int bottom = mIvtMain.getHeight() - (int) rectF.bottom;
                    LogUtils.e("===================width", width);
                    LogUtils.e("===================height", height);
                    LogUtils.e("===================left", left);
                    LogUtils.e("===================right", right);
                    LogUtils.e("===================top", top);
                    LogUtils.e("===================bottom", bottom);
                    LogUtils.e("===================", "\n\n");
                    updateMask(left, top, right, bottom);
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSvSticker.getLayoutParams();
//                params.width = width;
//                params.height = height;
//                mSvSticker.setLayoutParams(params);
            }
        });
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case CUT:
                showCropImageView(true);
                showToolCropView(true);
                break;
            case FILTER:
                showFilterImageView(true);
                showToolFilterView(true);
                break;
            case STICKER:
                showStickerView(true);
                showToolStickerView(true);
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


    //<editor-fold desc="滤镜">
    private void showFilterImageView(boolean isVisiable) {
        mIvtFilter.setVisibility(isVisiable ? View.VISIBLE : View.GONE);
        mIvtMain.setVisibility(isVisiable ? View.INVISIBLE : View.VISIBLE);
    }

    private void showToolFilterView(boolean isVisiable) {
        mConstraintSet.clone(mRootViewLayout);

        if (isVisiable) {
            mConstraintSet.clear(mToolFilterView.getId(), ConstraintSet.TOP);
            mConstraintSet.connect(mToolFilterView.getId(), ConstraintSet.TOP,
                    mRvTools.getId(), ConstraintSet.TOP);
            mConstraintSet.connect(mToolFilterView.getId(), ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        } else {
            mConstraintSet.clear(mToolFilterView.getId(), ConstraintSet.TOP);
            mConstraintSet.clear(mToolFilterView.getId(), ConstraintSet.BOTTOM);
            mConstraintSet.connect(mToolFilterView.getId(), ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootViewLayout, changeBounds);

        mConstraintSet.applyTo(mRootViewLayout);
    }

    private void setupToolFilterView() {
        mToolFilterView = findViewById(R.id.tool_filter_view);

        mToolFilterView.setOnToolFilterViewListener(new ToolFilterView.OnToolFilterViewListener() {
            @Override
            public void onClickCancel() {
                showFilterImageView(false);
                showToolFilterView(false);
            }

            @Override
            public void onClickSure() {
                updateBitmap(mFilterBitmap);
                showFilterImageView(false);
                showToolFilterView(false);
            }

            @Override
            public void onFilterSelected(String key) {
                Bitmap newBitmap = FilterUtils.filterPhoto(ImageEActivity.this, mMainBitmap, key);
                updateFilterBitmap(newBitmap);
            }
        });
    }
    //</editor-fold>


    //<editor-fold desc="贴纸">
    private void showStickerView(boolean isVisiable) {
        mSvSticker.setVisibility(isVisiable ? View.VISIBLE : View.GONE);
    }

    private void showToolStickerView(boolean isVisiable) {
        mConstraintSet.clone(mRootViewLayout);

        if (isVisiable) {
            mConstraintSet.clear(mToolStickerView.getId(), ConstraintSet.TOP);
            mConstraintSet.connect(mToolStickerView.getId(), ConstraintSet.TOP,
                    mRvTools.getId(), ConstraintSet.TOP);
            mConstraintSet.connect(mToolStickerView.getId(), ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        } else {
            mConstraintSet.clear(mToolStickerView.getId(), ConstraintSet.TOP);
            mConstraintSet.clear(mToolStickerView.getId(), ConstraintSet.BOTTOM);
            mConstraintSet.connect(mToolStickerView.getId(), ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootViewLayout, changeBounds);

        mConstraintSet.applyTo(mRootViewLayout);
    }

    private void setupToolStickerView() {
        mToolStickerView = findViewById(R.id.tool_sticker_view);

        mToolStickerView.setOnToolStickerViewListener(new ToolStickerView.OnToolStickerViewListener() {
            @Override
            public void onClickCancel() {
                showStickerView(false);
                showToolStickerView(false);
            }

            @Override
            public void onClickSure() {
                mSaveStickersTask = new SaveStickersTask();
                mSaveStickersTask.execute(mMainBitmap);
                showStickerView(false);
                showToolStickerView(false);
            }

            @Override
            public void onStickerSelected(String stickerPath) {
                Bitmap sticker = BitmapUtils.getStickerFromAssetsFile(stickerPath);
                if (sticker != null) {
                    mSvSticker.addBitImage(sticker);
                }
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

        mIvtFilter.setImageBitmap(mMainBitmap);
        mIvtFilter.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        mCropImageView.setImageBitmap(mMainBitmap);
    }

    private void updateFilterBitmap(Bitmap bitmap) {
        if (mFilterBitmap != null) {
            if (!mFilterBitmap.isRecycled()) {// 回收
                mFilterBitmap.recycle();
            }
            mFilterBitmap = bitmap;
        } else {
            mFilterBitmap = bitmap;
        }

        mIvtFilter.setImageBitmap(mFilterBitmap);
        mIvtFilter.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
    }

    private void updateMask(int left, int top, int right, int bottom) {
//        RelativeLayout.LayoutParams paramsLeft = (RelativeLayout.LayoutParams) mLlMaskLeft.getLayoutParams();
//        paramsLeft.width = left;
//        mLlMaskLeft.setLayoutParams(paramsLeft);
//
//        RelativeLayout.LayoutParams paramsTop = (RelativeLayout.LayoutParams) mLlMaskTop.getLayoutParams();
//        paramsTop.height = top;
//        mLlMaskTop.setLayoutParams(paramsTop);
//
//        RelativeLayout.LayoutParams paramsRight = (RelativeLayout.LayoutParams) mLlMaskRight.getLayoutParams();
//        paramsRight.width = right;
//        mLlMaskRight.setLayoutParams(paramsRight);
//
//        RelativeLayout.LayoutParams paramsBottom = (RelativeLayout.LayoutParams) mLlMaskBottom.getLayoutParams();
//        paramsBottom.height = bottom;
//        mLlMaskBottom.setLayoutParams(paramsBottom);
    }

    //<editor-fold desc="任务">
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

    // 保存贴图任务
    private final class SaveStickersTask extends AsyncTask<Bitmap, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap resultBit = Bitmap.createBitmap(params[0]).copy(
                    Bitmap.Config.RGB_565, true);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Matrix touchMatrix = mIvtMain.getImageViewMatrix();
                    Canvas canvas = new Canvas(resultBit);

                    float[] data = new float[9];
                    // 底部图片变化记录矩阵原始数据
                    touchMatrix.getValues(data);
                    // 辅助矩阵计算类
                    Matrix3 cal = new Matrix3(data);
                    // 计算逆矩阵
                    Matrix3 inverseMatrix = cal.inverseMatrix();
                    Matrix m = new Matrix();
                    m.setValues(inverseMatrix.getValues());

                    List<StickerItem> addItems = mSvSticker.getBanks();
                    for (StickerItem item : addItems) {
                        // 乘以底部图片变化矩阵
                        item.matrix.postConcat(m);
                        canvas.drawBitmap(item.bitmap, item.matrix, null);
                    }
                }
            });
            return resultBit;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            mSvSticker.clear();
            updateBitmap(result);
        }
    }
    //</editor-fold>

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        if (mSaveStickersTask != null) {
            mSaveStickersTask.cancel(true);
        }
    }
}
