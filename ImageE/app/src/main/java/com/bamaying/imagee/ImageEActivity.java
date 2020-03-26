package com.bamaying.imagee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bamaying.imagee.tools.ToolAdapter;
import com.bamaying.imagee.tools.ToolType;
import com.bamaying.imagee.utils.LogUtils;
import com.bamaying.imagee.utils.ResUtils;
import com.bamaying.imagee.utils.ToastUtils;

public class ImageEActivity extends AppCompatActivity implements ToolAdapter.OnToolSelectedListener {

    private RecyclerView mRvTools;
    private TextView mTvCancel;
    private TextView mTvSave;

    private ToolAdapter mEditingToolsAdapter = new ToolAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_image_e);
        initViews();
        setupEvents();
    }

    private void initViews() {
        mRvTools = findViewById(R.id.rv_tools);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvSave= findViewById(R.id.tv_sure);

        LinearLayoutManager toolLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(toolLayoutManager);
        mRvTools.setAdapter(mEditingToolsAdapter);
    }

    private void setupEvents() {
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showSystemLongMessage("取消");
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
                ToastUtils.showSystemLongMessage("点击了"+ ResUtils.getString(R.string.tool_name_cut));
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

    // 设置全屏，没有状态栏
    private void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
