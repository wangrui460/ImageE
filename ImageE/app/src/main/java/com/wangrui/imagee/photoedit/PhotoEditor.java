package com.wangrui.imagee.photoedit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.wangrui.imagee.R;
import com.wangrui.imagee.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;


public class PhotoEditor {

    private Context mContext;
    private RelativeLayout mParentView;
    private ImageView mImageView;
    private View mDeleteView;
//    private BrushDrawingView mBrushDrawingView;
    private List<PhotoEditAddView> mAddedViews;
    private List<PhotoEditAddView> mRedoViews;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private boolean mIsTextPinchZoomable;
    private Typeface mDefaultTextTypeface;
    private Typeface mDefaultEmojiTypeface;


    private PhotoEditor(PhotoEditBuilder builder) {
        mContext = builder.mContext;
        mParentView = builder.mParentView;
        mImageView = builder.mImageView;
        mDeleteView = builder.mDeleteView;
//        mBrushDrawingView = builder.mBrushDrawingView;
        mIsTextPinchZoomable = builder.mIsTextPinchZoomable;
        mDefaultTextTypeface = builder.mTextTypeface;
        mDefaultEmojiTypeface = builder.mEmojiTypeface;
//        mBrushDrawingView.setBrushViewChangeListener(this);
        mAddedViews = new ArrayList<>();
        mRedoViews = new ArrayList<>();
    }


    public PhotoEditAddView addImage(Bitmap desiredImage) {
        final PhotoEditAddView imageRootView = getLayout(ViewType.IMAGE);
        final ImageView imageView = imageRootView.getImageView();
        final FrameLayout frmBorder = imageRootView.getFrmBorder();
        final ImageView imgClose = imageRootView.getImgClose();

        imageView.setImageBitmap(desiredImage);

        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {

            }
        });

        imageRootView.setOnTouchListener(multiTouchListener);

        addViewToParent(imageRootView, ViewType.IMAGE);
        return imageRootView;
    }

    public PhotoEditAddView addText(String text) {
        // 默认白色
        TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(Color.WHITE);
        return addText(text, styleBuilder);
    }

    public PhotoEditAddView addText(String text, final int colorCodeTextView) {
        return addText(null, text, colorCodeTextView);
    }

    public PhotoEditAddView addText(@Nullable Typeface textTypeface, String text, final int colorCodeTextView) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();

        styleBuilder.withTextColor(colorCodeTextView);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        return addText(text, styleBuilder);
    }

    public PhotoEditAddView addText(String text, @Nullable TextStyleBuilder styleBuilder) {
//        mBrushDrawingView.setBrushDrawingMode(false);
        final PhotoEditAddView textRootView = getLayout(ViewType.TEXT);
        final TextView textInputTv = textRootView.getTxtText();
        final ImageView imgClose = textRootView.getImgClose();
        final FrameLayout frmBorder = textRootView.getFrmBorder();

        textInputTv.setText(text);
        if (styleBuilder != null) {
            styleBuilder.applyStyle(textInputTv);
            textRootView.setTextStyleBuilder(styleBuilder);
        }

        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInput, currentTextColor);
                }
            }
        });

        textRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(textRootView, ViewType.TEXT);
        return textRootView;
    }

    public void editText(@NonNull PhotoEditAddView addView, String inputText) {
        editText(addView, inputText, addView.getTextStyleBuilder());
    }

    public void editText(@NonNull PhotoEditAddView view, String inputText, @NonNull int colorCode) {
        editText(view, null, inputText, colorCode);
    }

    public void editText(@NonNull PhotoEditAddView view, @Nullable Typeface textTypeface, String inputText, @NonNull int colorCode) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(colorCode);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        editText(view, inputText, styleBuilder);
    }

    public void editText(@NonNull PhotoEditAddView view, String inputText, @Nullable TextStyleBuilder styleBuilder) {
        TextView inputTextView = view.findViewById(R.id.tvPhotoEditorText);
        if (inputTextView != null && mAddedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            if (styleBuilder != null)
                styleBuilder.applyStyle(inputTextView);

            mParentView.updateViewLayout(view, view.getLayoutParams());
            int i = mAddedViews.indexOf(view);
            if (i > -1) mAddedViews.set(i, view);
        }
    }

    public PhotoEditAddView addEmoji(String emojiName) {
        return addEmoji(null, emojiName);
    }

    public PhotoEditAddView addEmoji(Typeface emojiTypeface, String emojiName) {
//        mBrushDrawingView.setBrushDrawingMode(false);
        final PhotoEditAddView emojiRootView = getLayout(ViewType.EMOJI);
        final TextView emojiTextView = emojiRootView.getTxtTextEmoji();//emojiRootView.findViewById(R.id.tvPhotoEditorText);
        final FrameLayout frmBorder = emojiRootView.getFrmBorder();//emojiRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = emojiRootView.getImgClose();//emojiRootView.findViewById(R.id.imgPhotoEditorClose);

        if (emojiTypeface != null) {
            emojiTextView.setTypeface(emojiTypeface);
        }
        emojiTextView.setTextSize(56);
        emojiTextView.setText(emojiName);
        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {
            }
        });
        emojiRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(emojiRootView, ViewType.EMOJI);
        return emojiRootView;
    }

    private void addViewToParent(PhotoEditAddView rootView, ViewType viewType) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mParentView.addView(rootView, params);
        mAddedViews.add(rootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(viewType, mAddedViews.size());
    }

    @NonNull
    private MultiTouchListener getMultiTouchListener() {
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                mDeleteView,
                mParentView,
                this.mImageView,
                mIsTextPinchZoomable,
                mOnPhotoEditorListener);

        //multiTouchListener.setOnMultiTouchListener(this);

        return multiTouchListener;
    }

    private PhotoEditAddView getLayout(final ViewType viewType) {
        PhotoEditAddView rootView = null;
        switch (viewType) {
            case TEXT:
                rootView = new PhotoEditAddView(mContext, ViewType.TEXT);
                TextView txtText = rootView.getTxtText();
                if (txtText != null && mDefaultTextTypeface != null) {
                    txtText.setGravity(Gravity.CENTER);
                    if (mDefaultEmojiTypeface != null) {
                        txtText.setTypeface(mDefaultTextTypeface);
                    }
                }
                break;
            case IMAGE:
                rootView = new PhotoEditAddView(mContext, ViewType.IMAGE);
                break;
            case EMOJI:
                rootView = new PhotoEditAddView(mContext, ViewType.EMOJI);
                TextView txtTextEmoji = rootView.getTxtTextEmoji();
                if (txtTextEmoji != null) {
                    if (mDefaultEmojiTypeface != null) {
                        txtTextEmoji.setTypeface(mDefaultEmojiTypeface);
                    }
                    txtTextEmoji.setGravity(Gravity.CENTER);
                    txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                break;
        }

        if (rootView != null) {
            // We are setting tag as ViewType to identify what type of the view it is
            // when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
            rootView.setTag(viewType);
            final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
            final PhotoEditAddView finalRootView = rootView;
            if (imgClose != null) {
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUndo(finalRootView, viewType);
                    }
                });
            }
        }
        return rootView;
    }

//    public void setBrushDrawingMode(boolean brushDrawingMode) {
//        if (mBrushDrawingView != null)
//            mBrushDrawingView.setBrushDrawingMode(brushDrawingMode);
//    }
//
//    public Boolean getBrushDrawableMode() {
//        return mBrushDrawingView != null && mBrushDrawingView.getBrushDrawingMode();
//    }
//
//    public void setBrushSize(float size) {
//        if (mBrushDrawingView != null)
//            mBrushDrawingView.setBrushSize(size);
//    }
//
//    public void setOpacity(@IntRange(from = 0, to = 100) int opacity) {
//        if (mBrushDrawingView != null) {
//            opacity = (int) ((opacity / 100.0) * 255.0);
//            mBrushDrawingView.setOpacity(opacity);
//        }
//    }
//
//    public void setBrushColor(@ColorInt int color) {
//        if (mBrushDrawingView != null)
//            mBrushDrawingView.setBrushColor(color);
//    }
//
//    public void setBrushEraserSize(float brushEraserSize) {
//        if (mBrushDrawingView != null)
//            mBrushDrawingView.setBrushEraserSize(brushEraserSize);
//    }
//
//    void setBrushEraserColor(@ColorInt int color) {
//        if (mBrushDrawingView != null)
//            mBrushDrawingView.setBrushEraserColor(color);
//    }
//
//    public float getEraserSize() {
//        return mBrushDrawingView != null ? mBrushDrawingView.getEraserSize() : 0;
//    }
//
//    public float getBrushSize() {
//        if (mBrushDrawingView != null)
//            return mBrushDrawingView.getBrushSize();
//        return 0;
//    }
//
//    public int getBrushColor() {
//        if (mBrushDrawingView != null)
//            return mBrushDrawingView.getBrushColor();
//        return 0;
//    }
//
//    public void brushEraser() {
//        if (mBrushDrawingView != null)
//            mBrushDrawingView.brushEraser();
//    }

    private void viewUndo(PhotoEditAddView removedView, ViewType viewType) {
        if (mAddedViews.size() > 0) {
            if (mAddedViews.contains(removedView)) {
                mParentView.removeView(removedView);
                mAddedViews.remove(removedView);
                mRedoViews.add(removedView);
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onRemoveViewListener(viewType, mAddedViews.size());
                }
            }
        }
    }

    public boolean undo() {
        if (mAddedViews.size() > 0) {
            PhotoEditAddView removeView = mAddedViews.get(mAddedViews.size() - 1);
//            if (removeView instanceof BrushDrawingView) {
//                return mBrushDrawingView != null && mBrushDrawingView.undo();
//            } else {
                mAddedViews.remove(mAddedViews.size() - 1);
                mParentView.removeView(removeView);
                mRedoViews.add(removeView);
//            }
            if (mOnPhotoEditorListener != null) {
                Object viewTag = removeView.getTag();
                if (viewTag != null && viewTag instanceof ViewType) {
                    mOnPhotoEditorListener.onRemoveViewListener(((ViewType) viewTag), mAddedViews.size());
                }
            }
        }
        return mAddedViews.size() != 0;
    }

    public boolean redo() {
        if (mRedoViews.size() > 0) {
            PhotoEditAddView redoView = mRedoViews.get(mRedoViews.size() - 1);
//            if (redoView instanceof BrushDrawingView) {
//                return mBrushDrawingView != null && mBrushDrawingView.redo();
//            } else {
                mRedoViews.remove(mRedoViews.size() - 1);
                mParentView.addView(redoView);
                mAddedViews.add(redoView);
//            }
            Object viewTag = redoView.getTag();
            if (mOnPhotoEditorListener != null && viewTag != null && viewTag instanceof ViewType) {
                mOnPhotoEditorListener.onAddViewListener(((ViewType) viewTag), mAddedViews.size());
            }
        }
        return mRedoViews.size() != 0;
    }

//    private void clearBrushAllViews() {
//        if (mBrushDrawingView != null)
//            mBrushDrawingView.clearAll();
//    }

    public void clearAllViews() {
        for (int i = 0; i < mAddedViews.size(); i++) {
            mParentView.removeView(mAddedViews.get(i));
        }
//        if (mAddedViews.contains(mBrushDrawingView)) {
//            mParentView.addView(mBrushDrawingView);
//        }
        mAddedViews.clear();
        mRedoViews.clear();
//        clearBrushAllViews();
    }

    @UiThread
    public void clearHelperBox() {
        for (int i = 0; i < mParentView.getChildCount(); i++) {
            View childAt = mParentView.getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
            ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final OnSaveBitmap onSaveBitmap) {
        new AsyncTask<String, String, Bitmap>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                clearHelperBox();
                mParentView.setDrawingCacheEnabled(false);
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                if (mParentView != null) {
                    mParentView.setDrawingCacheEnabled(true);
                    return BitmapUtils.removeTransparency(mParentView.getDrawingCache());
                } else {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    clearAllViews();
                    onSaveBitmap.onBitmapReady(bitmap);
                } else {
                    onSaveBitmap.onFailure(new Exception("Failed to load the bitmap"));
                }
            }

        }.execute();
    }

    private static String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = new String(Character.toChars(convertEmojiToInt));
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
    }

    /**
     * Check if any changes made need to save
     *
     * @return true if nothing is there to change
     */
    public boolean isCacheEmpty() {
        return mAddedViews.size() == 0 && mRedoViews.size() == 0;
    }


//    @Override
//    public void onViewAdd(BrushDrawingView brushDrawingView) {
//        if (mRedoViews.size() > 0) {
//            mRedoViews.remove(mRedoViews.size() - 1);
//        }
//        mAddedViews.add(brushDrawingView);
//        if (mOnPhotoEditorListener != null) {
//            mOnPhotoEditorListener.onAddViewListener(ViewType.BRUSH_DRAWING, mAddedViews.size());
//        }
//    }
//
//    @Override
//    public void onViewRemoved(BrushDrawingView brushDrawingView) {
//        if (mAddedViews.size() > 0) {
//            View removeView = mAddedViews.remove(mAddedViews.size() - 1);
//            if (!(removeView instanceof BrushDrawingView)) {
//                mParentView.removeView(removeView);
//            }
//            mRedoViews.add(removeView);
//        }
//        if (mOnPhotoEditorListener != null) {
//            mOnPhotoEditorListener.onRemoveViewListener(ViewType.BRUSH_DRAWING, mAddedViews.size());
//        }
//    }
//
//    @Override
//    public void onStartDrawing() {
//        if (mOnPhotoEditorListener != null) {
//            mOnPhotoEditorListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
//        }
//    }
//
//    @Override
//    public void onStopDrawing() {
//        if (mOnPhotoEditorListener != null) {
//            mOnPhotoEditorListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
//        }
//    }


    //<editor-fold desc="PhotoEditBuilder">
    public static class PhotoEditBuilder {

        private Context mContext;
        private RelativeLayout mParentView;
        private ImageView mImageView;
        private View mDeleteView;
//        private BrushDrawingView mBrushDrawingView;
        private Typeface mTextTypeface;
        private Typeface mEmojiTypeface;
        private boolean mIsTextPinchZoomable = true;

        public PhotoEditBuilder(Context context) {
            mContext = context;
        }

        public PhotoEditBuilder parentView(RelativeLayout parentView) {
            mParentView = parentView;
            return this;
        }

        public PhotoEditBuilder imageView(ImageView imageView) {
            mImageView = imageView;
            return this;
        }

//        public PhotoEditBuilder brushDrawingView(BrushDrawingView brushDrawingView) {
//            mBrushDrawingView = brushDrawingView;
//            return this;
//        }

        public PhotoEditBuilder textTypeface(Typeface textTypeface) {
            mTextTypeface = textTypeface;
            return this;
        }

        public PhotoEditBuilder emojiTypeface(Typeface emojiTypeface) {
            mEmojiTypeface = emojiTypeface;
            return this;
        }

        public PhotoEditBuilder isTextPinchZoomable(boolean isTextPinchZoomable) {
            mIsTextPinchZoomable = isTextPinchZoomable;
            return this;
        }

        public PhotoEditor buildPhotoEdit() {
            return new PhotoEditor(this);
        }
    }
    //</editor-fold>


    //<editor-fold desc="OnSaveBitmap">
    public interface OnSaveBitmap {
        void onBitmapReady(Bitmap saveBitmap);
        void onFailure(Exception e);
    }
    //</editor-fold>
}
