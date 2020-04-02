package com.wangrui.imagee.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.wangrui.imagee.R;
import com.wangrui.imagee.tools.CancelSureView;

/**
 * 文字
 *
 * created by WR
 * 时间：2020-03-27 15:14
 */
public class ToolTextView extends ConstraintLayout {

    private CancelSureView mCancelSureView;

    public ToolTextView(Context context) {
        this(context, null);
    }

    public ToolTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        setupEvents();
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_tool_text, this, true);

        mCancelSureView = findViewById(R.id.view_cancel_sure);
    }

    private void setupEvents() {
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

    //<editor-fold desc="listener">
    private OnToolTextViewListener mListener;

    public void setOnToolTextViewListener(OnToolTextViewListener listener) {
        mListener = listener;
    }

    public interface OnToolTextViewListener {
        void onClickCancel();
        void onClickSure();
    }
    //</editor-fold>
}
