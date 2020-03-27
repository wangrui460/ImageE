package com.wangrui.imagee.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.wangrui.imagee.R;

/**
 * created by WR
 * 时间：2020-03-27 14:20
 */
public class CancelSureView extends ConstraintLayout {

    public CancelSureView(Context context) {
        this(context, null);
    }

    public CancelSureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CancelSureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_tool_setting_cancel_sure, this, true);

        ImageView mIvCancel = findViewById(R.id.iv_cancel);
        ImageView mIvSure = findViewById(R.id.iv_sure);

        mIvCancel.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClickCancel();
            }
        });

        mIvSure.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClickSure();
            }
        });
    }

    private OnCancelSureViewListener mListener;

    public void setOnCancelSureViewListener(OnCancelSureViewListener listener) {
        this.mListener = listener;
    }

    public interface OnCancelSureViewListener {
        void onClickCancel();
        void onClickSure();
    }
}
