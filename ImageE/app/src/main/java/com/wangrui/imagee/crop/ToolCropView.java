package com.wangrui.imagee.crop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.SeekBar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangrui.imagee.R;
import com.wangrui.imagee.tools.CancelSureView;
import com.wangrui.imagee.utils.LogUtils;

/**
 * created by WR
 * 时间：2020-03-27 15:14
 */
public class ToolCropView extends ConstraintLayout implements CropAdapter.OnCropSelectedListener {

    private CropAdapter mCropAdapter = new CropAdapter(this);
//    private SeekBar mSeekBar;
    private RecyclerView mRvCrops;
    private CancelSureView mCancelSureView;

    // 0~360
    private int mRotate = 0;

    public ToolCropView(Context context) {
        this(context, null);
    }

    public ToolCropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        setupEvents();
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_tool_crop, this, true);

//        mSeekBar = findViewById(R.id.seekbar);
        mRvCrops = findViewById(R.id.rv_crops);
        mCancelSureView = findViewById(R.id.view_cancel_sure);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRvCrops.setLayoutManager(layoutManager);
        mRvCrops.setAdapter(mCropAdapter);
    }

    private void setupEvents() {
//        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                int rotate = (int)(360f / 100 * progress);
//                if (mListener != null) {
//                    mListener.onClickRotate(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });

        mCancelSureView.setOnCancelSureViewListener(new CancelSureView.OnCancelSureViewListener() {
            @Override
            public void onClickCancel() {
                if (mListener != null) {
                    mListener.onClickCancel();
                }
            }

            @Override
            public void onClickSure() {
                if (mListener != null) {
                    mListener.onClickSure();
                }
            }
        });
    }

    @Override
    public void onCropSelected(CropType cropType) {
        if (mListener == null) {
            return;
        }
        switch (cropType) {
            case CROP_ROTATE:
                mRotate += 90;
                if (mRotate > 360) {
                    mRotate -= 360;
                }
                mListener.onClickRotate(mRotate);
                break;
            case CROP_FREE:
                mListener.onClickCropFree();
                break;
            case CROP_11:
                mListener.onClickCrop11();
                break;
            case CROP_43:
                mListener.onClickCrop43();
                break;
        }
    }

    //<editor-fold desc="listener">
    private OnToolCropViewListener mListener;

    public void setOnToolCropViewListener(OnToolCropViewListener listener) {
        mListener = listener;
    }

    public interface OnToolCropViewListener {
        void onClickCancel();
        void onClickSure();
        void onClickRotate(int rotate);
        void onClickCropFree();
        void onClickCrop11();
        void onClickCrop43();
    }
    //</editor-fold>
}
