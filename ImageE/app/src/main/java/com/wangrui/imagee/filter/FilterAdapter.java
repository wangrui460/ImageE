package com.wangrui.imagee.filter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangrui.imagee.R;
import com.wangrui.imagee.tools.ToolType;
import com.wangrui.imagee.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具 adapter
 *
 * created by WR
 * 时间：2020-03-26 15:20
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<FilterModel> mFilterList = new ArrayList<>();

    public FilterAdapter(OnFilterSelectedListener onItemSelected) {
        mListener = onItemSelected;
        for (String key : FilterUtils.FILTERS.keySet()) {
            FilterModel model = new FilterModel(R.drawable.ic_filter_1, key);
            mFilterList.add(model);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterModel item = mFilterList.get(position);
        holder.mIvFilter.setImageResource(item.mFilterIcon);
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvFilter;

        ViewHolder(View itemView) {
            super(itemView);
            mIvFilter = itemView.findViewById(R.id.iv_filter);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onFilterSelected(mFilterList.get(getLayoutPosition()).mKey);
                    }
                }
            });
        }
    }

    //<editor-fold desc="listener">
    private OnFilterSelectedListener mListener;

    public interface OnFilterSelectedListener {
        void onFilterSelected(String key);
    }
    //</editor-fold>

    //<editor-fold desc="ToolModel">
    class FilterModel {

        private int mFilterIcon;
        private String mKey;

        FilterModel(int filterIcon, String key) {
            mFilterIcon = filterIcon;
            mKey = key;
        }
    }
    //</editor-fold>
}
