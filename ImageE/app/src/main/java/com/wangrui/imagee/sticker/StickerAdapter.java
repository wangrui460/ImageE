package com.wangrui.imagee.sticker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangrui.imagee.R;
import com.wangrui.imagee.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具 adapter
 *
 * created by WR
 * 时间：2020-03-26 15:20
 */
public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    private List<String> mPaths = new ArrayList<>();

    public StickerAdapter(List<String> paths, OnStickerSelectedListener onItemSelected) {
        mPaths = paths;
        mListener = onItemSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_sticker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = mPaths.get(position);

        holder.mIvSticker.setImageBitmap(BitmapUtils.getStickerFromAssetsFile(path));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onStickerSelected(path);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvSticker;

        ViewHolder(View itemView) {
            super(itemView);
            mIvSticker = itemView.findViewById(R.id.iv_sticker);
        }
    }

    //<editor-fold desc="listener">
    private OnStickerSelectedListener mListener;

    public interface OnStickerSelectedListener {
        void onStickerSelected(String path);
    }
    //</editor-fold>
}
