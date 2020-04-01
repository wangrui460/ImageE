package com.wangrui.imagee.sticker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangrui.imagee.R;

/**
 * 贴纸类型
 *
 * created by WR
 * 时间：2020-04-01 14:13
 */
public class StickerTypeAdapter extends RecyclerView.Adapter<StickerTypeAdapter.ViewHolder> {

    private static final String[] stickerTypePath = {"stickers/dongwu",
            "stickers/xinqing", "stickers/cos", "stickers/fuhao",
            "stickers/shipin", "stickers/chunjie", "stickers/wenzi",
            "stickers/shuzi", "stickers/biankuang", "stickers/zhiye"};
    private static final String[] stickerTypeName = {"动物", "心情", "cos", "符号",
            "饰品", "春节", "文字", "数字", "边框", "职业"};

    public StickerTypeAdapter(OnStickerTypeSelectedListener listener) {
        mListener = listener;
    }

    public void selecteFirst() {
        if (mListener != null) {
            mListener.onStickerTypeSelected(stickerTypePath[0]);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvType;

        ViewHolder(View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tv_type);
        }
    }

    @NonNull
    @Override
    public StickerTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_sticker_type, parent, false);
        return new StickerTypeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerTypeAdapter.ViewHolder holder, int position) {
        String typeName = stickerTypeName[position];
        String typePath = stickerTypePath[position];
        holder.mTvType.setText(typeName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onStickerTypeSelected(typePath);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stickerTypeName.length;
    }

    //<editor-fold desc="listener">
    private OnStickerTypeSelectedListener mListener;

    public interface OnStickerTypeSelectedListener {
        void onStickerTypeSelected(String typePath);
    }
    //</editor-fold>
}
