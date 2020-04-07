package com.wangrui.imagee;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */

public class TextEditorDialogFragment extends DialogFragment {

    private static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    private static final String EXTRA_INPUT_TEXT = "extra_input_text";
    private InputMethodManager mInputMethodManager;

    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                @NonNull String inputText) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        // 设置 Dialog 全屏且背景透明
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
            window.setLayout(width, height);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            window.setAttributes(windowParams);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_text_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText mEtText = view.findViewById(R.id.et_text);
        ImageView mIvSure = view.findViewById(R.id.iv_sure);

        LinearLayout mLlCancel = view.findViewById(R.id.ll_cancel);
        mLlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                dismiss();
            }
        });

        if (getArguments() != null) {
            String inputText = getArguments().getString(EXTRA_INPUT_TEXT);
            mEtText.setText(inputText);
            mEtText.requestFocus();
        }

        mEtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mListener != null) {
                    mListener.onTextChange(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mIvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onTextDone(mEtText.getText().toString().trim());
                }
                hideKeyboard();
                dismiss();
            }
        });

        showKeyboard();
    }

    private void showKeyboard() {
        if (mInputMethodManager != null) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void hideKeyboard() {
        if (mInputMethodManager != null && getView() != null) {
            mInputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    //<editor-fold desc="listener">
    private OnTextEditorDialogListener mListener;

    public void setOnTextEditorDialogListener(OnTextEditorDialogListener listener) {
        mListener = listener;
    }

    public interface OnTextEditorDialogListener {
        void onTextChange(String inputText);
        void onTextDone(String inputText);
    }
    //</editor-fold>
}
