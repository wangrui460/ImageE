package com.wangrui.imagee.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangrui.imagee.R;
import com.wangrui.imagee.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具 adapter
 *
 * created by WR
 * 时间：2020-03-26 15:20
 */
public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ViewHolder> {

    private List<ToolModel> mToolList = new ArrayList<>();

    public ToolAdapter(OnToolSelectedListener onItemSelected) {
        mListener = onItemSelected;
        mToolList.add(new ToolModel(ResUtils.getString(R.string.tool_name_cut), R.drawable.ic_tool_cut, ToolType.CUT));
        mToolList.add(new ToolModel(ResUtils.getString(R.string.tool_name_filter), R.drawable.ic_tool_filter, ToolType.FILTER));
        mToolList.add(new ToolModel(ResUtils.getString(R.string.tool_name_sticker), R.drawable.ic_tool_sticker, ToolType.STICKER));
        mToolList.add(new ToolModel(ResUtils.getString(R.string.tool_name_doodling), R.drawable.ic_tool_doodling, ToolType.DOODLING));
        mToolList.add(new ToolModel(ResUtils.getString(R.string.tool_name_text), R.drawable.ic_tool_text, ToolType.TEXT));
        mToolList.add(new ToolModel(ResUtils.getString(R.string.tool_name_mosaic), R.drawable.ic_tool_cut, ToolType.MOSAIC));
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_tool, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.mTvTool.setText(item.mToolName);
        holder.mIvTool.setImageResource(item.mToolIcon);
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvTool;
        TextView mTvTool;

        ViewHolder(View itemView) {
            super(itemView);
            mIvTool = itemView.findViewById(R.id.iv_tool);
            mTvTool = itemView.findViewById(R.id.tv_tool);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onToolSelected(mToolList.get(getLayoutPosition()).mToolType);
                }
            });
        }
    }

    //<editor-fold desc="listener">
    private OnToolSelectedListener mListener;

    public interface OnToolSelectedListener {
        void onToolSelected(ToolType toolType);
    }
    //</editor-fold>

    //<editor-fold desc="ToolModel">
    class ToolModel {

        private String mToolName;
        private int mToolIcon;
        private ToolType mToolType;

        ToolModel(String toolName, int toolIcon, ToolType toolType) {
            mToolName = toolName;
            mToolIcon = toolIcon;
            mToolType = toolType;
        }
    }
    //</editor-fold>
}
