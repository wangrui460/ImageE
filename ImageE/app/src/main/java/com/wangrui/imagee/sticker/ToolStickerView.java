package com.wangrui.imagee.sticker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangrui.imagee.ImageEActivity;
import com.wangrui.imagee.R;
import com.wangrui.imagee.filter.FilterAdapter;
import com.wangrui.imagee.tools.CancelSureView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 贴纸
 *
 * created by WR
 * 时间：2020-03-27 15:14
 */
public class ToolStickerView extends ConstraintLayout implements StickerTypeAdapter.OnStickerTypeSelectedListener, StickerAdapter.OnStickerSelectedListener {

    private StickerTypeAdapter mStickerTypeAdapter = new StickerTypeAdapter(this);
    private RecyclerView mRvStickers;
    private RecyclerView mRvStickerTypes;
    private CancelSureView mCancelSureView;

    public ToolStickerView(Context context) {
        this(context, null);
    }

    public ToolStickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        setupEvents();
        mStickerTypeAdapter.selecteFirst();
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_tool_sticker, this, true);

        mRvStickers = findViewById(R.id.rv_stickers);
        mRvStickerTypes = findViewById(R.id.rv_types);
        mCancelSureView = findViewById(R.id.view_cancel_sure);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRvStickerTypes.setLayoutManager(layoutManager);
        mRvStickerTypes.setAdapter(mStickerTypeAdapter);
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

    private void setupStickerRecyclerView(List<String> paths) {
        StickerAdapter stickerAdapter = new StickerAdapter(paths, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRvStickers.setLayoutManager(layoutManager);
        mRvStickers.setAdapter(stickerAdapter);
    }

    @Override
    public void onStickerSelected(String path) {
        if (mListener != null) {
            mListener.onStickerSelected(path);
        }
    }

    @Override
    public void onStickerTypeSelected(String typePath) {
        try {
            List<String> paths = new ArrayList<>();
            if (getContext() != null) {
                String[] files = getContext().getAssets().list(typePath);
                if (null != files) {
                    for (String name : files) {
                        paths.add(typePath + File.separator + name);
                    }
                    setupStickerRecyclerView(paths);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //<editor-fold desc="listener">
    private OnToolStickerViewListener mListener;

    public void setOnToolStickerViewListener(OnToolStickerViewListener listener) {
        mListener = listener;
    }

    public interface OnToolStickerViewListener {
        void onClickCancel();
        void onClickSure();
        void onStickerSelected(String stickerPath);
    }
    //</editor-fold>
}
