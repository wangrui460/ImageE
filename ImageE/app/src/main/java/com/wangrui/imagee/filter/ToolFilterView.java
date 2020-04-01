package com.wangrui.imagee.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangrui.imagee.R;
import com.wangrui.imagee.crop.CropAdapter;
import com.wangrui.imagee.crop.CropType;
import com.wangrui.imagee.tools.CancelSureView;

/**
 * 滤镜
 *
 * created by WR
 * 时间：2020-03-27 15:14
 */
public class ToolFilterView extends ConstraintLayout implements FilterAdapter.OnFilterSelectedListener {

    private FilterAdapter mFilterAdapter = new FilterAdapter(this);
    private RecyclerView mRvFilters;
    private CancelSureView mCancelSureView;


    public ToolFilterView(Context context) {
        this(context, null);
    }

    public ToolFilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        setupEvents();
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_tool_filter, this, true);

        mRvFilters = findViewById(R.id.rv_filters);
        mCancelSureView = findViewById(R.id.view_cancel_sure);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(layoutManager);
        mRvFilters.setAdapter(mFilterAdapter);
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

    @Override
    public void onFilterSelected(String key) {
        if (mListener != null) {
            mListener.onFilterSelected(key);
        }
    }

    //<editor-fold desc="listener">
    private OnToolFilterViewListener mListener;

    public void setOnToolFilterViewListener(OnToolFilterViewListener listener) {
        mListener = listener;
    }

    public interface OnToolFilterViewListener {
        void onClickCancel();
        void onClickSure();
        void onFilterSelected(String key);
    }
    //</editor-fold>
}
