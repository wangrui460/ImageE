package com.wangrui.imagee.crop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class CropAdapter extends RecyclerView.Adapter<CropAdapter.ViewHolder> {

    private List<CropModel> mCropList = new ArrayList<>();

    public CropAdapter(OnCropSelectedListener onItemSelected) {
        mListener = onItemSelected;
        mCropList.add(new CropModel(R.drawable.ic_crop_rotate, R.drawable.ic_crop_rotate, CropType.CROP_ROTATE, false));
        mCropList.add(new CropModel(R.drawable.ic_crop_free, R.drawable.ic_crop_free_selected, CropType.CROP_FREE, false));
        mCropList.add(new CropModel(R.drawable.ic_crop_11, R.drawable.ic_crop_11_selected, CropType.CROP_11, false));
        mCropList.add(new CropModel(R.drawable.ic_crop_43, R.drawable.ic_crop_43_selected, CropType.CROP_43, false));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_crop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CropModel item = mCropList.get(position);
        int resId = item.mIsSelected ? item.mCropSelectedIcon : item.mCropIcon;
        holder.mIvCrop.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return mCropList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvCrop;

        ViewHolder(View itemView) {
            super(itemView);
            mIvCrop = itemView.findViewById(R.id.iv_crop);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCropSelected(mCropList.get(getLayoutPosition()).mCropType);
                    }
                }
            });
        }
    }

    //<editor-fold desc="listener">
    private OnCropSelectedListener mListener;

    public interface OnCropSelectedListener {
        void onCropSelected(CropType cropType);
    }
    //</editor-fold>

    //<editor-fold desc="ToolModel">
    class CropModel {

        private int mCropIcon;
        private int mCropSelectedIcon;
        private CropType mCropType;
        private boolean mIsSelected;

        CropModel(int cropIcon, int cropSelectedIcon, CropType cropType, boolean isSelected) {
            mCropIcon = cropIcon;
            mCropSelectedIcon = cropSelectedIcon;
            mCropType = cropType;
            mIsSelected = isSelected;
        }
    }
    //</editor-fold>
}
