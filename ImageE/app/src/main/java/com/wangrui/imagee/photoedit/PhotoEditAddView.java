package com.wangrui.imagee.photoedit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangrui.imagee.R;

/**
 * created by WR
 * 时间：2020-04-08 15:36
 */
public class PhotoEditAddView extends RelativeLayout {

    private FrameLayout mFrmBorder;
    private ImageView mImgClose;

    private TextView mTxtText;
    private TextView mTxtTextEmoji;
    private ImageView mImageView;

    private ViewType mViewType;
    private TextStyleBuilder mTextStyleBuilder;

    public PhotoEditAddView(Context context, ViewType viewType) {
        super(context);
        initView(context, viewType);
    }

    public PhotoEditAddView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoEditAddView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context, ViewType type) {
        mViewType = type;
        if (mViewType == ViewType.TEXT) {
            LayoutInflater.from(context).inflate(R.layout.view_photo_editor_text, this, true);
            mTxtText = findViewById(R.id.tvPhotoEditorText);
            mFrmBorder = findViewById(R.id.frmBorder);
            mImgClose = findViewById(R.id.imgPhotoEditorClose);
        }
        else if (mViewType == ViewType.EMOJI) {
            LayoutInflater.from(context).inflate(R.layout.view_photo_editor_text, this, true);
            mTxtTextEmoji = findViewById(R.id.tvPhotoEditorText);
            mFrmBorder = findViewById(R.id.frmBorder);
            mImgClose = findViewById(R.id.imgPhotoEditorClose);
        }
        else if (mViewType == ViewType.IMAGE) {
            LayoutInflater.from(context).inflate(R.layout.view_photo_editor_image, this, true);
            mImageView = findViewById(R.id.imgPhotoEditorImage);
            mFrmBorder = findViewById(R.id.frmBorder);
            mImgClose = findViewById(R.id.imgPhotoEditorClose);
        }
    }

    public FrameLayout getFrmBorder() {
        return mFrmBorder;
    }

    public ImageView getImgClose() {
        return mImgClose;
    }

    public TextView getTxtText() {
        return mTxtText;
    }

    public TextView getTxtTextEmoji() {
        return mTxtTextEmoji;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public ViewType getViewType() {
        return mViewType;
    }

    public TextStyleBuilder getTextStyleBuilder() {
        return mTextStyleBuilder;
    }

    public void setTextStyleBuilder(TextStyleBuilder mTextStyleBuilder) {
        this.mTextStyleBuilder = mTextStyleBuilder;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
